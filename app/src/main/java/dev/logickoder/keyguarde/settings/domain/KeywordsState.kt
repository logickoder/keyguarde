package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

data class KeywordsState(
    val keywords: ImmutableList<Keyword> = persistentListOf(),
    /** Match count and last match per keyword, keyed by the lowercased word. Missing means none yet. */
    val stats: ImmutableMap<String, KeywordStats> = persistentMapOf(),
    val sort: KeywordSort = KeywordSort.RecentMatch,
    /** The keyword open in the edit sheet; null when the sheet is closed. */
    val editing: Keyword? = null,
)
