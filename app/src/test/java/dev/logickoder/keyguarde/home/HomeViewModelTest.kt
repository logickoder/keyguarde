package dev.logickoder.keyguarde.home

import androidx.paging.PagingData
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.SystemState
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.home.domain.HomeEffect
import dev.logickoder.keyguarde.home.domain.ListenerIssue
import dev.logickoder.keyguarde.settings.SettingsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val app1 = WatchedApp("com.example.app1", "App 1", "")
    private val app2 = WatchedApp("com.example.app2", "App 2", "")
    private val match = KeywordMatch(
        id = 7,
        keywords = setOf("rent"),
        message = "Rent is due",
        chat = "Landlord",
        app = app1.packageName,
        timestamp = Instant.parse("2026-10-01T09:00:00Z"),
    )

    // Stands in for the saved setting: what's saved is what's read back.
    private val savedFilter = MutableStateFlow<Set<String>>(emptySet())

    private val repository = mockk<AppRepository> {
        every { watchedApps } returns flowOf(listOf(app1, app2))
        every { lastVisitAt } returns flowOf(null)
        every { matchesFilter } returns savedFilter
        every { matchCountsByApp } returns flowOf(mapOf(app1.packageName to 3, app2.packageName to 5))
        coEvery { saveMatchesFilter(any()) } answers { savedFilter.value = firstArg() }
        every { getMatches(any(), any(), any()) } returns flowOf(PagingData.empty())
    }

    private val listenerConnected = MutableStateFlow(false)
    private val system = MutableStateFlow(SystemState())
    private val paused = MutableStateFlow(false)
    private val settings = mockk<SettingsRepository>(relaxed = true) {
        every { isPaused } returns paused
    }

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = HomeViewModel(repository, mockk<ResetMatchCountUsecase>(relaxed = true), settings, system, listenerConnected)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `watched apps come from the repository`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        val watchedApps = viewModel.state.value.watchedApps
        assertEquals(2, watchedApps.size)
        assertEquals("App 1", watchedApps[0].name)
    }

    @Test
    fun `applying ticked apps updates the filter and saves it`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        viewModel.onAction(HomeAction.ShowFilterSheet)
        viewModel.onAction(HomeAction.ToggleFilterApp(app1.packageName))
        viewModel.onAction(HomeAction.ToggleFilterApp(app2.packageName))
        viewModel.onAction(HomeAction.ApplyFilter)
        advanceUntilIdle()

        assertEquals(listOf(app1, app2), viewModel.state.value.filter)
        assertFalse(viewModel.state.value.isFilterSheetVisible)
        coVerify { repository.saveMatchesFilter(setOf(app1.packageName, app2.packageName)) }
    }

    @Test
    fun `dismissing the sheet discards ticked apps`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        viewModel.onAction(HomeAction.ShowFilterSheet)
        viewModel.onAction(HomeAction.ToggleFilterApp(app1.packageName))
        viewModel.onAction(HomeAction.DismissFilterSheet)
        viewModel.onAction(HomeAction.ShowFilterSheet)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.filter.isEmpty())
        assertTrue(viewModel.state.value.filterDraft.isEmpty())
    }

    @Test
    fun `applying with nothing ticked shows every app`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        viewModel.onAction(HomeAction.ShowFilterSheet)
        viewModel.onAction(HomeAction.ToggleFilterApp(app1.packageName))
        viewModel.onAction(HomeAction.ApplyFilter)
        viewModel.onAction(HomeAction.ShowFilterSheet)
        viewModel.onAction(HomeAction.ClearFilterDraft)
        viewModel.onAction(HomeAction.ApplyFilter)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.filter.isEmpty())
    }

    @Test
    fun `match counts per app come from the repository`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        assertEquals(5, viewModel.state.value.matchCounts[app2.packageName])
    }

    @Test
    fun `opening a match shows it and dismissing closes it`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        viewModel.onAction(HomeAction.OpenMatch(match))
        advanceUntilIdle()
        assertEquals(match, viewModel.state.value.openMatch)

        viewModel.onAction(HomeAction.DismissMatch)
        advanceUntilIdle()
        assertNull(viewModel.state.value.openMatch)
    }

    @Test
    fun `deleting from the sheet closes it and offers undo`() = runTest(dispatcher) {
        coEvery { repository.getMatchesByIds(listOf(match.id)) } returns listOf(match)
        coEvery { repository.deleteKeywordMatches(any()) } just Runs
        coEvery { repository.restoreMatches(any()) } just Runs
        backgroundScope.launch { viewModel.state.collect {} }
        val effects = mutableListOf<HomeEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }

        viewModel.onAction(HomeAction.OpenMatch(match))
        viewModel.onAction(HomeAction.DeleteMatch(match))
        advanceUntilIdle()

        assertNull(viewModel.state.value.openMatch)
        assertEquals(listOf<HomeEffect>(HomeEffect.MatchesDeleted(listOf(match))), effects)

        viewModel.onAction(HomeAction.UndoDelete(listOf(match)))
        advanceUntilIdle()
        coVerify { repository.restoreMatches(listOf(match)) }
    }

    @Test
    fun `launching the app closes the sheet and hands the launch to the screen`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }
        val effects = mutableListOf<HomeEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }

        viewModel.onAction(HomeAction.OpenMatch(match))
        viewModel.onAction(HomeAction.LaunchApp(app1.packageName))
        advanceUntilIdle()

        assertNull(viewModel.state.value.openMatch)
        assertEquals(listOf<HomeEffect>(HomeEffect.LaunchApp(app1.packageName)), effects)
    }

    @Test
    fun `an unbound listener is reported stopped only after the grace period`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        advanceTimeBy(1_000)
        assertEquals(ListenerIssue.None, viewModel.state.value.listenerIssue)

        advanceUntilIdle()
        assertEquals(ListenerIssue.Stopped, viewModel.state.value.listenerIssue)

        listenerConnected.value = true
        advanceUntilIdle()
        assertEquals(ListenerIssue.None, viewModel.state.value.listenerIssue)
    }

    @Test
    fun `a restart request holds the warning back while the listener binds`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()
        assertEquals(ListenerIssue.Stopped, viewModel.state.value.listenerIssue)

        viewModel.onAction(HomeAction.ListenerRestartRequested)
        advanceTimeBy(1_000)
        assertEquals(ListenerIssue.None, viewModel.state.value.listenerIssue)

        advanceUntilIdle()
        assertEquals(ListenerIssue.StillStopped, viewModel.state.value.listenerIssue)
    }

    @Test
    fun `connecting clears a failed restart`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }
        viewModel.onAction(HomeAction.ListenerRestartRequested)
        advanceUntilIdle()
        assertEquals(ListenerIssue.StillStopped, viewModel.state.value.listenerIssue)

        listenerConnected.value = true
        advanceUntilIdle()
        listenerConnected.value = false
        advanceUntilIdle()

        assertEquals(ListenerIssue.Stopped, viewModel.state.value.listenerIssue)
    }

    @Test
    fun `access off and blocked alerts come from the system state`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        system.value = SystemState(hasListenerAccess = false, notificationsAllowed = false)
        advanceUntilIdle()

        assertEquals(ListenerIssue.AccessOff, viewModel.state.value.listenerIssue)
        assertFalse(viewModel.state.value.notificationsAllowed)
    }

    @Test
    fun `the list waits for the saved filter before loading`() = runTest(dispatcher) {
        savedFilter.value = setOf(app2.packageName)
        viewModel = HomeViewModel(repository, mockk<ResetMatchCountUsecase>(relaxed = true), settings, system, listenerConnected)
        backgroundScope.launch { viewModel.matches.collect {} }
        advanceUntilIdle()

        verify(timeout = 2_000) { repository.getMatches(setOf(app2.packageName), "", null) }
        verify(exactly = 0) { repository.getMatches(emptySet(), "", null) }
    }

    @Test
    fun `matches follow the filter`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.matches.collect {} }

        viewModel.onAction(HomeAction.ShowFilterSheet)
        viewModel.onAction(HomeAction.ToggleFilterApp(app1.packageName))
        viewModel.onAction(HomeAction.ApplyFilter)
        advanceUntilIdle()

        // The paging pipeline runs on Dispatchers.Default (flowOn), outside the test scheduler.
        verify(timeout = 2_000) { repository.getMatches(setOf(app1.packageName), "", null) }
    }

    @Test
    fun `a pause set in Settings shows on Matches and Resume clears it`() = runTest {
        backgroundScope.launch { viewModel.state.collect {} }
        paused.value = true
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isPaused)

        viewModel.onAction(HomeAction.Resume)
        advanceUntilIdle()

        coVerify { settings.setPaused(false) }
    }

    @Test
    fun `see matches limits the list to that keyword until cleared`() = runTest {
        backgroundScope.launch { viewModel.matches.collect {} }
        backgroundScope.launch { viewModel.state.collect {} }
        viewModel.onAction(HomeAction.FilterByKeyword("invoice"))
        advanceUntilIdle()

        verify(timeout = 2_000) { repository.getMatches(any(), "", "invoice") }
        assertEquals("invoice", viewModel.state.value.keywordFilter)

        viewModel.onAction(HomeAction.ClearKeywordFilter)
        advanceUntilIdle()
        assertNull(viewModel.state.value.keywordFilter)
    }

    @Test
    fun `each undo restores the matches its own delete removed`() = runTest(dispatcher) {
        val other = match.copy(id = 8, message = "Rent again")
        coEvery { repository.getMatchesByIds(listOf(match.id)) } returns listOf(match)
        coEvery { repository.getMatchesByIds(listOf(other.id)) } returns listOf(other)
        coEvery { repository.deleteKeywordMatches(any()) } just Runs
        coEvery { repository.restoreMatches(any()) } just Runs
        val effects = mutableListOf<HomeEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }

        viewModel.onAction(HomeAction.DeleteMatch(match))
        viewModel.onAction(HomeAction.DeleteMatch(other))
        advanceUntilIdle()
        val first = effects.filterIsInstance<HomeEffect.MatchesDeleted>().first()
        viewModel.onAction(HomeAction.UndoDelete(first.matches))
        advanceUntilIdle()

        coVerify { repository.restoreMatches(listOf(match)) }
        coVerify(exactly = 0) { repository.restoreMatches(listOf(other)) }
    }
}
