package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class HomeState(
    val filter: WatchedApp? = null,
    val watchedApps: ImmutableList<WatchedApp> = persistentListOf(),
    val newSinceLastVisit: Int = 0,
    val openableMatchIds: ImmutableSet<Long> = persistentSetOf(),
    val isFilterSheetVisible: Boolean = false,
    val isClearAllConfirmVisible: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedMatches: ImmutableSet<Long> = persistentSetOf(),
)
