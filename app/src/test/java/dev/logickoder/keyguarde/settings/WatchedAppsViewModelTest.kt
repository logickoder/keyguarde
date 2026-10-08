package dev.logickoder.keyguarde.settings

import android.content.Context
import android.graphics.drawable.Drawable
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.settings.domain.WatchedAppsEffect
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchedAppsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val context = mockk<Context>(relaxed = true)
    private val watched = MutableStateFlow(emptyList<WatchedApp>())

    private val repository = mockk<AppRepository>(relaxed = true) {
        every { getInstalledApps(any()) } returns listOf(installed("com.whatsapp", "WhatsApp"), installed("org.telegram.messenger", "Telegram"))
        every { watchedApps } returns watched
    }

    private fun installed(packageName: String, name: String) = AppInfo(name, packageName, mockk<Drawable>())

    private fun watched(packageName: String, name: String) = WatchedApp(packageName, name, "")

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.loadedViewModel(): WatchedAppsViewModel {
        val viewModel = WatchedAppsViewModel(repository, backgroundDispatcher = dispatcher)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()
        return viewModel
    }

    @Test
    fun `watched apps are listed even when their notifications are blocked`() = runTest(dispatcher) {
        watched.value = listOf(watched("com.whatsapp", "WhatsApp"))
        loadedViewModel()

        verify { repository.getInstalledApps(setOf("com.whatsapp")) }
    }

    @Test
    fun `the last installed app can't be unticked`() = runTest(dispatcher) {
        watched.value = listOf(watched("com.whatsapp", "WhatsApp"))
        val viewModel = loadedViewModel()
        val effects = mutableListOf<WatchedAppsEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }

        viewModel.toggleApp(context, "com.whatsapp")
        advanceUntilIdle()

        assertEquals(listOf(WatchedAppsEffect.LastAppKept("WhatsApp")), effects)
        coVerify(exactly = 0) { repository.deleteWatchedApp(any()) }
    }

    @Test
    fun `an app can be unticked while another installed app stays`() = runTest(dispatcher) {
        watched.value = listOf(watched("com.whatsapp", "WhatsApp"), watched("org.telegram.messenger", "Telegram"))
        val viewModel = loadedViewModel()

        viewModel.toggleApp(context, "com.whatsapp")
        advanceUntilIdle()

        coVerify { repository.deleteWatchedApp("com.whatsapp") }
    }

    @Test
    fun `an app that isn't installed can always be unticked`() = runTest(dispatcher) {
        watched.value = listOf(watched("com.whatsapp", "WhatsApp"), watched("org.thoughtcrime.securesms", "Signal"))
        val viewModel = loadedViewModel()

        viewModel.toggleApp(context, "org.thoughtcrime.securesms")
        advanceUntilIdle()

        coVerify { repository.deleteWatchedApp("org.thoughtcrime.securesms") }
        assertEquals(1, viewModel.state.first().missingApps.size)
    }
}
