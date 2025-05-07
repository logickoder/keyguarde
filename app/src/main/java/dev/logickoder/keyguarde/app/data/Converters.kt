package dev.logickoder.keyguarde.app.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

object Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?) = value?.toEpochDay()

    @TypeConverter
    fun toLocalDate(value: Long?) = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?) = value?.toEpochSecond(ZoneOffset.UTC)

    @TypeConverter
    fun toLocalDateTime(value: Long?) = value?.let {
        LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromStringSet(value: Set<String>?): String? {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringSet(value: String?): Set<String>? {
        return value?.let {
            Json.decodeFromString<Set<String>>(it)
        }
    }
}