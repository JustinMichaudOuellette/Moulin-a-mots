

package ca.justinmo.word.generator.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the chain of letters table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [LetterChain::class, Letter::class], version = 1, exportSchema = false)
abstract class LetterChainDatabase : RoomDatabase() {

    abstract fun letterChainDao(): LetterChainDao
}
