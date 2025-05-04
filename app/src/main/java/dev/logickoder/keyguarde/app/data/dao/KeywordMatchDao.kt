package dev.logickoder.keyguarde.app.data.dao

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
     * Insert one or more KeywordMatch entries into the database.
     * If a conflict occurs, the existing entry will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg matches: KeywordMatch)

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
    @Query("SELECT * FROM keyword_matches WHERE keyword = :keyword ORDER BY timestamp DESC")
    fun getByKeyword(keyword: String): Flow<List<KeywordMatch>>

    /**
     * Fetch KeywordMatch entries filtered by a specific app.
     */
    @Query("SELECT * FROM keyword_matches WHERE app = :app ORDER BY timestamp DESC")
    fun getByApp(app: String): Flow<List<KeywordMatch>>
}