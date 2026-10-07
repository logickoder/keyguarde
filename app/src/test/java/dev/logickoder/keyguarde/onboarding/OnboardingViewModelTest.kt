package dev.logickoder.keyguarde.onboarding

import androidx.lifecycle.SavedStateHandle
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.onboarding.domain.OnboardingAction
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val repository = mockk<AppRepository> {
        coEvery { getInstalledApps() } returns emptyList()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(pages: List<String>? = null) = OnboardingViewModel(
        repository = repository,
        savedStateHandle = SavedStateHandle(pages?.let { mapOf("pages" to ArrayList(it)) }.orEmpty()),
    )

    @Test
    fun `setup starts on the intro`() {
        assertEquals(OnboardingPage.Intro, viewModel().state.value.currentPage)
    }

    @Test
    fun `steps run intro, keywords, apps, access, test`() {
        val viewModel = viewModel()
        val seen = mutableListOf(viewModel.state.value.currentPage)
        repeat(OnboardingPage.entries.size - 1) {
            viewModel.onAction(OnboardingAction.Next)
            seen += viewModel.state.value.currentPage
        }

        assertEquals(OnboardingPage.entries, seen)
    }

    @Test
    fun `saved progress is restored`() {
        val viewModel = viewModel(pages = listOf("Intro", "Keywords"))

        assertEquals(OnboardingPage.Keywords, viewModel.state.value.currentPage)
    }

    @Test
    fun `pages saved by an older version are dropped instead of crashing`() {
        val viewModel = viewModel(pages = listOf("Intro", "HowItWorks", "Permissions"))

        assertEquals(listOf(OnboardingPage.Intro), viewModel.state.value.backStack)
    }

    @Test
    fun `nothing restorable falls back to the intro`() {
        val viewModel = viewModel(pages = listOf("Welcome"))

        assertEquals(OnboardingPage.Intro, viewModel.state.value.currentPage)
    }
}
