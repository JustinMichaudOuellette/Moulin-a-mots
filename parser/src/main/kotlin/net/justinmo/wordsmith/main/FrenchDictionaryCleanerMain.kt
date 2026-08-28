package net.justinmo.wordsmith.main

import net.justinmo.wordsmith.FileLinesParser
import net.justinmo.wordsmith.FileLinesWriter
import net.justinmo.wordsmith.Language
import net.justinmo.wordsmith.SetUtil.toSortedArray

/**
 * Cleans a word list by removing trailing s and replacing accents with base letter.
 */
fun main() {
  var words = mutableSetOf<String>()
  FileLinesParser("fr-words.txt").parse { line: String, _: Int ->
    words.add(line)
  }
  words = removeTrailingS(words)
  words = replaceAccents(words)
  FileLinesWriter("dictionary_fr_clean.txt").run {
    write(words.toSortedArray(Language.FR))
    close()
  }
}

private fun removeTrailingS(words: MutableSet<String>): MutableSet<String> {
  val result = mutableSetOf<String>()
  words.forEach { word ->
    var updatedWord = word.lowercase()
    if (updatedWord.endsWith("s")) {
      updatedWord = updatedWord.removeSuffix("s")
    }
    result.add(updatedWord)
  }
  return result
}

private fun replaceAccents(words: MutableSet<String>): MutableSet<String> {
  val result = mutableSetOf<String>()
  words.forEach { word ->
    var updatedWord = word
    ACCENTS_REGEX.forEach { (regex, letter) ->
      updatedWord = updatedWord.replace(regex, letter)
    }
    result.add(updatedWord)
  }
  return result
}

val ACCENTS_REGEX: Map<Regex, String> = mapOf(
  Regex("[àáâãäå]") to "a",
  Regex("[ç]") to "c",
  Regex("[èéêë]") to "e",
  Regex("[ìíîï]") to "i",
  Regex("[òóôõö]") to "o",
  Regex("[ùúûü]") to "u",
  Regex("[ýÿ]") to "y",
)