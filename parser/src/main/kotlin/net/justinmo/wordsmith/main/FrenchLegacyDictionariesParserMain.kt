package net.justinmo.wordsmith.main

import net.justinmo.wordsmith.FileLinesParser
import net.justinmo.wordsmith.FileLinesWriter
import net.justinmo.wordsmith.Language
import net.justinmo.wordsmith.div
import net.justinmo.wordsmith.main.FrenchDictionariesParser.filter
import java.text.Collator
import java.util.*

fun main() {
  val words = mutableSetOf<String>()
  val allWords = mutableSetOf<String>()
  val basePath = "dictionaries" / "fr" / ""
  FileLinesParser("${basePath}verbs.txt").parse { line: String, _: Int ->
    val split = line.split(";")
    if (split.size == 2) {
      val word = split[0]
      allWords.add(word)
      if (split[1].equals("infi")) {
        words.add(word)
      }
    }
  }
  FileLinesParser("${basePath}pronouns.txt").parse { line: String, _: Int ->
    val word = line.split(";")[0]
    allWords.add(word)
    words.add(word)
  }
  FileLinesParser("${basePath}prepositions.txt").parse { line: String, _: Int ->
    val word = line.split(";")[0]
    allWords.add(word)
    words.add(word)
  }
  FileLinesParser("${basePath}nouns.txt").parse { line: String, _: Int ->
    val split = line.split(";")
    val word = split[0]
    allWords.add(word)
    if (split[split.size-1].equals("sg")) {
      words.add(word)
    }
  }
  FileLinesParser("${basePath}determiners.txt").parse { line: String, _: Int ->
    val word = line.split(";")[0]
    allWords.add(word)
    words.add(word)
  }
  FileLinesParser("${basePath}conjunctions.txt").parse { line: String, _: Int ->
    val word = line.split(";")[0]
    allWords.add(word)
    words.add(word)
  }
  FileLinesParser("${basePath}adverbs.txt").parse { line: String, _: Int ->
    allWords.add(line)
    words.add(line)
  }
  FileLinesParser("${basePath}adjectives.txt").parse { line: String, _: Int ->
    val split = line.split(";")
    val word = split[0]
    allWords.add(word)
    if (split[split.size-1].equals("sg")) {
      words.add(word)
    }
  }
  FileLinesWriter("dictionary_fr_old.txt").run {
    write(
        filter(words))
    close()
  }
  FileLinesWriter("full_dictionary_fr_v1.txt").run {
    write(
        filter(allWords))
    close()
  }
}

object FrenchDictionariesParser {

  fun filter(words: Set<String>) = words
      .filter {
        it.length >= Language.FR.depth
            && !it.contains('_')
      }
    .map {
      it.replace("œ", "oe")
        .replace("æ", "ae")
    }
    .sortedWith(Collator.getInstance(Locale.FRENCH))
}
