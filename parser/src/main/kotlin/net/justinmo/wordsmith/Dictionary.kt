package net.justinmo.wordsmith

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.math.max

class Dictionary {

  constructor(language: Language) {
    filePath = "${language.code}-words.txt"
    init()
  }

  constructor(filePath: String) {
    this.filePath = filePath
    init()
  }

  private val filePath: String
  private lateinit var words: Set<String>
  private lateinit var letters: Map<Int, String>
  var maxWordLenght = 0
    private set

  private fun init() {
    val bufferedReader = BufferedReader(InputStreamReader(FileInputStream(filePath), StandardCharsets.UTF_8))
    val wordsBuilder = mutableSetOf<String>()
    val lettersBuilder = mutableMapOf<Int, String>()
    var line: String? = bufferedReader.readLine()?.trim()?.lowercase()
    var lineNumber = 0
    while (line != null) {
      when {
        line.isEmpty() -> println("line %d is empty".format(lineNumber))
        wordsBuilder.contains(line) -> println("%s duplicate on line %d".format(line, lineNumber))
        isOnlyRepeatingLetters(line) -> println("%s only repeating letter on line %d".format(line, lineNumber))
        endsWithNonLetter(line) -> println("%s ends with non-letter on line %d".format(line, lineNumber))
        startsWithAa(line) -> println("%s starts with aa on line %d".format(line, lineNumber))
        else -> {
          wordsBuilder.add(line)
          maxWordLenght = max(maxWordLenght, line.length)
          for (i in line.indices) {
            if (!lettersBuilder.containsKey(line.codePointAt(i))) {
              lettersBuilder.put(line.codePointAt(i), line.substring(i, i+1))
            }
          }
        }
      }
      line = bufferedReader.readLine()?.trim()?.lowercase()
      lineNumber += 1
    }
    bufferedReader.close()
    words = wordsBuilder.toSet()
    letters = lettersBuilder.toMap()
  }

  fun contains(word: String) = words.contains(word)

  fun words() = words.asIterable()

  fun letter(codePoint: Int) = letters[codePoint] ?: error("Letter not found")

  private fun isOnlyRepeatingLetters(word: String) =
      word.length > 1 && word.all { it == word[0] }

  private fun endsWithNonLetter(word: String) = !word.last().isLetter()

  private fun startsWithAa(word: String) = word.startsWith("aa")
}
