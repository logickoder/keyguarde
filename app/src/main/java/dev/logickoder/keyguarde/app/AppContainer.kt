package dev.logickoder.keyguarde.app

import android.content.Context
import dev.logickoder.keyguarde.App
import dev.logickoder.keyguarde.ads.AdsConsent
import dev.logickoder.keyguarde.analytics.Analytics
import dev.logickoder.keyguarde.analytics.FirebaseAppAnalytics
import dev.logickoder.keyguarde.app.data.AppDatabase
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.app.domain.SystemStatus
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
        ResetMatchCountUsecase(appRepository)
    }

    val systemStatus by lazy { SystemStatus(appContext) }

    val analytics: Analytics by lazy { FirebaseAppAnalytics(appContext) }

    val adsConsent by lazy { AdsConsent(appContext) }

    companion object {
        // Compose previews don't run App, so they get a throwaway container.
        fun from(context: Context): AppContainer {
            val appContext = context.applicationContext
            return (appContext as? App)?.container ?: AppContainer(appContext)
        }
    }
}
