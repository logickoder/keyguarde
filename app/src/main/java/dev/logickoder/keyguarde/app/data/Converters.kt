package dev.logickoder.keyguarde.app.data

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json
import java.time.Instant

object Converters {
    // Seconds since the epoch: a real instant, so times read right after the phone changes zone.
    @ColumnTypeConverter
    fun fromInstant(value: Instant?) = value?.epochSecond

    @ColumnTypeConverter
    fun toInstant(value: Long?) = value?.let(Instant::ofEpochSecond)

    @ColumnTypeConverter
    fun fromStringSet(value: Set<String>?): String? {
        return Json.encodeToString(value)
    }

    @ColumnTypeConverter
    fun toStringSet(value: String?): Set<String>? {
        return value?.let {
            Json.decodeFromString<Set<String>>(it)
        }
    }
}
