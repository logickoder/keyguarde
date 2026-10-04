package dev.logickoder.keyguarde.home.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class RelativeTimeTest {

    // A Saturday afternoon.
    private val now = LocalDateTime.of(2026, 10, 3, 15, 0)

    @Test
    fun `under a minute is just now`() {
        assertEquals(RelativeTime.JustNow, relativeTime(now.minusSeconds(30), now))
    }

    @Test
    fun `under an hour counts minutes`() {
        assertEquals(RelativeTime.Minutes(42), relativeTime(now.minusMinutes(42), now))
    }

    @Test
    fun `under a day counts hours, even across midnight`() {
        assertEquals(RelativeTime.Hours(20), relativeTime(now.minusHours(20), now))
    }

    @Test
    fun `a day or more ago on the previous date is yesterday`() {
        assertEquals(RelativeTime.Yesterday, relativeTime(now.minusHours(25), now))
    }

    @Test
    fun `two to six days ago is the day name`() {
        assertEquals(RelativeTime.Weekday(DayOfWeek.THURSDAY), relativeTime(now.minusDays(2), now))
        assertEquals(RelativeTime.Weekday(DayOfWeek.SUNDAY), relativeTime(now.minusDays(6), now))
    }

    @Test
    fun `a week or more ago is the date without the year`() {
        assertEquals(
            RelativeTime.Date(LocalDate.of(2026, 9, 26), showYear = false),
            relativeTime(now.minusDays(7), now),
        )
    }

    @Test
    fun `a date from another year shows the year`() {
        assertEquals(
            RelativeTime.Date(LocalDate.of(2025, 12, 30), showYear = true),
            relativeTime(LocalDateTime.of(2025, 12, 30, 9, 0), now),
        )
    }
}
