package dev.logickoder.keyguarde.app.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class ConvertersTest {

    @Test
    fun `a time is stored as seconds since the epoch`() {
        assertEquals(1_791_450_000L, Converters.fromInstant(Instant.parse("2026-10-08T09:00:00Z")))
    }

    @Test
    fun `a stored time reads back as the same instant`() {
        assertEquals(Instant.parse("2026-10-08T09:00:00Z"), Converters.toInstant(1_791_450_000L))
    }
}
