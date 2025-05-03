package dev.logickoder.keyguarde.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.app.domain.SingletonCompanion
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppStore(
    private val context: Context,
) {
    suspend fun <T> save(key: Preferences.Key<T>, data: T?) {
        context.app.edit { preferences ->
            if (data == null) {
                preferences.remove(key)
            } else preferences[key] = data
        }
    }

    fun <T> get(key: Preferences.Key<T>): Flow<T?> {
        return context.app.data.map { preferences ->
            preferences[key]
        }
    }

    /**
     * Get the data from the [key] and transform it to [R] safely
     */
    fun <T, R> getSafely(
        key: Preferences.Key<T>,
        transform: (T) -> R,
    ): Flow<R?> = get(key).map { data ->
        try {
            data?.let(transform)
        } catch (e: Exception) {
            Napier.e(e) { "Datastore error" }
            null
        }
    }

    suspend fun clear() = context.app.edit { preferences ->
        preferences.clear()
    }

    companion object : SingletonCompanion<AppStore, Context>() {
        private val Context.app: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
            name = BuildConfig.APPLICATION_ID
        )

        override fun createInstance(dependency: Context) = AppStore(dependency)

        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
    }
}