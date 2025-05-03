package dev.logickoder.keyguarde.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_apps")
data class SelectedApp(
    @PrimaryKey val packageName: String,
    val name: String,
    val icon: String,
)