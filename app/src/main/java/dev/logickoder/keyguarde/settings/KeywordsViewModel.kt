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
import dev.logickoder.keyguarde.settings.domain.KeywordsEffect
import dev.logickoder.keyguarde.settings.domain.KeywordsState
import dev.logickoder.keyguarde.settings.domain.sortKeywords
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeywordsViewModel(
    private val repository: AppRepository,
) : ViewModel() {
    private val editing = MutableStateFlow<Keyword?>(null)

    private val _effects = Channel<KeywordsEffect>(Channel.BUFFERED)
    val effects: Flow<KeywordsEffect> = _effects.receiveAsFlow()

    val state: StateFlow<KeywordsState> = combine(
        repository.keywords,
        repository.statsByKeyword,
        repository.keywordSort,
        editing,
    ) { keywords, stats, sort, editing ->
        KeywordsState(
            keywords = sortKeywords(keywords, stats, sort).toImmutableList(),
            stats = stats.toImmutableMap(),
            sort = sort,
            editing = editing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = KeywordsState(),
    )

    fun onAction(action: KeywordsAction) {
        when (action) {
            is KeywordsAction.Add -> viewModelScope.launch {
                repository.addKeyword(Keyword(word = action.word))
            }

            is KeywordsAction.Edit -> editing.update { action.keyword }

            is KeywordsAction.SetSort -> viewModelScope.launch { repository.saveKeywordSort(action.sort) }

            KeywordsAction.DismissEdit -> editing.update { null }

            is KeywordsAction.Save -> save(action.word)

            is KeywordsAction.Delete -> delete(action.keyword)

            // Same createdAt, so it returns to its old place.
            is KeywordsAction.UndoDelete -> viewModelScope.launch { repository.addKeyword(action.keyword) }
        }
    }

    private fun save(word: String) {
        val original = editing.value ?: return
        editing.update { null }
        if (word == original.word) return
        viewModelScope.launch {
            repository.updateKeyword(original, Keyword(word = word))
        }
    }

    private fun delete(keyword: Keyword) {
        editing.update { null }
        viewModelScope.launch {
            repository.deleteKeyword(keyword)
            _effects.send(KeywordsEffect.Deleted(keyword))
        }
    }

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
