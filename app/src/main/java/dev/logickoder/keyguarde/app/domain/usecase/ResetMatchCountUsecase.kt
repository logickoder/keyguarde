package dev.logickoder.keyguarde.app.domain.usecase

import android.content.Context
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.settings.SettingsRepository
import kotlinx.coroutines.flow.first

class ResetMatchCountUsecase(
    private val context: Context,
    private val repository: AppRepository,
    private val settings: SettingsRepository,
) {
    suspend operator fun invoke() {
        repository.updateRecentMatchCount(0)
        repository.updateRecentChats(emptySet())

        if (settings.usePersistentSilentNotification.first()) {
            NotificationHelper.showPersistentNotification(
                context,
                0,
                0,
            )
        }
    }
}
