package net.justinmo.wordsmith

import java.io.File
import java.sql.DriverManager

fun main() {
  SQLExport(Language.FR).export()
}

class SQLExport(private val language: Language) {

    class LetterChainRow(
        val letters: String,
        val nextLetter: Int,
        val count: Int)

    companion object {
        const val MIN_WORD_LENGTH = 4
    }

    private val wordSmith = WordSmith(language)
    private val dictionary =
        Dictionary("${language.code}-words.txt")
    private val letterChainRows = mutableListOf<LetterChainRow>()

    fun export() {
        for (letterChain in wordSmith.rootNextLetterChain.nextLetters()) {
            exportNextLetters(letterChain, language.depth, StringBuilder())
        }

        exportWordsDb()
        exportLetterChainsDb()
    }

    private fun exportWordsDb() {
        val dbFile = File("words_${language.code}.db")
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(
                    """
                    DROP TABLE IF EXISTS dictionary;
                    CREATE TABLE dictionary (
                      word TEXT NOT NULL PRIMARY KEY);
                    DROP TABLE IF EXISTS favorites;
                    CREATE TABLE favorites (
                      word TEXT NOT NULL PRIMARY KEY);
                    """.trimIndent()
                )
            }
            val words = dictionary.words()
                .map { it.trimEnd('s') }
                .filter { it.length >= MIN_WORD_LENGTH }
                .toSet()
            connection.autoCommit = false
            connection.prepareStatement("INSERT INTO dictionary VALUES (?)").use { insert ->
                var batchCount = 0
                for (word in words) {
                    insert.setString(1, word)
                    insert.addBatch()
                    if (++batchCount % 50_000 == 0) insert.executeBatch()
                }
                insert.executeBatch()
            }
            connection.commit()
            connection.autoCommit = true
            connection.createStatement().use { statement -> statement.executeUpdate("VACUUM") }
        }
        println("Created ${dbFile.name}")
        compressToXz(dbFile)
    }

    private fun exportLetterChainsDb() {
        val dbFile = File("letter_chains_${language.code}.db")
        DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(
                    """
                    DROP TABLE IF EXISTS letter_chains;
                    CREATE TABLE letter_chains (
                      chain TEXT NOT NULL,
                      next_letter INTEGER NOT NULL,
                      count INTEGER NOT NULL,
                      PRIMARY KEY (chain, next_letter));
                    DROP TABLE IF EXISTS letters;
                    CREATE TABLE letters (letter TEXT NOT NULL PRIMARY KEY);
                    """.trimIndent()
                )
            }
            val letters = mutableSetOf<Int>()
            connection.autoCommit = false
            connection.prepareStatement("INSERT INTO letter_chains VALUES (?, ?, ?)").use { insert ->
                var batchCount = 0
                for (letterChainRow in letterChainRows) {
                    insert.setString(1, letterChainRow.letters)
                    insert.setInt(2, letterChainRow.nextLetter)
                    insert.setInt(3, letterChainRow.count)
                    insert.addBatch()
                    if (++batchCount % 50_000 == 0) insert.executeBatch()
                    letters.add(letterChainRow.nextLetter)
                }
                insert.executeBatch()
            }
            connection.prepareStatement("INSERT INTO letters VALUES (?)").use { insert ->
                var batchCount = 0
                for (letter in letters) {
                    insert.setString(1, letter.codePointToString())
                    insert.addBatch()
                    if (++batchCount % 50_000 == 0) insert.executeBatch()
                }
                insert.executeBatch()
            }
            connection.commit()
            connection.autoCommit = true
            connection.createStatement().use { statement -> statement.executeUpdate("VACUUM") }
        }
        println("Created ${dbFile.name}")
        compressToXz(dbFile)
    }

    private fun compressToXz(dbFile: File) {
        val xzFile = File("${dbFile.name}.xz")
        XzUtils.compressToXz(dbFile, xzFile)
        println("Created ${xzFile.name}")
    }

    private fun exportNextLetters(letterChain: LetterChain, depth: Int, letters: StringBuilder) {
        letters.append(letterChain.letter)
        if (depth == 1) {
            val lettersString = letters.toString()
            val nextLetters = letterChain.nextLetters().sortedWith { a, b ->
                when {
                    a.letter == "$" -> Integer.MIN_VALUE
                    b.letter == "$" -> Integer.MAX_VALUE
                    else -> b.count - a.count
                }
            }
            for (childLetter in nextLetters) {
                letterChainRows.add(LetterChainRow(
                    lettersString,
                    childLetter.letter.codePointAt(0),
                    childLetter.count))
            }
        } else {
            for (childLetter in letterChain.nextLetters()) {
                exportNextLetters(childLetter, depth - 1, letters)
            }
        }
        letters.deleteCharAt(letters.length - 1)
    }
}

fun Int.codePointToString() = String(intArrayOf(this), 0, 1)
