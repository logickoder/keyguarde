package dev.logickoder.keyguarde.settings

import androidx.datastore.preferences.core.Preferences
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.settings.domain.ThemeMode
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryTest {

    private fun repository(savedTheme: String?) = SettingsRepository(
        mockk<AppStore> {
            every { get(any<Preferences.Key<Any>>()) } returns flowOf(savedTheme)
        }
    )

    @Test
    fun `theme follows the system until the user picks one`() = runTest {
        assertEquals(ThemeMode.System, repository(null).themeMode.first())
    }

    @Test
    fun `a saved theme is restored`() = runTest {
        assertEquals(ThemeMode.Dark, repository("Dark").themeMode.first())
    }

    @Test
    fun `an unknown saved theme falls back to the system`() = runTest {
        assertEquals(ThemeMode.System, repository("Sepia").themeMode.first())
    }
}
