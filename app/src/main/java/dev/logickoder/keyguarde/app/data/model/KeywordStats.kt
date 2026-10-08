package dev.logickoder.keyguarde.app.data.model

import java.time.Instant

/**
 * How often one keyword has matched and when it last did, for the Keywords tab.
 */
data class KeywordStats(
    val word: String,
    val count: Int,
    val lastMatchAt: Instant?,
)
