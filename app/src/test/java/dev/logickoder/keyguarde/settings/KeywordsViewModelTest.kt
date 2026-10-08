package dev.logickoder.keyguarde.settings

import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
class KeywordsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = mockk<AppRepository>(relaxed = true) {
        every { keywords } returns flowOf(listOf(Keyword("invoice"), Keyword("rent")))
        every { matchCountsByKeyword } returns flowOf(mapOf("invoice" to 12))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `each keyword carries its match count`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf("invoice", "rent"), state.keywords.map { it.word })
        assertEquals(12, state.matchCounts["invoice"])
        assertEquals(null, state.matchCounts["rent"])
    }

    @Test
    fun `adding from the field saves the keyword`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        viewModel.onAction(KeywordsAction.Add("payment"))
        advanceUntilIdle()

        coVerify { repository.addKeyword(*varargAll { it.word == "payment" }) }
    }
}
