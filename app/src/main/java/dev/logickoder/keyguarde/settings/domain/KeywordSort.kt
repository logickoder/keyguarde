package dev.logickoder.keyguarde.settings.domain

import androidx.annotation.StringRes
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import java.time.LocalDateTime

enum class KeywordSort(@param:StringRes val label: Int) {
    /** Keywords that fired most recently first; ones that never fired sink to the bottom. */
    RecentMatch(R.string.keyword_sort_recent_match),
    Alphabetical(R.string.keyword_sort_alphabetical),
    RecentlyAdded(R.string.keyword_sort_recently_added),
}

/**
 * Orders keywords for the list. Ties fall back to the word, so the order never shuffles between
 * visits.
 *
 * @param stats keyed by the lowercased word.
 */
fun sortKeywords(keywords: List<Keyword>, stats: Map<String, KeywordStats>, sort: KeywordSort): List<Keyword> =
    when (sort) {
        KeywordSort.RecentMatch -> keywords.sortedWith(
            compareByDescending<Keyword, LocalDateTime?>(nullsFirst()) { stats[it.word.lowercase()]?.lastMatchAt }
                .thenBy { it.word }
        )
        KeywordSort.Alphabetical -> keywords.sortedBy { it.word }
        KeywordSort.RecentlyAdded -> keywords.sortedWith(compareByDescending<Keyword> { it.createdAt }.thenBy { it.word })
    }
