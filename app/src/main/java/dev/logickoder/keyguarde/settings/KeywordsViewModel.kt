package dev.logickoder.keyguarde.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import dev.logickoder.keyguarde.settings.domain.KeywordsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeywordsViewModel(
    private val repository: AppRepository,
) : ViewModel() {
    private val dialog = MutableStateFlow(DialogState())

    val state: StateFlow<KeywordsState> = combine(
        repository.keywords,
        dialog,
    ) { keywords, dialog ->
        KeywordsState(
            keywords = keywords.toImmutableList(),
            isDialogVisible = dialog.isVisible,
            editing = dialog.editing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = KeywordsState(),
    )

    fun onAction(action: KeywordsAction) {
        when (action) {
            is KeywordsAction.OpenDialog -> dialog.update {
                DialogState(isVisible = true, editing = action.keyword)
            }

            KeywordsAction.DismissDialog -> dialog.update { DialogState() }

            is KeywordsAction.Save -> save(action.word)

            is KeywordsAction.Delete -> viewModelScope.launch {
                repository.deleteKeyword(action.keyword)
            }
        }
    }

    private fun save(word: String) {
        if (word.isBlank()) {
            return
        }
        val editing = dialog.value.editing
        viewModelScope.launch {
            val keyword = Keyword(word = word)
            when (editing) {
                null -> repository.addKeyword(keyword)
                else -> repository.updateKeyword(editing, keyword)
            }
        }.invokeOnCompletion {
            dialog.update { DialogState() }
        }
    }

    private data class DialogState(
        val isVisible: Boolean = false,
        val editing: Keyword? = null,
    )

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    KeywordsViewModel(
                        repository = container.appRepository,
                    )
                }
            }
        }
    }
}
