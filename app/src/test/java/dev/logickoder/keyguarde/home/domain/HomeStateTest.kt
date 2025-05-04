package dev.logickoder.keyguarde.home.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeStateTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var repository: AppRepository

    private lateinit var homeState: HomeState
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        every { repository.watchedApps } returns flowOf(
            listOf(
                WatchedApp("com.example.app1", "App 1"),
                WatchedApp("com.example.app2", "App 2")
            )
        )
        every { repository.matches } returns flowOf(
            listOf()
        )

        homeState = HomeState(
            context = TestContext(),
            scope = testScope,
        )
    }

    @Test
    fun `watchedApps should return data from repository`() = runTest {
        val watchedApps = homeState.watchedApps.value
        assertEquals(2, watchedApps.size)
        assertEquals("App 1", watchedApps[0].name)
    }

    @Test
    fun `onFilterChange should update filter`() = runTest {
        val app = WatchedApp("com.example.app1", "App 1")
        homeState.onFilterChange(app)
        assertEquals(app, homeState.filter)
    }

    @Test
    fun `matches should return filtered data`() = runTest {
        val app = WatchedApp("com.example.app1", "App 1")
        every { repository.getKeywordMatchesForApp(app.packageName) } returns flowOf(
            listOf()
        )

        homeState.onFilterChange(app)
        val matches = homeState.matches.value
        assertEquals(0, matches.size)
    }
}