package ca.justinmo.word.generator.app.data

import androidx.room.Entity

@Entity(tableName = "letter_chains", primaryKeys = ["chain", "next_letter"])
data class LetterChain(
        val chain: String,
        val next_letter: Int,
        val count: Int,
)
