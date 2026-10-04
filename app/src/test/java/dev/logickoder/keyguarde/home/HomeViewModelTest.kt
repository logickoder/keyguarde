package dev.logickoder.keyguarde.home

import androidx.paging.PagingData
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.home.domain.HomeAction
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val app1 = WatchedApp("com.example.app1", "App 1", "")
    private val app2 = WatchedApp("com.example.app2", "App 2", "")

    private val repository = mockk<AppRepository> {
        every { watchedApps } returns flowOf(listOf(app1, app2))
        every { lastVisitAt } returns flowOf(null)
        every { matchesFilter } returns flowOf(emptySet())
        every { matchCountsByApp } returns flowOf(mapOf(app1.packageName to 3, app2.packageName to 5))
        coEvery { saveMatchesFilter(any()) } just Runs
        every { getMatches(any(), any()) } returns flowOf(PagingData.empty())
    }

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = HomeViewModel(repository, mockk<ResetMatchCountUsecase>(relaxed = true))
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
    fun `matches follow the filter`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.matches.collect {} }

        viewModel.onAction(HomeAction.ShowFilterSheet)
        viewModel.onAction(HomeAction.ToggleFilterApp(app1.packageName))
        viewModel.onAction(HomeAction.ApplyFilter)
        advanceUntilIdle()

        // The paging pipeline runs on Dispatchers.Default (flowOn), outside the test scheduler.
        verify(timeout = 2_000) { repository.getMatches(setOf(app1.packageName), "") }
    }
}
