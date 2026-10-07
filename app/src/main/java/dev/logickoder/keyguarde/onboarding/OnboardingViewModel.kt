package dev.logickoder.keyguarde.onboarding

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.onboarding.domain.KeywordInput
import dev.logickoder.keyguarde.onboarding.domain.defaultAppSelection
import dev.logickoder.keyguarde.onboarding.domain.OnboardingAction
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import dev.logickoder.keyguarde.onboarding.domain.OnboardingState
import dev.logickoder.keyguarde.onboarding.domain.parseKeyword
import dev.logickoder.keyguarde.onboarding.domain.saveIconToFile
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingViewModel(
    private val repository: AppRepository,
    private val savedStateHandle: SavedStateHandle,
    // Reading every installed app's label and icon is slow; kept off the main thread.
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _state = MutableStateFlow(restore())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private var loadAppsJob: Job? = null

    init {
        loadApps()
        // The user can leave for system Settings on the Access page; keep their progress
        // if Android kills the process meanwhile.
        viewModelScope.launch {
            _state.collect { state ->
                savedStateHandle[KEY_PAGES] = ArrayList(state.backStack.map { it.name })
                // Saved only once defaults are in, or a restore would read the empty start as a choice.
                if (state.hasDefaultedApps) {
                    savedStateHandle[KEY_SELECTED_APPS] = ArrayList(state.selectedApps)
                }
                savedStateHandle[KEY_KEYWORDS] = ArrayList(state.keywords.map { it.word })
            }
        }
    }

    private fun restore(): OnboardingState {
        val default = OnboardingState()
        val savedApps = savedStateHandle.get<ArrayList<String>>(KEY_SELECTED_APPS)
        return default.copy(
            backStack = savedStateHandle.get<ArrayList<String>>(KEY_PAGES)
                // A page renamed between app versions is dropped rather than crashing the restore.
                ?.mapNotNull { name -> OnboardingPage.entries.firstOrNull { it.name == name } }
                ?.takeIf { it.isNotEmpty() }
                ?.toImmutableList()
                ?: default.backStack,
            selectedApps = savedApps?.toImmutableSet() ?: default.selectedApps,
            // A restored selection is the user's own; don't replace it with defaults.
            hasDefaultedApps = savedApps != null,
            keywords = savedStateHandle.get<ArrayList<String>>(KEY_KEYWORDS)
                ?.map { Keyword(word = it) }
                ?.toImmutableList()
                ?: default.keywords,
        )
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.Next -> next()

            OnboardingAction.Previous -> _state.update {
                when (it.backStack.size > 1) {
                    true -> it.copy(backStack = it.backStack.dropLast(1).toImmutableList())
                    else -> it
                }
            }

            is OnboardingAction.ToggleApp -> _state.update {
                val selected = when (action.packageName in it.selectedApps) {
                    true -> it.selectedApps - action.packageName
                    else -> it.selectedApps + action.packageName
                }
                it.copy(selectedApps = selected.toImmutableSet())
            }

            is OnboardingAction.AddKeyword -> _state.update { state ->
                when (val input = parseKeyword(action.word, state.keywords.map { it.word })) {
                    is KeywordInput.Valid -> state.copy(
                        keywords = (state.keywords + Keyword(word = input.word)).toImmutableList()
                    )

                    // The page checks first; this guards a double tap on a suggestion.
                    else -> state
                }
            }

            is OnboardingAction.RemoveKeyword -> _state.update {
                it.copy(keywords = (it.keywords - action.keyword).toImmutableList())
            }

            is OnboardingAction.PermissionsChecked -> _state.update {
                it.copy(permissionGranted = action.listenerGranted, alertsAllowed = action.alertsAllowed)
            }

            is OnboardingAction.Save -> save(action.context.applicationContext)
        }
    }

    private fun next() {
        // The Access step's button opens Settings until access is on; this guards a stray Next.
        if (_state.value.currentPage == OnboardingPage.Access && !_state.value.permissionGranted) return
        _state.update {
            val nextPage = OnboardingPage.entries[it.currentPage.ordinal + 1]
            it.copy(backStack = it.backStack.toPersistentList().add(nextPage))
        }
        if (_state.value.currentPage == OnboardingPage.Apps && _state.value.apps.isEmpty()) {
            loadApps()
        }
    }

    private fun loadApps() {
        if (loadAppsJob?.isActive == true) {
            return
        }
        loadAppsJob = viewModelScope.launch {
            val apps = withContext(backgroundDispatcher) { repository.getInstalledApps() }
            _state.update { state ->
                state.copy(
                    apps = apps.toImmutableList(),
                    selectedApps = when (state.hasDefaultedApps) {
                        true -> state.selectedApps
                        else -> defaultAppSelection(apps.map { it.packageName }).toImmutableSet()
                    },
                    hasDefaultedApps = true,
                )
            }
        }
    }

    private fun save(context: Context) {
        if (_state.value.isSaving) {
            return
        }
        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val state = _state.value
            repository.addKeyword(*state.keywords.toTypedArray())

            val watchedApps = state.apps.filter { it.packageName in state.selectedApps }.map { app ->
                WatchedApp(
                    packageName = app.packageName,
                    name = app.name,
                    icon = saveIconToFile(
                        app.icon,
                        app.packageName,
                        context
                    )
                )
            }
            repository.addWatchedApp(*watchedApps.toTypedArray())

            // Last, so an interrupted save shows onboarding again instead of an empty app.
            repository.onboardingCompleted()

            NotificationHelper.startListenerService(context)
            NotificationHelper.requestListenerServiceRebind(context)

            _state.update { it.copy(isComplete = true) }
        }.invokeOnCompletion {
            _state.update { it.copy(isSaving = false) }
        }
    }

    companion object {
        private const val KEY_PAGES = "pages"
        private const val KEY_SELECTED_APPS = "selected_apps"
        private const val KEY_KEYWORDS = "keywords"

        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    OnboardingViewModel(
                        repository = container.appRepository,
                        savedStateHandle = createSavedStateHandle(),
                    )
                }
            }
        }
    }
}
