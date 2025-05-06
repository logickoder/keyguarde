package dev.logickoder.keyguarde.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.app.domain.SingletonCompanion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class SettingsRepository(context: Context) {
    private val localStore = AppStore.getInstance(context)

    val usePersistentSilentNotification = localStore.get(USE_PERSISTENT_SILENT_NOTIFICATION).map {
        it ?: true
    }

    val showHeadsUpAlert = localStore.get(SHOW_HEADS_UP_ALERT).map {
        it ?: true
    }

    val resetMatchCountOnAppOpen = localStore.get(RESET_MATCH_COUNT_ON_APP_OPEN).map {
        it ?: false
    }

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

    companion object : SingletonCompanion<SettingsRepository, Context>() {
        override fun createInstance(dependency: Context) = SettingsRepository(dependency)

        private val USE_PERSISTENT_SILENT_NOTIFICATION = booleanPreferencesKey("use_persistent_silent_notification")
        private val SHOW_HEADS_UP_ALERT = booleanPreferencesKey("show_heads_up_alert")
        private val RESET_MATCH_COUNT_ON_APP_OPEN = booleanPreferencesKey("reset_match_count_on_app_open")
    }
}