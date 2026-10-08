package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.KeywordMatch

/**
 * One-off events the screen turns into user feedback. Kept as data so the ViewModel never needs
 * a Context to format strings. Deletes and clears offer an undo.
 */
sealed interface HomeEffect {
    /** Carries the removed matches, so the snackbar's Undo restores exactly these. */
    data class MatchesDeleted(val matches: List<KeywordMatch>) : HomeEffect

    data class MatchesCleared(val matches: List<KeywordMatch>) : HomeEffect

    data class OpenInAppFailed(val reason: String?) : HomeEffect

    /** Starting an activity needs a Context, so the screen does it. */
    data class LaunchApp(val packageName: String) : HomeEffect
}
