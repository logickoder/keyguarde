package dev.logickoder.keyguarde.settings

import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordStats
import dev.logickoder.keyguarde.settings.domain.KeywordSort
import java.time.Instant
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
        every { statsByKeyword } returns flowOf(mapOf("invoice" to KeywordStats("invoice", 12, Instant.parse("2026-10-01T09:00:00Z"))))
        every { keywordSort } returns flowOf(KeywordSort.RecentMatch)
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
    fun `keywords come with their stats, most recent match first`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf("invoice", "rent"), state.keywords.map { it.word })
        assertEquals(12, state.stats["invoice"]?.count)
        assertEquals(null, state.stats["rent"])
    }

    @Test
    fun `adding from the field saves the keyword`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        viewModel.onAction(KeywordsAction.Add("payment"))
        advanceUntilIdle()

        coVerify { repository.addKeyword(*varargAll { it.word == "payment" }) }
    }

    @Test
    fun `a duplicate or too short word is never saved`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        viewModel.onAction(KeywordsAction.Add("Invoice"))
        viewModel.onAction(KeywordsAction.Add("a"))
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.addKeyword(*anyVararg()) }
    }

    @Test
    fun `an edit that would duplicate another keyword is not saved`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        val invoice = Keyword("invoice")

        viewModel.onAction(KeywordsAction.Edit(invoice))
        viewModel.onAction(KeywordsAction.Save("rent"))
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.updateKeyword(any(), any()) }
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
        assertEquals(listOf(KeywordsEffect.Deleted(rent)), effects)

        viewModel.onAction(KeywordsAction.UndoDelete(rent))
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

    @Test
    fun `undo restores the keyword its snackbar named, not the latest delete`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        val exam = Keyword("exam", createdAt = 1L)
        val rent = Keyword("rent", createdAt = 2L)

        viewModel.onAction(KeywordsAction.Delete(exam))
        viewModel.onAction(KeywordsAction.Delete(rent))
        viewModel.onAction(KeywordsAction.UndoDelete(exam))
        advanceUntilIdle()

        coVerify { repository.addKeyword(exam) }
        coVerify(exactly = 0) { repository.addKeyword(rent) }
    }

    @Test
    fun `picking a sort saves it`() = runTest(dispatcher) {
        val viewModel = KeywordsViewModel(repository)
        viewModel.onAction(KeywordsAction.SetSort(KeywordSort.Alphabetical))
        advanceUntilIdle()

        coVerify { repository.saveKeywordSort(KeywordSort.Alphabetical) }
    }
}
