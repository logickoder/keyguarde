package dev.logickoder.keyguarde.onboarding

import android.graphics.drawable.Drawable
import androidx.lifecycle.SavedStateHandle
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import dev.logickoder.keyguarde.onboarding.domain.OnboardingAction
import dev.logickoder.keyguarde.onboarding.domain.OnboardingPage
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private var installed = emptyList<AppInfo>()
    private val repository = mockk<AppRepository> {
        coEvery { getInstalledApps() } answers { installed }
    }

    private fun app(packageName: String) = AppInfo(packageName, packageName, mockk<Drawable>())

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(pages: List<String>? = null, selectedApps: List<String>? = null) = OnboardingViewModel(
        repository = repository,
        backgroundDispatcher = dispatcher,
        savedStateHandle = SavedStateHandle(
            buildMap {
                pages?.let { put("pages", ArrayList(it)) }
                selectedApps?.let { put("selected_apps", ArrayList(it)) }
            }
        ),
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

    @Test
    fun `installed chat apps are ticked once the list loads`() = runTest(dispatcher) {
        installed = listOf(app("com.whatsapp"), app("com.android.clock"))
        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals(setOf("com.whatsapp"), viewModel.state.value.selectedApps)
    }

    @Test
    fun `a phone without them starts with nothing ticked`() = runTest(dispatcher) {
        installed = listOf(app("com.android.clock"))
        val viewModel = viewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.selectedApps.isEmpty())
    }

    @Test
    fun `a restored choice is kept over the defaults`() = runTest(dispatcher) {
        installed = listOf(app("com.whatsapp"), app("com.android.clock"))
        val viewModel = viewModel(selectedApps = listOf("com.android.clock"))
        advanceUntilIdle()

        assertEquals(setOf("com.android.clock"), viewModel.state.value.selectedApps)
    }

    @Test
    fun `apps step needs a ticked app that is installed`() = runTest(dispatcher) {
        installed = listOf(app("com.android.clock"))
        val viewModel = viewModel(pages = listOf("Intro", "Keywords", "Apps"), selectedApps = listOf("com.whatsapp"))
        advanceUntilIdle()
        assertFalse(viewModel.state.value.nextEnabled)

        viewModel.onAction(OnboardingAction.ToggleApp("com.android.clock"))
        assertTrue(viewModel.state.value.nextEnabled)
    }
}
