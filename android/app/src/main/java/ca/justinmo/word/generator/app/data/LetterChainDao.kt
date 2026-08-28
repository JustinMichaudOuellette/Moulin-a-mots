package ca.justinmo.word.generator.app.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LetterChainDao {

    @Query("""
        SELECT next_letter, count
        FROM letter_chains 
        WHERE chain = :chain
    """)
    suspend fun nextLetters(chain: String): Array<NextLetter>

    @Query("""
        SELECT next_letter, count 
        FROM letter_chains 
        WHERE chain LIKE :chain
    """)
    suspend fun nextLettersLike(chain: String): Array<NextLetter>

    @Query("""
        SELECT chain, count
        FROM letter_chains
        WHERE next_letter = :lastLetter AND chain LIKE :chainLike
    """)
    suspend fun previousChainsLike(chainLike: String, lastLetter: Int): Array<PreviousLetter>
}