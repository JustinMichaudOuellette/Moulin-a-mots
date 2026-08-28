package ca.justinmo.word.generator.app.data

import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun generateWordTask(): WordInfo
    fun exploreWordsWithPrefix(prefix: String, limit: Int): Flow<WordInfo>
    fun exploreWordsWithSuffix(suffix: String, limit: Int): Flow<WordInfo>
    suspend fun removeFavorite(word: String)
    suspend fun addFavorite(word: String)
    fun geFavoritesStream(): Flow<List<WordInfo>>
}