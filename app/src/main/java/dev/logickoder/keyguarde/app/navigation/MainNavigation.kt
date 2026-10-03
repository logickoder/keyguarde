package dev.logickoder.keyguarde.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.app.components.BannerAd
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.mainPopTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.mainTransition
import dev.logickoder.keyguarde.home.HomeScreen
import kotlinx.serialization.Serializable

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(MainRoute.Home)

    Column(
        modifier = modifier,
        content = {
            NavDisplay(
                modifier = Modifier.weight(1f),
                backStack = backStack,
                transitionSpec = mainTransition,
                popTransitionSpec = mainPopTransition,
                predictivePopTransitionSpec = { mainPopTransition() },
                entryProvider = entryProvider {
                    entry<MainRoute.Home> {
                        HomeScreen(
                            onSettings = {
                                backStack.add(MainRoute.Settings)
                            }
                        )
                    }

                    entry<MainRoute.Settings> {
                        SettingsNavigation(
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
            )

            BannerAd(modifier = Modifier.padding(top = 8.dp).navigationBarsPadding())
        }
    )
}

sealed interface MainRoute : NavKey {
    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Settings : MainRoute
}
