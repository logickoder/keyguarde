package dev.logickoder.keyguarde.app.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.SingletonCompanion
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
     * Get the count of recent matches from the local store.
     */
    val recentMatchCount = localStore.get(RECENT_MATCH_COUNT).map { it ?: 0 }

    /**
     * Get the recent chats from the local store.
     */
    val recentChats = localStore.get(RECENT_CHATS).map { it ?: emptySet() }

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
            // Exclude the current app from the list
            if (app.packageName == context.packageName) {
                continue
            }

            var passesPermissionCheck = true

            // Only check for POST_NOTIFICATIONS on Android 13 (API 33) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (packageManager.checkPermission(
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        app.packageName
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    passesPermissionCheck = false
                }
            }

            if (passesPermissionCheck) {
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
     * Update an existing keyword in the database.
     *
     * @param oldKeyword The [Keyword] object to be updated.
     * @param newKeyword The new [Keyword] object with updated values.
     */
    suspend fun updateKeyword(oldKeyword: Keyword, newKeyword: Keyword) {
        database.keywordDao().update(oldKeyword, newKeyword)
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
     * @param packageName The packageName of the [WatchedApp] object to be deleted.
     */
    suspend fun deleteWatchedApp(packageName: String) {
        database.watchedAppDao().delete(packageName)
    }

    /**
     * Get the count of keyword matches.
     *
     * @return The count of keyword matches.
     */
    suspend fun getMatchesCount() = database.keywordMatchDao().getSize()

    /**
     * Get all keyword matches.
     *
     * @param packageName (Optional) The package name of the app for which to retrieve keyword matches.
     *
     * @return A [Flow] emitting [PagingData] of [KeywordMatch] objects.
     */
    fun getMatches(packageName: String? = null): Flow<PagingData<KeywordMatch>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            database.keywordMatchDao().getMatches(packageName?.takeIf { it.isNotBlank() })
        }
    ).flow

    /**
     * Add matched keywords to the database.
     *
     * @param match [KeywordMatch] object to be added.
     */
    suspend fun addKeywordMatch(match: KeywordMatch): Long {
        return database.keywordMatchDao().insert(match)
    }

    /**
     * Delete a matched keyword from the database.
     *
     * @param match The [KeywordMatch] object to be deleted.
     */
    suspend fun deleteKeywordMatch(vararg match: KeywordMatch) {
        database.keywordMatchDao().delete(*match)
    }

    /**
     * Clear all matches from the database.
     */
    suspend fun clearMatches() {
        database.keywordMatchDao().clear()
    }

    /**
     * Mark the onboarding process as completed
     */
    suspend fun onboardingCompleted() {
        localStore.save(ONBOARDING_COMPLETE, true)
    }

    /**
     * Update the count of recent matches in the DataStore.
     */
    suspend fun updateRecentMatchCount(count: Int) {
        localStore.save(RECENT_MATCH_COUNT, count)
    }

    /**
     * Update the recent chats in the DataStore.
     */
    suspend fun updateRecentChats(chats: Set<String>) {
        localStore.save(RECENT_CHATS, chats)
    }

    companion object : SingletonCompanion<AppRepository, Context>() {
        override fun createInstance(dependency: Context) = AppRepository(dependency)

        // Key for storing onboarding completion status in DataStore
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

        private val RECENT_MATCH_COUNT = intPreferencesKey("recent_match_count")

        private val RECENT_CHATS = stringSetPreferencesKey("recent_chats")

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
