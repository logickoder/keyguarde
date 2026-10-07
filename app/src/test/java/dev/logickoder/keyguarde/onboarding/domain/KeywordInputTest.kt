package dev.logickoder.keyguarde.onboarding.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class KeywordInputTest {

    @Test
    fun `blank input is empty`() {
        assertEquals(KeywordInput.Empty, parseKeyword("   ", emptyList()))
    }

    @Test
    fun `one letter is too short`() {
        assertEquals(KeywordInput.TooShort, parseKeyword(" a ", emptyList()))
    }

    @Test
    fun `input is trimmed, lowercased and single-spaced`() {
        assertEquals(KeywordInput.Valid("call me"), parseKeyword("  Call   ME ", emptyList()))
    }

    @Test
    fun `an existing keyword in any case is a duplicate`() {
        assertEquals(KeywordInput.Duplicate("invoice"), parseKeyword("INVOICE", listOf("Invoice")))
    }
}
