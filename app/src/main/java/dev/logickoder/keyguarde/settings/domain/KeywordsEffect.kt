package dev.logickoder.keyguarde.settings.domain

sealed interface KeywordsEffect {
    /** Show a snackbar with Undo. */
    data class Deleted(val word: String) : KeywordsEffect
}
