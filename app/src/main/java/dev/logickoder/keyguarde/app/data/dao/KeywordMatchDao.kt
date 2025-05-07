package dev.logickoder.keyguarde.app.data.dao

import androidx.room.*
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordMatchDao {
    /**
     * Insert a KeywordMatch entry into the database.
     * If a conflict occurs, the insertion will be ignored.
     *
     * Returns the row ID, where -1 indicates a conflict.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(match: KeywordMatch): Long

    /**
     * Delete a specific KeywordMatch entry from the database.
     */
    @Delete
    suspend fun delete(match: KeywordMatch)

    /**
     * Fetch all KeywordMatch entries from the database.
     */
    @Query("SELECT * FROM keyword_matches ORDER BY timestamp DESC")
    fun getAll(): Flow<List<KeywordMatch>>

    /**
     * Fetch KeywordMatch entries filtered by a specific keyword.
     */
    @Query("SELECT * FROM keyword_matches WHERE :keyword IN (keywords) ORDER BY timestamp DESC")
    fun getByKeyword(keyword: String): Flow<List<KeywordMatch>>

    /**
     * Fetch KeywordMatch entries filtered by a specific app.
     */
    @Query("SELECT * FROM keyword_matches WHERE app = :app ORDER BY timestamp DESC")
    fun getByApp(app: String): Flow<List<KeywordMatch>>
}