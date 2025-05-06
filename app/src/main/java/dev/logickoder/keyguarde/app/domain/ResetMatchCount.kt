package dev.logickoder.keyguarde.app.domain

import android.content.Context
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.settings.SettingsRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun resetMatchCount(context: Context) {
    GlobalScope.launch {
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