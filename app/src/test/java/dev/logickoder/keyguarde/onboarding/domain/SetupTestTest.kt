package dev.logickoder.keyguarde.onboarding.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SetupTestTest {

    @Test
    fun `the keyword in the received text is reported`() {
        assertEquals("invoice", caughtKeyword("Testing Keyguarde: does it catch “invoice”?", listOf("invoice", "rent")))
    }

    @Test
    fun `text without a whole-word keyword catches nothing`() {
        assertNull(caughtKeyword("Testing the current setup", listOf("rent")))
    }
}
