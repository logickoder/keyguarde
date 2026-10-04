package dev.logickoder.keyguarde.home.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * How long ago a match arrived, at the precision a list row needs: minutes and hours for today,
 * then day names for the past week, then a date.
 */
sealed interface RelativeTime {
    data object JustNow : RelativeTime
    data class Minutes(val count: Long) : RelativeTime
    data class Hours(val count: Long) : RelativeTime
    data object Yesterday : RelativeTime
    data class Weekday(val day: DayOfWeek) : RelativeTime
    data class Date(val date: LocalDate, val showYear: Boolean) : RelativeTime
}

fun relativeTime(timestamp: LocalDateTime, now: LocalDateTime): RelativeTime {
    val minutes = ChronoUnit.MINUTES.between(timestamp, now)
    val days = ChronoUnit.DAYS.between(timestamp.toLocalDate(), now.toLocalDate())
    return when {
        minutes < 1 -> RelativeTime.JustNow
        minutes < 60 -> RelativeTime.Minutes(minutes)
        minutes < 24 * 60 -> RelativeTime.Hours(minutes / 60)
        days <= 1 -> RelativeTime.Yesterday
        // Under a week, a day name can't be confused with last week's.
        days < 7 -> RelativeTime.Weekday(timestamp.dayOfWeek)
        else -> RelativeTime.Date(timestamp.toLocalDate(), showYear = timestamp.year != now.year)
    }
}
