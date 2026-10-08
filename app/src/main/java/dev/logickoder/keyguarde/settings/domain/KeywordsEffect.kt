package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword

sealed interface KeywordsEffect {
    /** Show a snackbar with Undo, which restores this exact keyword. */
    data class Deleted(val keyword: Keyword) : KeywordsEffect
}
