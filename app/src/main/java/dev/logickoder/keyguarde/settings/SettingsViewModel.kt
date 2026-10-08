package dev.logickoder.keyguarde.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.service.AppListenerService
import dev.logickoder.keyguarde.home.domain.ListenerHealth
import dev.logickoder.keyguarde.onboarding.domain.SetupTest
import dev.logickoder.keyguarde.onboarding.domain.caughtKeyword
import dev.logickoder.keyguarde.settings.domain.SettingsAction
import dev.logickoder.keyguarde.settings.domain.SettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appRepository: AppRepository,
    private val settingsRepository: SettingsRepository,
    listenerConnected: Flow<Boolean> = AppListenerService.isConnected,
    setupTestReceived: Flow<String> = AppListenerService.setupTestReceived,
) : ViewModel() {
    private val listenerHealth = ListenerHealth(listenerConnected)

    private val inputs = MutableStateFlow(Inputs())

    private var testTimeoutJob: Job? = null

    private val alerts: Flow<Alerts> = combine(
        settingsRepository.showHeadsUpAlert,
        settingsRepository.usePersistentSilentNotification,
        settingsRepository.resetMatchCountOnAppOpen,
        ::Alerts,
    )

    val state: StateFlow<SettingsState> = combine(
        inputs,
        listenerHealth.issue,
        appRepository.keywords,
        combine(appRepository.installedWatchedAppCount, alerts, ::Pair),
        combine(settingsRepository.themeMode, appRepository.caughtCount, settingsRepository.ratePromptDone, ::Triple),
    ) { inputs, issue, keywords, (apps, alerts), (themeMode, caughtCount, ratePromptDone) ->
        SettingsState(
            listenerIssue = issue,
            notificationsAllowed = inputs.notificationsAllowed,
            test = inputs.test,
            testKeyword = keywords.firstOrNull()?.word,
            watchedAppCount = apps,
            showHeadsUpAlert = alerts.showHeadsUp,
            usePersistentNotification = alerts.usePersistent,
            resetCountOnOpen = alerts.resetOnOpen,
            isBatteryUnrestricted = inputs.isBatteryUnrestricted,
            themeMode = themeMode,
            caughtCount = caughtCount,
            ratePromptDone = ratePromptDone,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsState(),
    )

    init {
        viewModelScope.launch {
            setupTestReceived.collect { text -> onTestReceived(text) }
        }
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SystemChecked -> {
                listenerHealth.accessChecked(action.hasListenerAccess)
                inputs.update {
                    it.copy(
                        notificationsAllowed = action.notificationsAllowed,
                        isBatteryUnrestricted = action.isBatteryUnrestricted,
                    )
                }
            }

            SettingsAction.ListenerRestartRequested -> listenerHealth.restartRequested()

            SettingsAction.TestSent -> startTest()

            SettingsAction.ResetTest -> {
                testTimeoutJob?.cancel()
                inputs.update { it.copy(test = SetupTest.Idle) }
            }

            SettingsAction.ToggleHeadsUpAlert -> viewModelScope.launch {
                settingsRepository.toggleShowHeadsUpAlert()
            }

            SettingsAction.TogglePersistentNotification -> viewModelScope.launch {
                settingsRepository.toggleUsePersistentSilentNotification()
            }

            SettingsAction.ToggleResetCountOnOpen -> viewModelScope.launch {
                settingsRepository.toggleResetMatchCountOnAppOpen()
            }

            is SettingsAction.SetThemeMode -> viewModelScope.launch {
                settingsRepository.setThemeMode(action.mode)
            }

            SettingsAction.RatePromptDone -> viewModelScope.launch {
                settingsRepository.markRatePromptDone()
            }
        }
    }

    private fun startTest() {
        inputs.update { it.copy(test = SetupTest.Waiting) }
        testTimeoutJob?.cancel()
        testTimeoutJob = viewModelScope.launch {
            delay(TEST_TIMEOUT_MILLIS)
            inputs.update { if (it.test == SetupTest.Waiting) it.copy(test = SetupTest.Missed) else it }
        }
    }

    private suspend fun onTestReceived(text: String) {
        if (inputs.value.test != SetupTest.Waiting) return
        testTimeoutJob?.cancel()
        val keywords = appRepository.keywords.first().map { it.word }
        val keyword = caughtKeyword(text, keywords)
        inputs.update { it.copy(test = keyword?.let { word -> SetupTest.Caught(word) } ?: SetupTest.Missed) }
    }

    private data class Inputs(
        val notificationsAllowed: Boolean = true,
        val isBatteryUnrestricted: Boolean? = null,
        val test: SetupTest = SetupTest.Idle,
    )

    private data class Alerts(
        val showHeadsUp: Boolean,
        val usePersistent: Boolean,
        val resetOnOpen: Boolean,
    )

    companion object {
        // How long the listener gets to report the test back; usually it takes well under a second.
        private const val TEST_TIMEOUT_MILLIS = 5_000L

        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    SettingsViewModel(
                        appRepository = container.appRepository,
                        settingsRepository = container.settingsRepository,
                    )
                }
            }
        }
    }
}
