package dev.logickoder.keyguarde.app.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.mainEnterTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.mainExitTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.mainPopEnterTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.mainPopExitTransition
import dev.logickoder.keyguarde.home.HomeScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainRoute.Home,
        builder = {
            composable<MainRoute.Home>(
                enterTransition = mainPopEnterTransition,
                exitTransition = mainExitTransition,
                popEnterTransition = mainPopEnterTransition,
                popExitTransition = mainPopExitTransition,
                content = {
                    HomeScreen(
                        onSettings = {
                            navController.navigate(MainRoute.Settings)
                        }
                    )
                }
            )

            composable<MainRoute.Settings>(
                enterTransition = mainEnterTransition,
                exitTransition = mainPopExitTransition,
                popEnterTransition = null,
                popExitTransition = null,
                content = {
                    SettingsNavigation(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    )
}

sealed interface MainRoute : Parcelable {
    @Serializable
    @Parcelize
    data object Home : MainRoute

    @Serializable
    @Parcelize
    data object Settings : MainRoute
}