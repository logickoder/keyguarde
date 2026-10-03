package dev.logickoder.keyguarde.home.domain

/**
 * One-off events the screen turns into user feedback. Kept as data so the ViewModel never needs
 * a Context to format strings. Deletes and clears offer an undo.
 */
sealed interface HomeEffect {
    data class MatchesDeleted(val count: Int) : HomeEffect

    data class MatchesCleared(val count: Int) : HomeEffect

    data class OpenInAppFailed(val reason: String?) : HomeEffect
}
