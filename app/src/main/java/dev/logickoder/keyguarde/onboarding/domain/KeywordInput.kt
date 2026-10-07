package dev.logickoder.keyguarde.onboarding.domain

private val Whitespace = "\\s+".toRegex()

/** The shortest keyword accepted; one letter would match almost every message. */
const val MIN_KEYWORD_LENGTH = 2

/**
 * What typing [input] into the keyword field would do. Keywords are stored lowercase with
 * single spaces, the same as the Keywords tab, since matching ignores case anyway.
 */
sealed interface KeywordInput {
    data object Empty : KeywordInput
    data object TooShort : KeywordInput
    data class Duplicate(val word: String) : KeywordInput
    data class Valid(val word: String) : KeywordInput
}

fun parseKeyword(input: String, existing: Collection<String>): KeywordInput {
    val word = input.trim().replace(Whitespace, " ").lowercase()
    return when {
        word.isEmpty() -> KeywordInput.Empty
        word.length < MIN_KEYWORD_LENGTH -> KeywordInput.TooShort
        existing.any { it.equals(word, ignoreCase = true) } -> KeywordInput.Duplicate(word)
        else -> KeywordInput.Valid(word)
    }
}
