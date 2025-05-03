package dev.logickoder.keyguarde.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keywords")
data class Keyword(
    @PrimaryKey val word: String,
    val createdAt: Long = System.currentTimeMillis(),
)