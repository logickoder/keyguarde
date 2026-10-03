package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class KeywordsState(
    val keywords: ImmutableList<Keyword> = persistentListOf(),
    val isDialogVisible: Boolean = false,
    val editing: Keyword? = null,
)
