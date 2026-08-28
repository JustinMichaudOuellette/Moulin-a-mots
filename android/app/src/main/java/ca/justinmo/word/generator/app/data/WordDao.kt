

package ca.justinmo.word.generator.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the word tables.
 */
@Dao
interface WordDao {

    @Query("SELECT (SELECT COUNT(*) FROM favorites WHERE word IS :word) as isFavorite, (SELECT COUNT(*) FROM dictionary WHERE word IS :word) as isInDictionary")
    suspend fun lookUpWord(word: String): ExistingWord

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = FavoriteWord::class)
    suspend fun insertFavoriteWord(favoriteWord: FavoriteWord): Long

    @Delete
    suspend fun deleteFavoriteWord(favoriteWord: FavoriteWord): Int

    @Query("SELECT word, 1 AS isFavorite FROM favorites")
    fun favorites():  Flow<List<WordInfo>>
}
