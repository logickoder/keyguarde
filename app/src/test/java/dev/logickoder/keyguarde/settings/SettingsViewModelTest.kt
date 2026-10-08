package dev.logickoder.keyguarde.settings

import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.home.domain.ListenerIssue
import dev.logickoder.keyguarde.onboarding.domain.SetupTest
import dev.logickoder.keyguarde.settings.domain.SettingsAction
import dev.logickoder.keyguarde.settings.domain.ThemeMode
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val keywords = MutableStateFlow(listOf(Keyword("invoice")))
    private val testReceived = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val listenerConnected = MutableStateFlow(true)
    private val caughtCount = MutableStateFlow(0)
    private val ratePromptDone = MutableStateFlow(false)
    private val paused = MutableStateFlow(false)

    private val appRepository = mockk<AppRepository> {
        every { keywords } returns this@SettingsViewModelTest.keywords
        every { caughtCount } returns this@SettingsViewModelTest.caughtCount
        every { installedWatchedAppCount } returns flowOf(2)
    }
    private val settingsRepository = mockk<SettingsRepository>(relaxed = true) {
        every { showHeadsUpAlert } returns flowOf(true)
        every { usePersistentSilentNotification } returns flowOf(false)
        every { resetMatchCountOnAppOpen } returns flowOf(false)
        every { themeMode } returns flowOf(ThemeMode.Dark)
        every { ratePromptDone } returns this@SettingsViewModelTest.ratePromptDone
        every { isPaused } returns this@SettingsViewModelTest.paused
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = SettingsViewModel(
        appRepository = appRepository,
        settingsRepository = settingsRepository,
        listenerConnected = listenerConnected,
        setupTestReceived = testReceived,
    )

    @Test
    fun `state shows saved settings and the first keyword to test with`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.watchedAppCount)
        assertEquals("invoice", state.testKeyword)
        assertEquals(ThemeMode.Dark, state.themeMode)
        assertEquals(true, state.showHeadsUpAlert)
        assertEquals(false, state.usePersistentNotification)
    }

    @Test
    fun `no keywords means nothing to test with`() = runTest(dispatcher) {
        keywords.value = emptyList()
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        assertNull(viewModel.state.value.testKeyword)
    }

    @Test
    fun `access off shows as a listener issue`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        viewModel.onAction(SettingsAction.SystemChecked(false, notificationsAllowed = true, isBatteryUnrestricted = true))
        advanceUntilIdle()

        assertEquals(ListenerIssue.AccessOff, viewModel.state.value.listenerIssue)
    }

    @Test
    fun `the test is caught when the listener reports a keyword back`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(SettingsAction.TestSent)
        advanceTimeBy(100)
        assertEquals(SetupTest.Waiting, viewModel.state.value.test)

        testReceived.emit("Testing Keyguarde: does it catch “invoice”?")
        advanceTimeBy(100)
        assertEquals(SetupTest.Caught("invoice"), viewModel.state.value.test)
    }

    @Test
    fun `the test is missed when nothing comes back in time`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        viewModel.onAction(SettingsAction.TestSent)
        advanceTimeBy(6_000)

        assertEquals(SetupTest.Missed, viewModel.state.value.test)
    }

    @Test
    fun `picking a theme saves it`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.onAction(SettingsAction.SetThemeMode(ThemeMode.Light))
        advanceUntilIdle()

        coVerify { settingsRepository.setThemeMode(ThemeMode.Light) }
    }

    @Test
    fun `the rate prompt waits for ten catches`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        caughtCount.value = 9
        advanceUntilIdle()
        assertFalse(viewModel.state.value.showRatePrompt)

        caughtCount.value = 10
        advanceUntilIdle()
        assertTrue(viewModel.state.value.showRatePrompt)
    }

    @Test
    fun `the rate prompt never returns once rated or dismissed`() = runTest(dispatcher) {
        caughtCount.value = 50
        ratePromptDone.value = true
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.state.value.showRatePrompt)
    }

    @Test
    fun `the rate prompt hides while a listener problem shows`() = runTest(dispatcher) {
        caughtCount.value = 50
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        viewModel.onAction(SettingsAction.SystemChecked(false, notificationsAllowed = true, isBatteryUnrestricted = true))
        advanceUntilIdle()

        assertFalse(viewModel.state.value.showRatePrompt)
    }

    @Test
    fun `dismissing the rate prompt saves it`() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.onAction(SettingsAction.RatePromptDone)
        advanceUntilIdle()

        coVerify { settingsRepository.markRatePromptDone() }
    }

    @Test
    fun `the rate prompt still shows when battery use is restricted`() = runTest(dispatcher) {
        caughtCount.value = 50
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        viewModel.onAction(SettingsAction.SystemChecked(true, notificationsAllowed = true, isBatteryUnrestricted = false))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.showRatePrompt)
    }

    @Test
    fun `pausing saves it and hides the rate prompt`() = runTest(dispatcher) {
        caughtCount.value = 50
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.state.collect {} }
        viewModel.onAction(SettingsAction.SetPaused(true))
        paused.value = true
        advanceUntilIdle()

        coVerify { settingsRepository.setPaused(true) }
        assertTrue(viewModel.state.value.isPaused)
        assertFalse(viewModel.state.value.showRatePrompt)
    }
}
