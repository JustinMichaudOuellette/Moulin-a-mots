package ca.justinmo.word.generator.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary")
data class DictionaryWord(
        @PrimaryKey val word: String
)