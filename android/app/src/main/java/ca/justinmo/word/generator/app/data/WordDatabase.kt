

package ca.justinmo.word.generator.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the existing words and favorite words table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [DictionaryWord::class, FavoriteWord::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
}
