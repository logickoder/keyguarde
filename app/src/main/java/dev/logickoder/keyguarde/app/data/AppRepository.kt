package dev.logickoder.keyguarde.app.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.SingletonCompanion
import dev.logickoder.keyguarde.onboarding.domain.AppInfo

/**
 * Repository for managing Keyguarde data.
 * This class acts as a single source of truth for accessing and managing data
 * from both the local database (Room) and the local store (DataStore).
 */
class AppRepository(private val context: Context) {

    private val localStore = AppStore.getInstance(context)

    private val database = AppDatabase.getInstance(context)

    /**
     * Check if the onboarding process is complete.
     */
    val onboardingComplete = localStore.get(ONBOARDING_COMPLETE)

    /**
     * Get all keywords stored in the database.
     */
    val keywords = database.keywordDao().getAll()

    /**
     * Get all watched apps stored in the database.
     */
    val watchedApps = database.watchedAppDao().getAll()

    /**
     * Get all keyword matches stored in the database.
     */
    val matches = database.keywordMatchDao().getAll()

    /**
     * Get all installed apps that can post notifications on the device.
     * Filters out the current app and sorts the apps by priority and name.
     *
     * @return A sorted list of [AppInfo] containing app name, package name, and icon.
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledApps() = buildList {
        val packageManager = context.packageManager
        val installedApplications = packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA
        )
        for (app in installedApplications) {
            if (packageManager.checkPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    app.packageName
                ) == PackageManager.PERMISSION_GRANTED
                && app.packageName != context.packageName
            ) {
                add(
                    AppInfo(
                        name = packageManager.getApplicationLabel(app).toString(),
                        app.packageName,
                        icon = packageManager.getApplicationIcon(app)
                    )
                )
            }
        }
    }.sortedWith(
        compareByDescending<AppInfo> {
            it.packageName in PRIORITY_APPS
        }.thenBy {
            it.name.lowercase()
        }
    )

    /**
     * Add one or more keywords to the database.
     *
     * @param keyword Vararg of [Keyword] objects to be added.
     */
    suspend fun addKeyword(vararg keyword: Keyword) {
        database.keywordDao().insert(*keyword)
    }

    /**
     * Delete a keyword from the database.
     *
     * @param keyword The [Keyword] object to be deleted.
     */
    suspend fun deleteKeyword(keyword: Keyword) {
        database.keywordDao().delete(keyword.word)
    }

    /**
     * Add one or more watched apps to the database.
     *
     * @param app Vararg of [WatchedApp] objects to be added.
     */
    suspend fun addWatchedApp(vararg app: WatchedApp) {
        database.watchedAppDao().insert(*app)
    }

    /**
     * Delete a watched app from the database.
     *
     * @param app The [WatchedApp] object to be deleted.
     */
    suspend fun deleteWatchedApp(app: WatchedApp) {
        database.watchedAppDao().delete(app.packageName)
    }

    /**
     * Get all keyword matches for a specific app.
     *
     * @param packageName The package name of the app for which to retrieve keyword matches.
     */
    fun getKeywordMatchesForApp(packageName: String) = database.keywordMatchDao().getByApp(
        packageName
    )

    /**
     * Add one or more matched keywords to the database.
     *
     * @param match Vararg of [KeywordMatch] objects to be added.
     */
    suspend fun addKeywordMatch(vararg match: KeywordMatch) {
        database.keywordMatchDao().insert(*match)
    }

    /**
     * Delete a matched keyword from the database.
     *
     * @param match The [KeywordMatch] object to be deleted.
     */
    suspend fun deleteKeywordMatch(match: KeywordMatch) {
        database.keywordMatchDao().delete(match)
    }


    /**
     * Mark the onboarding process as completed
     */
    suspend fun onboardingCompleted() {
        localStore.save(ONBOARDING_COMPLETE, true)
    }

    companion object : SingletonCompanion<AppRepository, Context>() {
        override fun createInstance(dependency: Context) = AppRepository(dependency)

        // Key for storing onboarding completion status in DataStore
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

        // Package names of priority apps
        const val WHATSAPP_PACKAGE_NAME = "com.whatsapp"
        const val TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"

        // List of priority apps to be sorted higher in the installed apps list
        private val PRIORITY_APPS = listOf(
            WHATSAPP_PACKAGE_NAME,
            TELEGRAM_PACKAGE_NAME,
            "org.thoughtcrime.securesms", // Signal
            "com.android.mms", // SMS/Messages
            "com.google.android.gm" // Gmail
        )
    }
}