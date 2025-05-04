package dev.logickoder.keyguarde.app.navigation

import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.logickoder.keyguarde.home.HomeScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        remember {
            {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300, easing = EaseOutCubic)
                        )
            }
        }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        remember {
            {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300, easing = EaseInCubic)
                        )
            }
        }

    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        remember {
            {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseOutCubic)
                        )
            }
        }

    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        remember {
            {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInCubic)
                        )
            }
        }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainRoute.Home,
        builder = {
            composable<MainRoute.Home>(
                enterTransition = popEnterTransition,
                exitTransition = exitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                HomeScreen(
                    onSettings = {
                        navController.navigate(MainRoute.Settings)
                    }
                )
            }

            composable<MainRoute.Settings>(
                enterTransition = enterTransition,
                exitTransition = popExitTransition,
                popEnterTransition = null,
                popExitTransition = null
            ) {
                SettingsNavigation(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
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