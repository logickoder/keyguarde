package dev.logickoder.keyguarde.app.data.model

/**
 * How many matches one app has, for the counts in the Matches filter.
 */
data class AppMatchCount(
    val app: String,
    val count: Int,
)
