package dev.logickoder.keyguarde.app.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.TimeZone

class ConvertersTest {

    private lateinit var original: TimeZone

    @Before
    fun setUp() {
        original = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Lagos"))
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(original)
    }

    @Test
    fun `local time is stored as the real instant`() {
        // 10:00 in Lagos (UTC+1) is 09:00 UTC.
        val stored = Converters.fromLocalDateTime(LocalDateTime.of(2026, 10, 8, 10, 0))

        assertEquals(1_791_450_000L, stored)
    }

    @Test
    fun `a stored time reads back in the phone's current zone`() {
        val stored = Converters.fromLocalDateTime(LocalDateTime.of(2026, 10, 8, 10, 0))

        // The phone moves from Lagos (UTC+1) to UTC.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        assertEquals(LocalDateTime.of(2026, 10, 8, 9, 0), Converters.toLocalDateTime(stored))
    }
}
