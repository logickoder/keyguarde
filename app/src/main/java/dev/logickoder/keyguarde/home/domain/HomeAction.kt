package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp

sealed interface HomeAction {
    data class SearchQueryChanged(val query: String) : HomeAction

    /** Show only [app]'s matches, or every app's when null. Persists across launches. */
    data class FilterChanged(val app: WatchedApp?) : HomeAction

    data object ShowFilterSheet : HomeAction

    data object DismissFilterSheet : HomeAction

    data class OpenInApp(val match: KeywordMatch) : HomeAction

    data object ResetCount : HomeAction

    /** The screen became visible; re-read when the user last left so "new" is measured from then. */
    data object RefreshLastVisit : HomeAction

    /** Enter selection mode with nothing selected (overflow menu). */
    data object StartSelection : HomeAction

    /** Enter selection mode with [matchId] selected (long-press on a row). */
    data class StartSelectionWith(val matchId: Long) : HomeAction

    data object ExitSelection : HomeAction

    data class ToggleMatchSelection(val matchId: Long) : HomeAction

    data class SelectVisibleMatches(val matchIds: List<Long>) : HomeAction

    data object DeleteSelectedMatches : HomeAction

    data object ShowClearAllConfirm : HomeAction

    data object DismissClearAllConfirm : HomeAction

    data object ClearAllMatches : HomeAction

    /** Put back whatever the last delete or clear removed. */
    data object UndoDelete : HomeAction
}
