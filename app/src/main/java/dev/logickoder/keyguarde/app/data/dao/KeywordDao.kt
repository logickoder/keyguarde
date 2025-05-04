package dev.logickoder.keyguarde.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.logickoder.keyguarde.app.data.model.Keyword
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg keyword: Keyword)

    @Query("DELETE FROM keywords WHERE word = :keyword")
    suspend fun delete(keyword: String)

    @Query("SELECT * FROM keywords ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Keyword>>
}