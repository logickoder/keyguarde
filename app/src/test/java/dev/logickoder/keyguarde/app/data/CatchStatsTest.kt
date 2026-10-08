package dev.logickoder.keyguarde.app.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.logickoder.keyguarde.app.data.dao.KeywordMatchDao
import dev.logickoder.keyguarde.app.data.model.AppMatchCount
import dev.logickoder.keyguarde.app.data.model.CatchStats
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CatchStatsTest {

    private val saved = listOf(AppMatchCount("com.whatsapp", 4), AppMatchCount("org.telegram.messenger", 2))

    // A tiny in-memory DataStore: reads see every write.
    private val preferences = MutableStateFlow<Preferences>(mutablePreferencesOf())

    private val store = mockk<AppStore> {
        every { get(any<Preferences.Key<Any>>()) } answers {
            val key = firstArg<Preferences.Key<Any>>()
            preferences.map { it[key] }
        }
        coEvery { edit(any()) } coAnswers {
            val transform = firstArg<suspend (MutablePreferences) -> Unit>()
            val next = preferences.value.toMutablePreferences()
            transform(next)
            preferences.update { next }
        }
    }

    private val repository = AppRepository(
        context = mockk<Context>(),
        localStore = store,
        database = mockk(relaxed = true) {
            every { keywordMatchDao() } returns mockk<KeywordMatchDao> {
                every { countByApp() } returns flowOf(saved)
            }
        },
    )

    @Test
    fun `before the first catch, stats start from the saved matches`() = runTest {
        assertEquals(CatchStats(messages = 6, apps = 2), repository.catchStats.first())
    }

    @Test
    fun `the first catch seeds from the saved matches, which already include it`() = runTest {
        repository.recordCatch("com.whatsapp")

        assertEquals(CatchStats(messages = 6, apps = 2), repository.catchStats.first())
        assertEquals(6, preferences.value[intPreferencesKey("caught_count")])
    }

    @Test
    fun `later catches count up and add new apps once`() = runTest {
        repository.recordCatch("com.whatsapp")
        repository.recordCatch("com.whatsapp")
        repository.recordCatch("org.thoughtcrime.securesms")

        assertEquals(CatchStats(messages = 8, apps = 3), repository.catchStats.first())
        assertEquals(3, preferences.value[stringSetPreferencesKey("caught_apps")]?.size)
    }
}
