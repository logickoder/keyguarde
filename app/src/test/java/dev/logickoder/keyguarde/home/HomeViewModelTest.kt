package dev.logickoder.keyguarde.home

import androidx.paging.PagingData
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import dev.logickoder.keyguarde.app.domain.usecase.ResetMatchCountUsecase
import dev.logickoder.keyguarde.home.domain.HomeAction
import io.mockk.every
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val app1 = WatchedApp("com.example.app1", "App 1", "")
    private val app2 = WatchedApp("com.example.app2", "App 2", "")

    private val repository = mockk<AppRepository> {
        every { watchedApps } returns flowOf(listOf(app1, app2))
        every { recentMatchCount } returns flowOf(0)
        every { lastVisitAt } returns flowOf(null)
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
    fun `changing the filter updates state`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.state.collect {} }

        viewModel.onAction(HomeAction.FilterChanged(app1))
        advanceUntilIdle()

        assertEquals(app1, viewModel.state.value.filter)
    }

    @Test
    fun `matches follow the filter`() = runTest(dispatcher) {
        backgroundScope.launch { viewModel.matches.collect {} }

        viewModel.onAction(HomeAction.FilterChanged(app1))
        advanceUntilIdle()

        // The paging pipeline runs on Dispatchers.Default (flowOn), outside the test scheduler.
        verify(timeout = 2_000) { repository.getMatches(app1.packageName, "") }
    }
}
