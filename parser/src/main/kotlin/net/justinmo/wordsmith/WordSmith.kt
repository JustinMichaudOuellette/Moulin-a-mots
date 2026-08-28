package net.justinmo.wordsmith

import java.text.Collator
import java.util.*
import kotlin.random.Random

fun main() {
    WordSmith(Language.FR).generateWords(false)
}

class WordSmith(language: Language) {

    val dictionary: Dictionary = Dictionary(language)
    val rootNextLetterChain: LetterChain = LetterChain("/", language)
    val rootPreviousLetterChain: LetterChain = LetterChain("/", language)

    init {
        for (word in dictionary.words()) {
            rootNextLetterChain.parseWordNextLetters(dictionary, word, 0)
            rootPreviousLetterChain.parseWordPreviousLetters(dictionary, word, word.length - 1)
        }
        rootNextLetterChain.countTotals()
        rootPreviousLetterChain.countTotals()
    }

    fun generateNextWords(base: String, random: Random, maxCount: Int): List<String> {
        val startTime = System.nanoTime()
        val words = mutableSetOf<String>()
        do {
            words.add(rootNextLetterChain.generateWord(dictionary, random, base))
        } while ((System.nanoTime() - startTime) < TIME_FOR_GENERATING_WORDS_NS && words.size < maxCount)
        return words.toList().sortedWith(Collator.getInstance(Locale.FRENCH))
    }

    fun generateWords(usePrefix: Boolean = true) {
        while (true) {
            if (usePrefix)
                println(rootNextLetterChain.generateWord(dictionary, Random.Default))
            else
                println(rootPreviousLetterChain.generateWord(dictionary, Random.Default, "", false))
            Thread.sleep(1000)
        }
    }

    companion object {
        const val TIME_FOR_GENERATING_WORDS_NS = 100 * 1000000
    }
}
