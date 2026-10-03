package dev.logickoder.keyguarde.app.data

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset

object Converters {
    @ColumnTypeConverter
    fun fromLocalDateTime(value: LocalDateTime?) = value?.toEpochSecond(ZoneOffset.UTC)

    @ColumnTypeConverter
    fun toLocalDateTime(value: Long?) = value?.let {
        LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
    }

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