package ca.justinmo.word.generator.app.data

import ca.justinmo.word.generator.app.main.WordGeneratorApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DefaultWordRepository : WordRepository {

    // Accessing dependencies directly from the global Application state
    private val letterChainSource = WordGeneratorApplication.letterChainDao
    private val wordsSource = WordGeneratorApplication.wordDao
    private val dispatcher = WordGeneratorApplication.defaultDispatcher

    private val wordGenerator: WordGenerator = WordGenerator(System.currentTimeMillis(), letterChainSource, wordsSource)

    override suspend fun generateWordTask(): WordInfo {
        return withContext(dispatcher) {
            wordGenerator.generate()
        }
    }

    override fun exploreWordsWithPrefix(prefix: String, limit: Int): Flow<WordInfo> {
        return wordGenerator.exploreWithPrefix(prefix, limit)
    }

    override fun exploreWordsWithSuffix(suffix: String, limit: Int): Flow<WordInfo> {
        return wordGenerator.exploreWithSuffix(suffix, limit)
    }

    override suspend fun removeFavorite(word: String) {
        wordsSource.deleteFavoriteWord(FavoriteWord(word))
    }

    override suspend fun addFavorite(word: String) {
        wordsSource.insertFavoriteWord(FavoriteWord(word))
    }

    override fun geFavoritesStream(): Flow<List<WordInfo>> {
        return wordsSource.favorites()
    }
}