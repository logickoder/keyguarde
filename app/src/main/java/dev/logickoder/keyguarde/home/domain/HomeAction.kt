package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.KeywordMatch

sealed interface HomeAction {
    data class SearchQueryChanged(val query: String) : HomeAction

    /** Open the filter sheet, starting from the applied filter. */
    data object ShowFilterSheet : HomeAction

    /** Close the filter sheet without applying what was ticked. */
    data object DismissFilterSheet : HomeAction

    /** Tick or untick an app in the filter sheet. */
    data class ToggleFilterApp(val packageName: String) : HomeAction

    /** Untick every app in the filter sheet. */
    data object ClearFilterDraft : HomeAction

    /** Apply the sheet's ticked apps and close it. Nothing ticked means every app. Persists across launches. */
    data object ApplyFilter : HomeAction

    /** Show every app's matches again. */
    data object ClearFilter : HomeAction

    /** Show [match] in the detail sheet. */
    data class OpenMatch(val match: KeywordMatch) : HomeAction

    data object DismissMatch : HomeAction

    /** Jump to the chat through the notification's own intent, while it's still alive. */
    data class OpenInApp(val match: KeywordMatch) : HomeAction

    /** Fallback when the notification expired: open the source app's main screen. */
    data class LaunchApp(val packageName: String) : HomeAction

    /** Delete one match from the detail sheet, with undo. */
    data class DeleteMatch(val match: KeywordMatch) : HomeAction

    data object ResetCount : HomeAction

    /** The user asked to restart the listener; wait a moment for it to bind before warning again. */
    data object ListenerRestartRequested : HomeAction

    /** Show only the matches [word] caught, from the Keywords tab's "See matches". */
    data class FilterByKeyword(val word: String) : HomeAction

    data object ClearKeywordFilter : HomeAction

    /** Undo a pause set in Settings, from the banner on Matches. */
    data object Resume : HomeAction

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

    /**
     * Put back what one delete or clear removed. Carries the matches, so a snackbar from an
     * earlier delete can't restore a later one.
     */
    data class UndoDelete(val matches: List<KeywordMatch>) : HomeAction
}
