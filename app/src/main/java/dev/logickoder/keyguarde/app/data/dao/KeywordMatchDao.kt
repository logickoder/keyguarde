package dev.logickoder.keyguarde.app.data.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import dev.logickoder.keyguarde.app.data.model.AppMatchCount
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
     * Count matches newer than [since], limited to [apps] unless [allApps] is set.
     */
    @Query("SELECT COUNT(*) FROM keyword_matches WHERE timestamp > :since AND (:allApps OR app IN (:apps))")
    fun countSince(since: LocalDateTime, allApps: Boolean, apps: Set<String>): Flow<Int>

    /**
     * How many matches each app has.
     */
    @Query("SELECT app, COUNT(*) AS count FROM keyword_matches GROUP BY app")
    fun countByApp(): Flow<List<AppMatchCount>>

    @Query("DELETE FROM keyword_matches WHERE rowid IN (:ids)")
    suspend fun delete(ids: List<Long>)

    /**
     * Fetch KeywordMatch entries filtered by a specific keyword.
     */
    @Query("SELECT * FROM keyword_matches WHERE :keyword IN (keywords) ORDER BY timestamp DESC")
    fun getByKeyword(keyword: String): Flow<List<KeywordMatch>>

    /**
     * Fetch matches from [apps] (or every app when [allApps] is set), optionally searched by [query].
     */
    @Query(
        """
        SELECT * FROM keyword_matches
        WHERE (:allApps OR app IN (:apps))
        AND (:query IS NULL OR rowid IN (SELECT rowid FROM keyword_matches_fts WHERE keyword_matches_fts MATCH :query))
        ORDER BY timestamp DESC
        """
    )
    fun getMatches(allApps: Boolean, apps: Set<String>, query: String?): PagingSource<Int, KeywordMatch>

    /**
     * Delete all KeywordMatch entries from the database.
     *
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM keyword_matches")
    suspend fun clear(): Int
}
