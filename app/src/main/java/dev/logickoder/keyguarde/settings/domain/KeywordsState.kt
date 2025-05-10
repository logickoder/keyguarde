package dev.logickoder.keyguarde.settings.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.service.AppListenerService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KeywordsState(
    context: Context,
    private val scope: CoroutineScope,
) {
    private val repository = AppRepository.getInstance(context)

    var edit by mutableStateOf<Keyword?>(null)
        private set

    var isDialogVisible by mutableStateOf(false)
        private set

    val keywords = repository.keywords.map { it.toImmutableList() }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    fun toggleDialog(keyword: Keyword? = null) {
        edit = when (isDialogVisible) {
            true -> null
            false -> keyword
        }
        isDialogVisible = !isDialogVisible
    }

    fun saveKeyword(word: String) {
        if (word.isNotBlank()) {
            scope.launch {
                AppListenerService.fetch = true
                val keyword = Keyword(word = word)
                when (edit) {
                    null -> repository.addKeyword(keyword)
                    else -> repository.updateKeyword(edit!!, keyword)
                }
            }.invokeOnCompletion {
                toggleDialog()
            }
        }
    }

    fun deleteKeyword(keyword: Keyword) {
        scope.launch {
            AppListenerService.fetch = true
            repository.deleteKeyword(keyword)
        }
    }
}

@Composable
fun rememberKeywordsState(): KeywordsState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember {
        KeywordsState(
            context,
            scope,
        )
    }
}