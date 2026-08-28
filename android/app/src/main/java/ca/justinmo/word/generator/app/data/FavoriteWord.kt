package ca.justinmo.word.generator.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteWord(
        @PrimaryKey val word: String
)