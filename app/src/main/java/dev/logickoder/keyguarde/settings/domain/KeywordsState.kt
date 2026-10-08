package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

data class KeywordsState(
    val keywords: ImmutableList<Keyword> = persistentListOf(),
    /** Matches per keyword, keyed by the lowercased word. Missing means none yet. */
    val matchCounts: ImmutableMap<String, Int> = persistentMapOf(),
    val isDialogVisible: Boolean = false,
    val editing: Keyword? = null,
)
