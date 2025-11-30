package dev.logickoder.keyguarde.app.domain.usecase

import android.content.Context
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.settings.SettingsRepository
import kotlinx.coroutines.flow.first

object ResetMatchCountUsecase {
    suspend operator fun invoke(context: Context) {
        val repository = AppRepository.getInstance(context)
        repository.updateRecentMatchCount(0)
        repository.updateRecentChats(emptySet())

        if (SettingsRepository.getInstance(context).usePersistentSilentNotification.first()) {
            NotificationHelper.showPersistentNotification(
                context,
                0,
                0,
            )
        }
    }
}