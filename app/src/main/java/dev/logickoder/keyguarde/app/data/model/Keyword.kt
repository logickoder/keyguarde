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
    val context: String = "", // Context/description for AI-based semantic matching
    val useSemanticMatching: Boolean = false, // Whether to use AI-based matching for this keyword
)