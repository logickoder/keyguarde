package dev.logickoder.keyguarde.home.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageSpansTest {

    private fun String.at(span: MessageSpan) = substring(span.range)

    @Test
    fun `keywords match whole words only, ignoring case`() {
        val message = "Start the Invoice. art invoices"
        val spans = messageSpans(message, listOf("invoice", "art"))

        assertEquals(listOf("Invoice", "art"), spans.map { message.at(it) })
    }

    @Test
    fun `urls become links without trailing punctuation`() {
        val message = "See https://example.com/pay?id=4. Thanks"
        val link = messageSpans(message, emptyList()).single() as MessageSpan.Link

        assertEquals("https://example.com/pay?id=4", message.at(link))
        assertEquals("https://example.com/pay?id=4", link.target)
    }

    @Test
    fun `www links get an https target`() {
        val link = messageSpans("go to www.example.com", emptyList()).single() as MessageSpan.Link

        assertEquals("https://www.example.com", link.target)
    }

    @Test
    fun `emails and phone numbers get mailto and tel targets`() {
        val spans = messageSpans("Mail ada@example.com or call +234 801 234 5678", emptyList())
            .filterIsInstance<MessageSpan.Link>()

        assertEquals(listOf("mailto:ada@example.com", "tel:+2348012345678"), spans.map { it.target })
    }

    @Test
    fun `a keyword inside a link stays part of the link`() {
        val message = "Pay at https://example.com/invoice now, invoice attached"
        val spans = messageSpans(message, listOf("invoice"))

        assertEquals(2, spans.size)
        assertEquals(MessageSpan.Link::class, spans[0]::class)
        assertEquals("invoice", message.at(spans[1]))
    }

    @Test
    fun `spans come back in reading order`() {
        val message = "urgent: www.example.com then urgent"
        val starts = messageSpans(message, listOf("urgent")).map { it.range.first }

        assertEquals(starts.sorted(), starts)
        assertEquals(3, starts.size)
    }
}
