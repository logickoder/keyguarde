package dev.logickoder.keyguarde.app.data.model

/**
 * What the persistent notification shows: matches since the counter was last reset, and how many
 * sources they came from.
 */
data class RecentMatches(
    val count: Int,
    val sourceCount: Int,
)
