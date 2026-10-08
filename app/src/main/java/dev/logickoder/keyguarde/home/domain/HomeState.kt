package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

data class HomeState(
    /** Apps the list is limited to, in watched-app order. Empty means every app. */
    val filter: ImmutableList<WatchedApp> = persistentListOf(),
    val watchedApps: ImmutableList<WatchedApp> = persistentListOf(),
    /** Match count per package name, for the filter sheet. */
    val matchCounts: ImmutableMap<String, Int> = persistentMapOf(),
    /** Apps ticked in the open filter sheet, applied only when the user confirms. */
    val filterDraft: ImmutableSet<String> = persistentSetOf(),
    val newSinceLastVisit: Int = 0,
    val openableMatchIds: ImmutableSet<Long> = persistentSetOf(),
    /** The match shown in the detail sheet, or null when it's closed. */
    val openMatch: KeywordMatch? = null,
    val listenerIssue: ListenerIssue = ListenerIssue.None,
    /** The user paused Keyguarde in Settings; nothing new is caught until they resume. */
    val isPaused: Boolean = false,
    /** Only matches this keyword caught, from a keyword's "See matches"; null for all. */
    val keywordFilter: String? = null,
    /** False when Android blocks Keyguarde's own notifications, so match alerts can't show. */
    val notificationsAllowed: Boolean = true,
    val isFilterSheetVisible: Boolean = false,
    val isClearAllConfirmVisible: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedMatches: ImmutableSet<Long> = persistentSetOf(),
)
