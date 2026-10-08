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
    }
}