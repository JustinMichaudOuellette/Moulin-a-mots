package net.justinmo.wordsmith

import java.util.ArrayDeque
import kotlin.random.Random

class LetterChain(val letter: String, val language: Language) {

  private val nextLetters = mutableMapOf<String, LetterChain>()
  var count = 0
    private set
  var totalCount = 0
    private set

  fun parseWordNextLetters(dictionary: Dictionary, word: String, i: Int) {
    val letter = if (i < word.length) dictionary.letter(word.codePointAt(i)) else "$"
    val previousLetters = MutableList(language.depth) {"^"}
    for (previousLetterIndex in previousLetters.indices) {
      val letterIndex = i - language.depth + previousLetterIndex
      if (letterIndex >= 0) {
        previousLetters[previousLetterIndex] = dictionary.letter(word.codePointAt(letterIndex))
      }
    }
    var letterChain = getOrCreateLetterChain(previousLetters[0])
    letterChain.incrementCount()
    for (previousLetterIndex in 1 until previousLetters.size) {
      letterChain = letterChain.getOrCreateLetterChain(previousLetters[previousLetterIndex])
      letterChain.incrementCount()
    }
    letterChain = letterChain.getOrCreateLetterChain(letter)
    letterChain.incrementCount()
    if (i < word.length) {
      parseWordNextLetters(dictionary, word, i + 1)
    }
  }

  fun parseWordPreviousLetters(dictionary: Dictionary, word: String, i: Int) {
    val letter = if (i >= 0) dictionary.letter(word.codePointAt(i)) else "^"
    val previousLetters = MutableList(language.depth) {"$"}
    for (previousLetterIndex in previousLetters.indices) {
      val letterIndex = i + previousLetterIndex + 1
      if (letterIndex < word.length) {
        previousLetters[previousLetterIndex] = dictionary.letter(word.codePointAt(letterIndex))
      }
    }
    var letterChain = getOrCreateLetterChain(previousLetters[0])
    letterChain.incrementCount()
    for (previousLetterIndex in 1 until previousLetters.size) {
      letterChain = letterChain.getOrCreateLetterChain(previousLetters[previousLetterIndex])
      letterChain.incrementCount()
    }
    letterChain = letterChain.getOrCreateLetterChain(letter)
    letterChain.incrementCount()
    if (i >= 0) {
      parseWordPreviousLetters(dictionary, word, i - 1)
    }
  }

  private fun incrementCount() {
    count++
  }

  private fun getOrCreateLetterChain(letter: String)
      = nextLetters.getOrPut(letter, { LetterChain(letter, language) })

  fun countTotals() {
    var total = 0
    for (letterChain in nextLetters.values) {
      total += letterChain.count
      letterChain.countTotals()
    }
    totalCount = total
  }

  fun generateWord(dictionary: Dictionary, random: Random, base: String = "", isPrefix: Boolean = true): String {
    var word = if (isPrefix) generateWordCandidateWithPrefix(dictionary, random, base) else generateWordCandidateWithSuffix(dictionary, random, base)
    var tries = 0
    while (word.isEmpty() && tries < MAX_RETRIES) {
      word = if (isPrefix) generateWordCandidateWithPrefix(dictionary, random, base) else generateWordCandidateWithSuffix(dictionary, random, base)
      tries++
    }
    return word
  }

  private fun generateWordCandidateWithSuffix(dictionary: Dictionary, random: Random, suffix: String): String {
    val word = StringBuilder(suffix)
    val previousLetters = ArrayDeque<String>(language.depth)
    for (i in 0 until language.depth) {
      val index = suffix.length + (language.depth - i)
      if (index < suffix.length) {
        previousLetters.add(suffix.substring(index, index + 1))
      } else {
        previousLetters.add("$")
      }
    }
    var previousLetter = findNextLetter(previousLetters, word, random)

    while (previousLetter != null && !previousLetter.letter.equals("^")) {
      word.insert(0, previousLetter.letter)
      previousLetters.removeLast()
      previousLetters.addFirst(previousLetter.letter)
      previousLetter = findNextLetter(previousLetters, word, random)
    }
    if (dictionary.contains(word.toString())) {
      return ""
    }
    return word.toString()
  }

  private fun generateWordCandidateWithPrefix(dictionary: Dictionary, random: Random, prefix: String): String {
    val word = StringBuilder(prefix)
    val previousLetters = ArrayDeque<String>(language.depth)
    for (i in 0 until language.depth) {
      val index = prefix.length - (language.depth - i)
      if (index >= 0) {
        previousLetters.add(prefix.substring(index, index + 1))
      } else {
        previousLetters.add("^")
      }
    }
    var nextLetter = findNextLetter(previousLetters, word, random)

    while (nextLetter != null && !nextLetter.letter.equals("$")) {
      word.append(nextLetter.letter)
      previousLetters.removeFirst()
      previousLetters.addLast(nextLetter.letter)
      nextLetter = findNextLetter(previousLetters, word, random)
    }
    if (dictionary.contains(word.toString())) {
      return ""
    }
    return word.toString()
  }

  private fun findNextLetter(previousLetters: ArrayDeque<String>, word: StringBuilder, random: Random): LetterChain? {
    var letterChain = this
    for (i in 0 until previousLetters.size) {
      try {
        letterChain = letterChain.nextLetters[previousLetters.elementAt(i)]!!
      } catch (e: KotlinNullPointerException) {
        println("No letter ${previousLetters.elementAt(i)} at index $i for sequence ${previousLetters.joinToString(",")} in word $word")
        return null
      }
    }
    val randomFloat = random.nextFloat()
    var cumulativeChance = 0f
    for (nextLetterCandidate in letterChain.nextLetters.values) {
      cumulativeChance += nextLetterCandidate.count.toFloat() / letterChain.totalCount.toFloat()
      if (cumulativeChance >= randomFloat) {
        return nextLetterCandidate
      }
    }
    error("Could not find next letter.")
  }

  fun nextLetters() = nextLetters.values

  override fun toString(): String {
    return letter
  }

  companion object {
    const val MAX_RETRIES = 50
  }
}
