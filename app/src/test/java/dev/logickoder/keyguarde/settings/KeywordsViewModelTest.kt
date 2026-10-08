package dev.logickoder.keyguarde.settings

import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import dev.logickoder.keyguarde.settings.domain.KeywordsEffect
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
import org.junit.Assert.assertNull
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

    @Test
    fun `deleting shows an undo that puts the keyword back in its place`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        val effects = mutableListOf<KeywordsEffect>()
        backgroundScope.launch { viewModel.effects.collect { effects += it } }
        val rent = Keyword("rent", createdAt = 42L)

        viewModel.onAction(KeywordsAction.Delete(rent))
        advanceUntilIdle()
        coVerify { repository.deleteKeyword(rent) }
        assertEquals(listOf(KeywordsEffect.Deleted("rent")), effects)

        viewModel.onAction(KeywordsAction.UndoDelete)
        advanceUntilIdle()
        coVerify { repository.addKeyword(rent) }
    }

    @Test
    fun `saving an edit renames the keyword and closes the sheet`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        backgroundScope.launch { viewModel.state.collect {} }
        val invoice = Keyword("invoice", createdAt = 1L)

        viewModel.onAction(KeywordsAction.Edit(invoice))
        advanceUntilIdle()
        assertEquals(invoice, viewModel.state.value.editing)

        viewModel.onAction(KeywordsAction.Save("invoices"))
        advanceUntilIdle()
        coVerify { repository.updateKeyword(invoice, match { it.word == "invoices" }) }
        assertNull(viewModel.state.value.editing)
    }

    @Test
    fun `saving an unchanged keyword writes nothing`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        val invoice = Keyword("invoice")

        viewModel.onAction(KeywordsAction.Edit(invoice))
        viewModel.onAction(KeywordsAction.Save("invoice"))
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.updateKeyword(any(), any()) }
    }
}
