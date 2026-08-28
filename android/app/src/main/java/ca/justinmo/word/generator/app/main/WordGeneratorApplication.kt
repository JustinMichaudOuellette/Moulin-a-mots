

package ca.justinmo.word.generator.app.main

import android.app.Application
import androidx.room.Room
import ca.justinmo.word.generator.app.data.DefaultWordRepository
import ca.justinmo.word.generator.app.data.LetterChainDao
import ca.justinmo.word.generator.app.data.LetterChainDatabase
import ca.justinmo.word.generator.app.data.SettingsRepository
import ca.justinmo.word.generator.app.data.WordDao
import ca.justinmo.word.generator.app.data.WordDatabase
import ca.justinmo.word.generator.app.data.WordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.tukaani.xz.XZInputStream

class WordGeneratorApplication : Application() {

    companion object {
        lateinit var wordRepository: WordRepository
        lateinit var wordDao: WordDao
        lateinit var letterChainDao: LetterChainDao
        lateinit var defaultDispatcher: CoroutineDispatcher
        lateinit var applicationScope: CoroutineScope
        lateinit var settingsRepository: SettingsRepository
    }

    override fun onCreate() {
        super.onCreate()

        settingsRepository = SettingsRepository(this)
        defaultDispatcher = Dispatchers.Default
        applicationScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

        val wordDatabase = Room.databaseBuilder(
            applicationContext,
            WordDatabase::class.java,
            "words-fr-db",
        )
            .createFromInputStream { XZInputStream(assets.open("words_fr.db.xz")) }
            .build()
        wordDao = wordDatabase.wordDao()

        val letterChainDatabase = Room.databaseBuilder(
            applicationContext,
            LetterChainDatabase::class.java,
            "letter-chains-fr-db",
        )
            .createFromInputStream { XZInputStream(assets.open("letter_chains_fr.db.xz")) }
            .build()
        letterChainDao = letterChainDatabase.letterChainDao()

        wordRepository = DefaultWordRepository()
    }
}
