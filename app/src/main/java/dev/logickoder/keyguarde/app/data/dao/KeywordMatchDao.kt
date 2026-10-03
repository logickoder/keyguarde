package dev.logickoder.keyguarde.app.data.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

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
     * Delete a specific KeywordMatch entry from the database.
     */
    /**
     * Fetch matches by id, e.g. to keep a copy before deleting them so the delete can be undone.
     */
    @Query("SELECT * FROM keyword_matches WHERE rowid IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<KeywordMatch>

    /**
     * Fetch every match, e.g. to keep a copy before clearing so it can be undone.
     */
    @Query("SELECT * FROM keyword_matches")
    suspend fun getAll(): List<KeywordMatch>

    /**
     * Put back matches that were deleted, keeping their original ids.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restore(matches: List<KeywordMatch>)

    /**
     * Count matches newer than [since], optionally limited to one app.
     */
    @Query("SELECT COUNT(*) FROM keyword_matches WHERE timestamp > :since AND (:app IS NULL OR app = :app)")
    fun countSince(since: LocalDateTime, app: String?): Flow<Int>

    @Query("DELETE FROM keyword_matches WHERE rowid IN (:ids)")
    suspend fun delete(ids: List<Long>)

    /**
     * Fetch KeywordMatch entries filtered by a specific keyword.
     */
    @Query("SELECT * FROM keyword_matches WHERE :keyword IN (keywords) ORDER BY timestamp DESC")
    fun getByKeyword(keyword: String): Flow<List<KeywordMatch>>

    /**
     * Fetch KeywordMatch entries filtered by a specific app if provided.
     */
    @Query(
        """
        SELECT * FROM keyword_matches
        WHERE (:app IS NULL OR app = :app)
        AND (:query IS NULL OR rowid IN (SELECT rowid FROM keyword_matches_fts WHERE keyword_matches_fts MATCH :query))
        ORDER BY timestamp DESC
        """
    )
    fun getMatches(app: String?, query: String?): PagingSource<Int, KeywordMatch>

    /**
     * Delete all KeywordMatch entries from the database.
     *
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM keyword_matches")
    suspend fun clear(): Int
}
