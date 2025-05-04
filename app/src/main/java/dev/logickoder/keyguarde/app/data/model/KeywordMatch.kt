package dev.logickoder.keyguarde.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime


/**
 * Represents a match instance when a keyword was found in a notification
 */
@Entity(
    tableName = "keyword_matches",
    indices = [
        Index(value = ["keyword"]),
        Index(value = ["app"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Keyword::class,
            parentColumns = ["word"],
            childColumns = ["keyword"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WatchedApp::class,
            parentColumns = ["packageName"],
            childColumns = ["app"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class KeywordMatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyword: String,
    val message: String,
    val chat: String,
    val app: String,
    val timestamp: LocalDateTime
)