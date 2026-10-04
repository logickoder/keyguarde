package dev.logickoder.keyguarde.home.domain

/**
 * A highlighted part of a full message: a matched keyword, or a link the user can tap.
 */
sealed interface MessageSpan {
    val range: IntRange

    data class Keyword(override val range: IntRange) : MessageSpan

    /** @property target what to open: an https, mailto or tel URI. */
    data class Link(override val range: IntRange, val target: String) : MessageSpan
}

private val LinkPattern = Regex(
    "(?<url>(?:https?://|www\\.)[^\\s<>\"]+)" +
        "|(?<email>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})" +
        "|(?<phone>\\+?\\d[\\d\\s-]{6,}\\d)",
    RegexOption.IGNORE_CASE,
)

// Sentence punctuation right after a link is almost never part of it.
private const val TrailingPunctuation = ".,;:!?)]}'\""

/**
 * Finds keywords (same whole-word rule as the listener) and links in [message], in reading order.
 * A keyword inside a link is dropped, so the link stays tappable as one piece.
 */
fun messageSpans(message: String, keywords: Collection<String>): List<MessageSpan> {
    val links = LinkPattern.findAll(message).mapNotNull { match ->
        val text = match.value.trimEnd { it in TrailingPunctuation }
        if (text.isEmpty()) return@mapNotNull null
        val range = match.range.first until match.range.first + text.length
        val target = when {
            match.groups["url"] != null -> if (text.startsWith("www.", ignoreCase = true)) "https://$text" else text
            match.groups["email"] != null -> "mailto:$text"
            else -> "tel:${text.filter { it.isDigit() || it == '+' }}"
        }
        MessageSpan.Link(range, target)
    }.toList()

    val keywordSpans = keywordRegex(keywords)?.findAll(message).orEmpty()
        .map { MessageSpan.Keyword(it.range) }
        .filter { keyword -> links.none { it.range.overlaps(keyword.range) } }
        .toList()

    return (links + keywordSpans).sortedBy { it.range.first }
}

private fun IntRange.overlaps(other: IntRange) = first <= other.last && other.first <= last
