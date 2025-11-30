package dev.logickoder.keyguarde.app.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    suspend fun delete(vararg match: KeywordMatch)

    /**
     * Fetch KeywordMatch entries filtered by a specific keyword.
     */
    @Query("SELECT * FROM keyword_matches WHERE :keyword IN (keywords) ORDER BY timestamp DESC")
    fun getByKeyword(keyword: String): Flow<List<KeywordMatch>>

    /**
     * Fetch KeywordMatch entries filtered by a specific app if provided.
     */
    @Query("SELECT * FROM keyword_matches WHERE :app IS NULL OR app = :app ORDER BY timestamp DESC")
    fun getMatches(app: String?): PagingSource<Int, KeywordMatch>

    /**
     * Get the total count of KeywordMatch entries.
     */
    @Query("SELECT COUNT(*) FROM keyword_matches")
    suspend fun getSize(): Long

    /**
     * Delete all KeywordMatch entries from the database.
     */
    @Query("DELETE FROM keyword_matches")
    suspend fun clear()
}