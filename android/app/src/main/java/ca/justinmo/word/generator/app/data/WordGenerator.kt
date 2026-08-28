package ca.justinmo.word.generator.app.data

import ca.justinmo.word.generator.app.main.WordGeneratorApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class WordGenerator(seed: Long, private val letterChainDao: LetterChainDao, private val wordDao: WordDao) {

    private val random = Random(seed)
    private val settingsRepository = WordGeneratorApplication.settingsRepository

    suspend fun generate(randomOverride: Random? = null): WordInfo {
        var word: String
        var existingWord: ExistingWord?
        var attempts = 0
        val rng = randomOverride ?: random
        val temp = settingsRepository.temperature.value
        do {
            val firstLetters = letterChainDao.nextLetters("^".times(CHAIN_SIZE))
            val firstLetter = firstLetters.weightedRandom(rng, temp)
            val wordSoFar = String(intArrayOf(firstLetter.next_letter), 0 , 1)
            word = finishRandomWord(rng, wordSoFar, temp)
            existingWord = wordDao.lookUpWord(cleanAccentsAndTrailingS(word))
            attempts++
        } while ((existingWord.isInDictionary || (word.length < MIN_WORD_LENGTH)) && (attempts < 100))
        return WordInfo(word, checkNotNull(existingWord.isFavorite))
    }

    suspend fun generateWithPrefix(prefix: String, randomOverride: Random? = null): WordInfo {
        var word: String
        var existingWord: ExistingWord?
        var attempts = 0
        val rng = randomOverride ?: random
        val temp = settingsRepository.temperature.value
        do {
            word = finishRandomWord(rng, prefix, temp)
            existingWord = wordDao.lookUpWord(cleanAccentsAndTrailingS(word))
            attempts++
        } while ((existingWord.isInDictionary || (word.length < MIN_WORD_LENGTH)) && (attempts < 100))
        return WordInfo(word, checkNotNull(existingWord.isFavorite))
    }

    suspend fun generateWithSuffix(suffix: String, randomOverride: Random? = null): WordInfo {
        val rng = randomOverride ?: random
        var attempts = 0
        while (attempts < 100) {
            val word = generateBackward(suffix, rng)
            if (word != null) {
                val existingWord = wordDao.lookUpWord(cleanAccentsAndTrailingS(word))
                if (word.length >= MIN_WORD_LENGTH && !existingWord.isInDictionary) {
                    return WordInfo(word, checkNotNull(existingWord.isFavorite))
                }
            }
            attempts++
        }
        return WordInfo(word = suffix, isFavorite = false) 
    }

    private suspend fun generateBackward(suffix: String, rng: Random, startOverride: String? = null): String? {
        var currentChain = if (startOverride != null) {
            startOverride
        } else {
            val lastOverlap = suffix.takeLast(CHAIN_SIZE).padStart(CHAIN_SIZE, '^')
            val startOptions = letterChainDao.previousChainsLike(lastOverlap, DOLLAR_CODE)
            if (startOptions.isEmpty()) return null
            startOptions.weightedRandom(rng).chain
        }
        
        var word = suffix
        
        if (suffix.length > CHAIN_SIZE && startOverride == null) {
            val prefixOfSuffix = suffix.dropLast(CHAIN_SIZE)
            var checkChain = currentChain
            for (i in prefixOfSuffix.indices.reversed()) {
                val charToMatch = prefixOfSuffix[i]
                val prevOptions = letterChainDao.previousChainsLike("_" + checkChain.take(CHAIN_SIZE - 1), checkChain.last().code)
                val matching = prevOptions.find { it.chain.endsWith(checkChain.take(CHAIN_SIZE - 1)) && it.chain.startsWith(charToMatch) }
                if (matching == null) return null
                checkChain = matching.chain
            }
            currentChain = checkChain
        }

        var attempts = 0
        while (currentChain != "^".times(CHAIN_SIZE) && attempts < 50) {
            val prevOptions = letterChainDao.previousChainsLike("_" + currentChain.take(CHAIN_SIZE - 1), currentChain.last().code)
            if (prevOptions.isEmpty()) return null
            
            val chosen = prevOptions.weightedRandom(rng)
            val prevChar = chosen.chain[0]
            if (prevChar != '^') {
                word = prevChar + word
            }
            currentChain = chosen.chain
            attempts++
        }
        
        return if (currentChain == "^".times(CHAIN_SIZE)) word else null
    }

    fun exploreWithPrefix(prefix: String, limit: Int): Flow<WordInfo> = flow {
        val emitted = mutableSetOf<String>()
        val rng = Random(prefix.hashCode().toLong())
        
        val chain = prefix.firstLetters(CHAIN_SIZE)
        val nextLetters = letterChainDao.nextLetters(chain)
        
        for (nextLetter in nextLetters.sortedBy { it.next_letter }) {
            if (emitted.size >= limit) break
            val nextChar = String(intArrayOf(nextLetter.next_letter), 0, 1)
            if (nextChar == "$") continue
            
            var attempts = 0
            while (attempts < 10) {
                val wordInfo = generateWithPrefix(prefix + nextChar, rng)
                if (emitted.add(wordInfo.word)) {
                    emit(wordInfo)
                    break
                }
                attempts++
            }
        }
        
        var attempts = 0
        while (emitted.size < limit && attempts < 500) {
            val wordInfo = generateWithPrefix(prefix, rng)
            if (emitted.add(wordInfo.word)) {
                emit(wordInfo)
            }
            attempts++
        }
    }

    fun exploreWithSuffix(suffix: String, limit: Int): Flow<WordInfo> = flow {
        val emitted = mutableSetOf<String>()
        val rng = Random(suffix.hashCode().toLong())
        
        val lastOverlap = suffix.takeLast(CHAIN_SIZE).padStart(CHAIN_SIZE, '^')
        val startOptions = letterChainDao.previousChainsLike(lastOverlap, DOLLAR_CODE)
        
        for (start in startOptions.sortedBy { it.chain }) {
            if (emitted.size >= limit) break
            
            var branchAttempts = 0
            while (branchAttempts < 5 && emitted.size < limit) {
                val word = generateBackward(suffix, rng, start.chain)
                if (word != null && emitted.add(word)) {
                    val existingWord = wordDao.lookUpWord(cleanAccentsAndTrailingS(word))
                    if (word.length >= MIN_WORD_LENGTH && !existingWord.isInDictionary) {
                        emit(WordInfo(word, existingWord.isFavorite))
                    }
                }
                branchAttempts++
            }
        }

        var attempts = 0
        while (emitted.size < limit && attempts < limit * 10) {
            val word = generateBackward(suffix, rng)
            if (word != null && emitted.add(word)) {
                val existingWord = wordDao.lookUpWord(cleanAccentsAndTrailingS(word))
                if (word.length >= MIN_WORD_LENGTH && !existingWord.isInDictionary) {
                    emit(WordInfo(word, existingWord.isFavorite))
                }
            }
            attempts++
        }
    }

    suspend fun finishRandomWord(random: Random, wordSoFar: String, temperature: Float = 1.0f): String {
        val nextLetters = letterChainDao.nextLetters(wordSoFar.firstLetters(CHAIN_SIZE))
        if ((wordSoFar.length >= MAX_WORD_LENGTH) && nextLetters.any { it.next_letter == DOLLAR_CODE }) {
            return wordSoFar
        }
        val nextLetterCode = nextLetters.weightedRandom(random, temperature).next_letter
        val nextLetterString = String(intArrayOf(nextLetterCode), 0 , 1)
        if (nextLetterString == "$") {
            return wordSoFar
        }
        val wordWithNextLetter = "$wordSoFar$nextLetterString"
        return finishRandomWord(random, wordWithNextLetter, temperature)
    }

    private fun cleanAccentsAndTrailingS(word: String): String {
        var cleanWord = word.removeSuffix("s")
        ACCENTS_REGEX.forEach { (regex, letter) ->
            cleanWord = cleanWord.replace(regex, letter)
        }
        return cleanWord
    }
}

const val CHAIN_SIZE = 4
const val MIN_WORD_LENGTH = 4
const val MAX_WORD_LENGTH = 15
const val DOLLAR_CODE = 0x24

val ACCENTS_REGEX: Map<Regex, String> = mapOf(
    Regex("[àáâãäå]") to "a",
    Regex("ç") to "c",
    Regex("[èéêë]") to "e",
    Regex("[ìíîï]") to "i",
    Regex("[òóôõö]") to "o",
    Regex("[ùúûü]") to "u",
    Regex("[ýÿ]") to "y",
)

fun CharSequence.times(count: Int): String {
    val sb = StringBuilder(count * length)
    repeat(count) {
        sb.append(this)
    }
    return sb.toString()
}

fun CharSequence.firstLetters(count: Int): String =
    StringBuilder().also {
        for (i in 1..count) {
            it.append(lastLetter(count - i))
        }
    }.toString()

fun CharSequence.lastLetter(offset: Int = 0) =
    if (length > offset)
        substring(length - offset - 1, length - offset)
    else
        "^"