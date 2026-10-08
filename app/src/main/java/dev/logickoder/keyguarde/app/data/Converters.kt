package dev.logickoder.keyguarde.app.data

import androidx.room3.ColumnTypeConverter
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object Converters {
    @ColumnTypeConverter
    fun fromLocalDateTime(value: LocalDateTime?) = value?.toEpochSecond()

    @ColumnTypeConverter
    fun toLocalDateTime(value: Long?) = value?.let(::localDateTimeOfEpochSecond)

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
/**
 * Seconds since the epoch for this phone-local time. Stored times are real instants, so they still
 * read right after the phone changes time zone or clocks change.
 */
fun LocalDateTime.toEpochSecond(): Long = atZone(ZoneId.systemDefault()).toEpochSecond()

/**
 * The phone-local time for [epochSecond], the reverse of [toEpochSecond].
 */
fun localDateTimeOfEpochSecond(epochSecond: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault())
