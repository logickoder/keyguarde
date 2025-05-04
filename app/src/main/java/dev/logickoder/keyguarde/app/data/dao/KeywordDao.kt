package dev.logickoder.keyguarde.app.data.dao

import androidx.room.*
import dev.logickoder.keyguarde.app.data.model.Keyword
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg keyword: Keyword)

    @Query("DELETE FROM keywords WHERE word = :keyword")
    suspend fun delete(keyword: String)

    @Transaction
    suspend fun update(oldKeyword: Keyword, newKeyword: Keyword) {
        delete(oldKeyword.word)
        insert(newKeyword.copy(createdAt = oldKeyword.createdAt))
    }

    @Query("SELECT * FROM keywords ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Keyword>>
}