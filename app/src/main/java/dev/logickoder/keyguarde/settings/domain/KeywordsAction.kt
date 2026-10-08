package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.app.data.model.Keyword

sealed interface KeywordsAction {
    /** A new keyword from the field at the top; already checked by the field. */
    data class Add(val word: String) : KeywordsAction

    data class Edit(val keyword: Keyword) : KeywordsAction

    data object DismissEdit : KeywordsAction

    /** The edited word from the sheet; already checked by the sheet. */
    data class Save(val word: String) : KeywordsAction

    /** From a swipe, the edit sheet, or TalkBack's Delete action. Undoable from the snackbar. */
    data class Delete(val keyword: Keyword) : KeywordsAction

    /** Carries the keyword, so a snackbar from an earlier delete can't restore a later one. */
    data class UndoDelete(val keyword: Keyword) : KeywordsAction
}
