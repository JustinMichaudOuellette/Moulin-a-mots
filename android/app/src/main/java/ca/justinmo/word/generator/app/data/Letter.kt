package ca.justinmo.word.generator.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letters")
data class Letter(@PrimaryKey val letter: String)
