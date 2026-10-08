package dev.logickoder.keyguarde.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.appTransition
import dev.logickoder.keyguarde.onboarding.OnboardingScreen
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation(
    start: AppRoute,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(start)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = appTransition,
        popTransitionSpec = appTransition,
        predictivePopTransitionSpec = { appTransition() },
        entryProvider = entryProvider {
            entry<AppRoute.Onboarding> {
                OnboardingScreen(
                    onDone = {
                        backStack.add(AppRoute.Main)
                        backStack.remove(AppRoute.Onboarding)
                    }
                )
            }
            entry<AppRoute.Main> {
                MainNavigation()
            }
        }
    )
}

sealed interface AppRoute : NavKey {
    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Main : AppRoute
}
