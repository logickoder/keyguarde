package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class KeywordSortTest {

    private val rent = Keyword("rent", createdAt = 3)
    private val exam = Keyword("exam", createdAt = 1)
    private val invoice = Keyword("invoice", createdAt = 2)
    private val unused = Keyword("unused", createdAt = 4)
    private val keywords = listOf(rent, exam, invoice, unused)

    private fun stats(word: String, hour: Int) = word to KeywordStats(word, 1, Instant.parse("2026-10-08T00:00:00Z").plusSeconds(hour * 3_600L))

    private val stats = mapOf(stats("rent", 9), stats("exam", 11), stats("invoice", 10))

    @Test
    fun `recent match puts the latest first and never-matched last`() {
        assertEquals(listOf(exam, invoice, rent, unused), sortKeywords(keywords, stats, KeywordSort.RecentMatch))
    }

    @Test
    fun `a to z sorts by word`() {
        assertEquals(listOf(exam, invoice, rent, unused), sortKeywords(keywords, stats, KeywordSort.Alphabetical))
    }

    @Test
    fun `recently added puts the newest first`() {
        assertEquals(listOf(unused, rent, invoice, exam), sortKeywords(keywords, stats, KeywordSort.RecentlyAdded))
    }

    @Test
    fun `stats match keywords in any case`() {
        val upper = Keyword("Rent", createdAt = 1)
        assertEquals(listOf(upper, unused), sortKeywords(listOf(unused, upper), stats, KeywordSort.RecentMatch))
    }

    @Test
    fun `keywords that never matched fall back to a to z`() {
        val b = Keyword("bravo")
        val a = Keyword("alpha")
        assertEquals(listOf(a, b), sortKeywords(listOf(b, a), emptyMap(), KeywordSort.RecentMatch))
    }
}
