package dev.logickoder.keyguarde.app.data.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "selected_apps")
data class WatchedApp(
    @PrimaryKey val packageName: String,
    val name: String,
    val icon: String,
)