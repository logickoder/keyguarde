package dev.logickoder.keyguarde.app.data.local

import androidx.room.TypeConverter
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
}