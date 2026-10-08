package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword

sealed interface KeywordsAction {
    data class OpenDialog(val keyword: Keyword? = null) : KeywordsAction

    data object DismissDialog : KeywordsAction

    /** A new keyword from the field at the top; already checked by the field. */
    data class Add(val word: String) : KeywordsAction

    data class Save(val word: String) : KeywordsAction

    data class Delete(val keyword: Keyword) : KeywordsAction
}
