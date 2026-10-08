package dev.logickoder.keyguarde.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.logickoder.keyguarde.analytics.Analytics
import dev.logickoder.keyguarde.analytics.analyticsEvent
import dev.logickoder.keyguarde.analytics.log
import dev.logickoder.keyguarde.app.AppContainer
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.domain.SystemState
import dev.logickoder.keyguarde.app.service.AppListenerService
import dev.logickoder.keyguarde.home.domain.ListenerHealth
import dev.logickoder.keyguarde.onboarding.domain.SetupTestRunner
import dev.logickoder.keyguarde.settings.domain.SettingsAction
import dev.logickoder.keyguarde.settings.domain.SettingsState
import dev.logickoder.keyguarde.settings.domain.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appRepository: AppRepository,
    private val settingsRepository: SettingsRepository,
    systemState: Flow<SystemState>,
    listenerConnected: Flow<Boolean> = AppListenerService.isConnected,
    setupTestReceived: Flow<String> = AppListenerService.setupTestReceived,
    private val analytics: Analytics = Analytics.None,
) : ViewModel() {
    private val listenerHealth = ListenerHealth(
        systemState.map { it.hasListenerAccess },
        listenerConnected,
        settingsRepository.isPaused,
    )

    private val setupTest = SetupTestRunner(
        scope = viewModelScope,
        received = setupTestReceived,
        keywords = { appRepository.keywords.first().map { it.word } },
        onFinished = { test -> analytics.log(test.analyticsEvent(from = "settings")) },
    )

    private val alerts: Flow<Alerts> = combine(
        settingsRepository.showHeadsUpAlert,
        settingsRepository.usePersistentSilentNotification,
        settingsRepository.resetMatchCountOnAppOpen,
        ::Alerts,
    )

    val state: StateFlow<SettingsState> = combine(
        combine(systemState, setupTest.state, ::Pair),
        listenerHealth.issue,
        appRepository.keywords,
        combine(appRepository.installedWatchedAppCount, alerts, ::Pair),
        combine(
            settingsRepository.themeMode,
            appRepository.caughtCount,
            settingsRepository.ratePromptDone,
            settingsRepository.isPaused,
            settingsRepository.batteryNoticeSeen,
            ::Extras,
        ),
    ) { (system, test), issue, keywords, (apps, alerts), extras ->
        SettingsState(
            listenerIssue = issue,
            notificationsAllowed = system.notificationsAllowed,
            test = test,
            testKeyword = keywords.firstOrNull()?.word,
            watchedAppCount = apps,
            showHeadsUpAlert = alerts.showHeadsUp,
            usePersistentNotification = alerts.usePersistent,
            resetCountOnOpen = alerts.resetOnOpen,
            isBatteryUnrestricted = system.isBatteryUnrestricted,
            themeMode = extras.themeMode,
            caughtCount = extras.caughtCount,
            ratePromptDone = extras.ratePromptDone,
            isPaused = extras.isPaused,
            batteryNoticeSeen = extras.batteryNoticeSeen,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsState(),
    )

    fun onAction(action: SettingsAction) {
        analytics.log(action.analyticsEvent())
        when (action) {
            SettingsAction.ListenerRestartRequested -> listenerHealth.restartRequested()

            SettingsAction.TestSent -> setupTest.start()

            SettingsAction.ResetTest -> setupTest.reset()

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

            is SettingsAction.SetPaused -> viewModelScope.launch {
                settingsRepository.setPaused(action.paused)
            }

            SettingsAction.BatteryScreenOpened -> viewModelScope.launch {
                settingsRepository.markBatteryNoticeSeen()
            }

            SettingsAction.RatePromptDone -> viewModelScope.launch {
                settingsRepository.markRatePromptDone()
            }
        }
    }

    private data class Extras(
        val themeMode: ThemeMode,
        val caughtCount: Int,
        val ratePromptDone: Boolean,
        val isPaused: Boolean,
        val batteryNoticeSeen: Boolean,
    )

    private data class Alerts(
        val showHeadsUp: Boolean,
        val usePersistent: Boolean,
        val resetOnOpen: Boolean,
    )

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val container = AppContainer.from(context)
            return viewModelFactory {
                initializer {
                    SettingsViewModel(
                        appRepository = container.appRepository,
                        settingsRepository = container.settingsRepository,
                        systemState = container.systemStatus.state,
                        analytics = container.analytics,
                    )
                }
            }
        }
    }
}
