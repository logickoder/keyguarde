package dev.logickoder.keyguarde.app.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg app: WatchedApp)

    @Query("DELETE FROM selected_apps WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("SELECT * FROM selected_apps")
    fun getAll(): Flow<List<WatchedApp>>
}