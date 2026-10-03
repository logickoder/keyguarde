package dev.logickoder.keyguarde.home.domain

/**
 * A short preview of a match, cut so the first matched keyword is visible.
 *
 * @property text the preview, prefixed with an ellipsis when the start was trimmed.
 * @property keywordRanges where each keyword occurrence sits in [text].
 */
data class MatchSnippet(
    val text: String,
    val keywordRanges: List<IntRange>,
)

private val Whitespace = "\\s+".toRegex()

/**
 * The same rule the listener uses to decide a match: whole word, case-insensitive. Returns null
 * when there is nothing to match.
 */
fun keywordRegex(keywords: Collection<String>): Regex? {
    val alternatives = keywords.filter { it.isNotBlank() }.joinToString("|") { Regex.escape(it) }
    return if (alternatives.isEmpty()) null else "\\b(?:$alternatives)\\b".toRegex(RegexOption.IGNORE_CASE)
}

/**
 * Builds a single-paragraph preview of [message] that starts at most [leadChars] before the first
 * matched keyword, trimmed back to a word boundary, so a two-line preview shows the keyword instead
 * of the message's opening.
 */
fun windowSnippet(
    message: String,
    keywords: Collection<String>,
    leadChars: Int = 32,
): MatchSnippet {
    val flat = message.trim().replace(Whitespace, " ")
    val regex = keywordRegex(keywords) ?: return MatchSnippet(flat, emptyList())
    val first = regex.find(flat) ?: return MatchSnippet(flat, emptyList())

    val text = when {
        first.range.first <= leadChars -> flat
        else -> {
            val roughStart = first.range.first - leadChars
            // Step forward to the next word start so the preview never opens mid-word.
            val wordStart = flat.indexOf(' ', roughStart).let { space ->
                if (space == -1 || space >= first.range.first) roughStart else space + 1
            }
            "…" + flat.substring(wordStart)
        }
    }
    return MatchSnippet(text, regex.findAll(text).map { it.range }.toList())
}
