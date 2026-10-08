package dev.logickoder.keyguarde.app.data.model

/**
 * How many matches one keyword has, for the counts in the Keywords tab.
 */
data class KeywordMatchCount(
    val word: String,
    val count: Int,
)
