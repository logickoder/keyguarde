package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp

sealed interface HomeAction {
    data class SearchQueryChanged(val query: String) : HomeAction

    data class FilterChanged(val app: WatchedApp?) : HomeAction

    data object ToggleKeywordDialog : HomeAction

    data class SaveKeyword(val word: String) : HomeAction

    data class DeleteMatch(val match: KeywordMatch) : HomeAction

    data class OpenInApp(val match: KeywordMatch) : HomeAction

    data object ResetCount : HomeAction

    /** The screen became visible; re-read when the user last left so "new" is measured from then. */
    data object RefreshLastVisit : HomeAction

    data object ToggleSelectionMode : HomeAction

    data class ToggleMatchSelection(val matchId: Long) : HomeAction

    data class SelectVisibleMatches(val matchIds: List<Long>) : HomeAction

    data object ClearSelection : HomeAction

    data object DeleteSelectedMatches : HomeAction

    data object ClearAllMatches : HomeAction
}
