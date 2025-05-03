package dev.logickoder.keyguarde.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.logickoder.keyguarde.app.data.model.SelectedApp

@Dao
interface SelectedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg app: SelectedApp)

    @Query("DELETE FROM selected_apps WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("SELECT * FROM selected_apps")
    suspend fun getAll(): List<SelectedApp>
}