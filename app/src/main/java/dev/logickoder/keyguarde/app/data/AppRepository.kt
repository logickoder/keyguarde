package dev.logickoder.keyguarde.app.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.settings.domain.KeywordSort
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Repository for managing Keyguarde data.
 * This class acts as a single source of truth for accessing and managing data
 * from both the local database (Room) and the local store (DataStore).
 */
class AppRepository(
    private val context: Context,
    private val localStore: AppStore,
    private val database: AppDatabase,
) {

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
     * How many watched apps are installed now. An uninstalled app stays watched, in case it comes
     * back, but can't send anything, so it isn't counted.
     */
    val installedWatchedAppCount: Flow<Int> = watchedApps.map { apps ->
        apps.count { app ->
            try {
                context.packageManager.getApplicationInfo(app.packageName, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get the count of recent matches from the local store.
     */
    val recentMatchCount = localStore.get(RECENT_MATCH_COUNT).map { it ?: 0 }

    /**
     * Get the recent chats from the local store.
     */
    val recentChats = localStore.get(RECENT_CHATS).map { it ?: emptySet() }

    /**
     * When the user last left the app, or null before the first visit ends.
     */
    val lastVisitAt = localStore.get(LAST_VISIT_AT).map { seconds ->
        seconds?.let(Instant::ofEpochSecond)
    }

    /**
     * Record that the user is leaving the app now, so the next visit can tell what's new.
     */
    suspend fun markVisited() {
        // Same encoding as the timestamp column (Converters), so comparisons line up.
        localStore.save(LAST_VISIT_AT, Instant.now().epochSecond)
    }

    /**
     * The apps the Matches list is filtered to; empty means every app. Kept across launches.
     */
    val matchesFilter: Flow<Set<String>> = localStore.get(MATCHES_FILTER).map { it.orEmpty() }

    suspend fun saveMatchesFilter(packageNames: Set<String>) {
        localStore.save(MATCHES_FILTER, packageNames.takeIf { it.isNotEmpty() })
    }

    /**
     * How many matches each app has, keyed by package name.
     */
    val matchCountsByApp: Flow<Map<String, Int>> = database.keywordMatchDao().countByApp().map { counts ->
        counts.associate { it.app to it.count }
    }

    /**
     * Every message caught since install; deleting matches doesn't lower it. Until the first catch
     * after this shipped, it starts from the matches saved, so existing users don't see zero.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val caughtCount: Flow<Int> = localStore.get(CAUGHT_COUNT).flatMapLatest { count ->
        when (count) {
            null -> matchCountsByApp.map { saved -> saved.values.sum() }
            // Once counted, the saved matches no longer matter; stop re-running their query.
            else -> flowOf(count)
        }
    }

    /**
     * Counts a match the listener just saved from [chat], in one write: the lifetime count, and
     * the recent count and chats the persistent notification shows. Call after the insert, so a
     * first-time seed of the lifetime count from the database already includes it.
     */
    suspend fun recordCatch(chat: String) {
        localStore.edit { preferences ->
            preferences[CAUGHT_COUNT] = when (val count = preferences[CAUGHT_COUNT]) {
                null -> database.keywordMatchDao().countByApp().first().sumOf { it.count }
                else -> count + 1
            }
            preferences[RECENT_MATCH_COUNT] = (preferences[RECENT_MATCH_COUNT] ?: 0) + 1
            preferences[RECENT_CHATS] = preferences[RECENT_CHATS].orEmpty() + chat
        }
    }

    /**
     * Match count and last match per keyword, keyed by the lowercased word.
     */
    val statsByKeyword: Flow<Map<String, KeywordStats>> = database.keywordMatchDao().statsByKeyword().map { stats ->
        stats.associateBy { it.word }
    }

    /** How the Keywords tab orders its list. An unknown saved name falls back to recent match. */
    val keywordSort: Flow<KeywordSort> = localStore.get(KEYWORD_SORT).map { name ->
        KeywordSort.entries.firstOrNull { it.name == name } ?: KeywordSort.RecentMatch
    }

    suspend fun saveKeywordSort(sort: KeywordSort) = localStore.save(KEYWORD_SORT, sort.name)

    /**
     * Copies of matches, taken before a delete so it can be undone.
     */
    suspend fun getMatchesByIds(ids: List<Long>): List<KeywordMatch> =
        database.keywordMatchDao().getByIds(ids)

    suspend fun getAllMatches(): List<KeywordMatch> = database.keywordMatchDao().getAll()

    /**
     * Undo a delete: put [matches] back with their original ids.
     */
    suspend fun restoreMatches(matches: List<KeywordMatch>) {
        database.keywordMatchDao().restore(matches)
    }

    /**
     * Count matches newer than [since], limited to [packageNames] unless it's empty.
     */
    fun countMatchesSince(since: Instant, packageNames: Set<String>): Flow<Int> =
        database.keywordMatchDao().countSince(since, packageNames.isEmpty(), packageNames)

    /**
     * Get all installed apps that can post notifications on the device.
     * Filters out the current app and sorts the apps by priority and name.
     *
     * @return A sorted list of [AppInfo] containing app name, package name, and icon.
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledApps() = buildList {
        val packageManager = context.packageManager
        // Only apps a person opens; system services and plugins can't send chat messages. One
        // launcher query, instead of a launch-intent lookup per installed package.
        val launchable = packageManager
            .queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            .mapTo(HashSet()) { it.activityInfo.packageName }
        for (app in packageManager.getInstalledApplications(0)) {
            if (app.packageName !in launchable) {
                continue
            }

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
        // Matches cascade with the app, so its cached avatars go too.
        deleteChatAvatars(context, packageName)
    }

    /**
     * Get all keyword matches.
     *
     * @param packageNames The apps to show matches from; empty means every app.
     * @param query (Optional) The query to search for in the keyword matches.
     *
     * @return A [Flow] emitting [PagingData] of [KeywordMatch] objects.
     */
    fun getMatches(
        packageNames: Set<String> = emptySet(),
        query: String = "",
        keyword: String? = null,
    ): Flow<PagingData<KeywordMatch>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            val queryFilter = query.takeIf { it.isNotBlank() }?.let {
                it.split(" ").filter { term ->
                    term.isNotBlank()
                }.joinToString(" ") { term -> "$term*" }
            }
            database.keywordMatchDao().getMatches(packageNames.isEmpty(), packageNames, queryFilter, keyword?.lowercase())
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
     * Delete keyword matches by their IDs.
     *
     * @param ids The IDs of the keyword matches to delete.
     */
    suspend fun deleteKeywordMatches(ids: List<Long>) {
        database.keywordMatchDao().delete(ids)
    }

    /**
     * Clear all matches from the database.
     *
     * @return The number of rows deleted.
     */
    suspend fun clearMatches(): Int {
        return database.keywordMatchDao().clear().also {
            deleteChatAvatars(context)
        }
    }

    /**
     * Mark the onboarding process as completed
     */
    suspend fun onboardingCompleted() {
        localStore.save(ONBOARDING_COMPLETE, true)
    }

    /** Starts the recent count over, in one write. */
    suspend fun resetRecentMatches() {
        localStore.edit { preferences ->
            preferences[RECENT_MATCH_COUNT] = 0
            preferences[RECENT_CHATS] = emptySet()
        }
    }

    companion object {
        // Key for storing onboarding completion status in DataStore
        private val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

        private val RECENT_MATCH_COUNT = intPreferencesKey("recent_match_count")

        private val RECENT_CHATS = stringSetPreferencesKey("recent_chats")

        private val LAST_VISIT_AT = longPreferencesKey("last_visit_at")

        private val MATCHES_FILTER = stringSetPreferencesKey("matches_filter_apps")

        private val CAUGHT_COUNT = intPreferencesKey("caught_count")

        private val KEYWORD_SORT = stringPreferencesKey("keyword_sort")

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
