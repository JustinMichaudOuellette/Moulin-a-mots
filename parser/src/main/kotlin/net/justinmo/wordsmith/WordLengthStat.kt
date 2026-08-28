package net.justinmo.wordsmith

fun main() {
    WordLengthStat(Language.FR).printStats()
}

class WordLengthStat(val language: Language) {

    val dictionary = Dictionary(language)
    val lengthCount = IntArray(dictionary.maxWordLenght+1)

    init {
        for (word in dictionary.words()) {
            lengthCount[word.length]++
        }
    }

    fun printStats() {
        println(language.code)
        println("length\toccurrence")
        for (i in 1 until lengthCount.size) {
            println("$i\t\t${lengthCount[i]}")
        }
        println()
    }
}
