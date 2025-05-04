package dev.logickoder.keyguarde.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Represents a keyword that the user wants to monitor in notifications
 */
@Entity(tableName = "keywords")
data class Keyword(
    @PrimaryKey val word: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isCaseSensitive: Boolean = false,
)