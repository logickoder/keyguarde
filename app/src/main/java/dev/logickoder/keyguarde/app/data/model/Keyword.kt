package dev.logickoder.keyguarde.app.data.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey


/**
 * Represents a keyword that the user wants to monitor in notifications
 */
@Entity(tableName = "keywords")
data class Keyword(
    @PrimaryKey val word: String,
    val createdAt: Long = System.currentTimeMillis(),
)