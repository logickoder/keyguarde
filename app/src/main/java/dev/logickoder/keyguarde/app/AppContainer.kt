package dev.logickoder.keyguarde.app

import android.content.Context
import dev.logickoder.keyguarde.App
import dev.logickoder.keyguarde.app.data.AppDatabase
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.settings.SettingsRepository

/**
 * Owns the app-wide singletons. Holds only the application context, so nothing here can leak
 * an Activity or Service.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database by lazy { AppDatabase.build(appContext) }

    private val appStore by lazy { AppStore(appContext) }

    val appRepository by lazy { AppRepository(appContext, appStore, database) }

    val settingsRepository by lazy { SettingsRepository(appStore) }

    val resetMatchCount by lazy {
        ResetMatchCountUsecase(appContext, appRepository, settingsRepository)
    }
}

val Context.container: AppContainer
    get() = (applicationContext as App).container
