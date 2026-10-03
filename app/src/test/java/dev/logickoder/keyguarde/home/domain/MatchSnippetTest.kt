package dev.logickoder.keyguarde.home.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchSnippetTest {

    private fun MatchSnippet.highlighted() = keywordRanges.map { text.substring(it) }

    @Test
    fun `keyword near the start keeps the whole message`() {
        val snippet = windowSnippet("Urgent: call the bank before noon", setOf("urgent"))

        assertEquals("Urgent: call the bank before noon", snippet.text)
        assertEquals(listOf("Urgent"), snippet.highlighted())
    }

    @Test
    fun `keyword deep in a long message is windowed with an ellipsis`() {
        val message = "Morning all, quick round-up from yesterday's standup and the planning " +
            "session that ran long. The invoice for March is overdue, please chase."
        val snippet = windowSnippet(message, setOf("invoice"), leadChars = 20)

        assertTrue(snippet.text.startsWith("…"))
        assertTrue(snippet.text.contains("The invoice for March"))
        assertEquals(listOf("invoice"), snippet.highlighted())
    }

    @Test
    fun `window never starts in the middle of a word`() {
        val message = "Lots of preamble text goes here before we finally reach the deadline word"
        val snippet = windowSnippet(message, setOf("deadline"), leadChars = 15)

        val afterEllipsis = snippet.text.removePrefix("…")
        val wordStart = message.indexOf(afterEllipsis)
        assertTrue(wordStart == 0 || message[wordStart - 1] == ' ')
    }

    @Test
    fun `keyword at the very end is still visible`() {
        val message = "This message is long and rambles on for a while and only at the end says urgent"
        val snippet = windowSnippet(message, setOf("urgent"), leadChars = 10)

        assertTrue(snippet.text.endsWith("urgent"))
        assertEquals(listOf("urgent"), snippet.highlighted())
    }

    @Test
    fun `no match returns the message unchanged without ranges`() {
        val snippet = windowSnippet("Nothing to see here", setOf("invoice"))

        assertEquals("Nothing to see here", snippet.text)
        assertTrue(snippet.keywordRanges.isEmpty())
    }

    @Test
    fun `several keywords are all highlighted, case-insensitively`() {
        val snippet = windowSnippet("Meeting moved. URGENT: bring the invoice", setOf("urgent", "invoice", "meeting"))

        assertEquals(listOf("Meeting", "URGENT", "invoice"), snippet.highlighted())
    }

    @Test
    fun `keywords only match whole words`() {
        val snippet = windowSnippet("Let's start the art class", setOf("art"))

        assertEquals(listOf("art"), snippet.highlighted())
        assertEquals("art", snippet.text.substring(snippet.keywordRanges.single()))
        assertEquals(snippet.text.indexOf(" art ") + 1, snippet.keywordRanges.single().first)
    }

    @Test
    fun `keywords next to punctuation still match`() {
        val snippet = windowSnippet("Is it urgent? Yes, urgent!", setOf("urgent"))

        assertEquals(listOf("urgent", "urgent"), snippet.highlighted())
    }

    @Test
    fun `line breaks and runs of spaces collapse into single spaces`() {
        val snippet = windowSnippet("Hello\n\nteam,   the   deadline is today", setOf("deadline"))

        assertEquals("Hello team, the deadline is today", snippet.text)
    }

    @Test
    fun `blank keywords produce no regex`() {
        assertNull(keywordRegex(setOf("", "  ")))
    }
}
