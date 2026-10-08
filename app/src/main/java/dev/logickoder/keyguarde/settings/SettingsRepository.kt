package dev.logickoder.keyguarde.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.settings.domain.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class SettingsRepository(private val localStore: AppStore) {

    val usePersistentSilentNotification = localStore.get(USE_PERSISTENT_SILENT_NOTIFICATION).map {
        it ?: true
    }

    val showHeadsUpAlert = localStore.get(SHOW_HEADS_UP_ALERT).map {
        it ?: true
    }

    val resetMatchCountOnAppOpen = localStore.get(RESET_MATCH_COUNT_ON_APP_OPEN).map {
        it ?: false
    }

    // An unknown name (from a removed option) falls back to following the system.
    val themeMode = localStore.get(THEME_MODE).map { name ->
        ThemeMode.entries.firstOrNull { it.name == name } ?: ThemeMode.System
    }

    suspend fun setThemeMode(mode: ThemeMode) = localStore.save(THEME_MODE, mode.name)

    /**
     * While paused, the listener ignores every notification. Notification access stays on, so
     * resuming takes one tap instead of a trip through Android settings.
     */
    val isPaused = localStore.get(PAUSED).map { it ?: false }

    suspend fun setPaused(paused: Boolean) = localStore.save(PAUSED, paused)

    /**
     * True once the user opened the Battery screen. Most phones restrict battery by default, so
     * the card's warning would otherwise show forever; once seen, the Battery row keeps the state.
     */
    val batteryNoticeSeen = localStore.get(BATTERY_NOTICE_SEEN).map { it ?: false }

    suspend fun markBatteryNoticeSeen() = localStore.save(BATTERY_NOTICE_SEEN, true)

    /** True once the user rated or dismissed the rate prompt; it never comes back after that. */
    val ratePromptDone = localStore.get(RATE_PROMPT_DONE).map { it ?: false }

    suspend fun markRatePromptDone() = localStore.save(RATE_PROMPT_DONE, true)

    suspend fun toggleUsePersistentSilentNotification() = localStore.save(
        USE_PERSISTENT_SILENT_NOTIFICATION,
        !usePersistentSilentNotification.first()
    )

    suspend fun toggleShowHeadsUpAlert() = localStore.save(
        SHOW_HEADS_UP_ALERT,
        !showHeadsUpAlert.first()
    )

    suspend fun toggleResetMatchCountOnAppOpen() = localStore.save(
        RESET_MATCH_COUNT_ON_APP_OPEN,
        !resetMatchCountOnAppOpen.first()
    )

    companion object {
        private val USE_PERSISTENT_SILENT_NOTIFICATION = booleanPreferencesKey("use_persistent_silent_notification")
        private val SHOW_HEADS_UP_ALERT = booleanPreferencesKey("show_heads_up_alert")
        private val RESET_MATCH_COUNT_ON_APP_OPEN = booleanPreferencesKey("reset_match_count_on_app_open")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val RATE_PROMPT_DONE = booleanPreferencesKey("rate_prompt_done")
        private val PAUSED = booleanPreferencesKey("paused")
        private val BATTERY_NOTICE_SEEN = booleanPreferencesKey("battery_notice_seen")
    }
}