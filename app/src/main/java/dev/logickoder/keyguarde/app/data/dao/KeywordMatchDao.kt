package dev.logickoder.keyguarde.app.data.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import dev.logickoder.keyguarde.app.data.model.AppMatchCount
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import kotlinx.coroutines.flow.Flow
import java.time.Instant

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
    fun countSince(since: Instant, allApps: Boolean, apps: Set<String>): Flow<Int>

    /**
     * How many matches each app has.
     */
    @Query("SELECT app, COUNT(*) AS count FROM keyword_matches GROUP BY app")
    fun countByApp(): Flow<List<AppMatchCount>>

    /**
     * How many matches each keyword has and when it last matched. Keywords are stored as a JSON
     * list per match, and lowercased here so older matches saved in another case still count.
     */
    @Query(
        "SELECT lower(k.value) AS word, COUNT(*) AS count, MAX(timestamp) AS lastMatchAt " +
            "FROM keyword_matches, json_each(keyword_matches.keywords) AS k GROUP BY lower(k.value)"
    )
    fun statsByKeyword(): Flow<List<KeywordStats>>

    @Query("DELETE FROM keyword_matches WHERE rowid IN (:ids)")
    suspend fun delete(ids: List<Long>)

    /**
     * Fetch matches from [apps] (or every app when [allApps] is set), optionally searched by [query]
     * and limited to those [keyword] caught. The keyword check reads the keywords saved with each
     * match, the same rule as [statsByKeyword], so a keyword's count and its list always agree.
     *
     * @param keyword lowercased, or null for every keyword.
     */
    @Query(
        """
        SELECT * FROM keyword_matches
        WHERE (:allApps OR app IN (:apps))
        AND (:query IS NULL OR rowid IN (SELECT rowid FROM keyword_matches_fts WHERE keyword_matches_fts MATCH :query))
        AND (:keyword IS NULL OR EXISTS (SELECT 1 FROM json_each(keyword_matches.keywords) AS k WHERE lower(k.value) = :keyword))
        ORDER BY timestamp DESC
        """
    )
    fun getMatches(allApps: Boolean, apps: Set<String>, query: String?, keyword: String?): PagingSource<Int, KeywordMatch>

    /**
     * Delete all KeywordMatch entries from the database.
     *
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM keyword_matches")
    suspend fun clear(): Int
}
