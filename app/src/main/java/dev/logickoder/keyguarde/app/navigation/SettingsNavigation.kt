package dev.logickoder.keyguarde.app.navigation

import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.logickoder.keyguarde.settings.SettingsScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Composable
fun SettingsNavigation(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val navController = rememberNavController()

    val goBack: () -> Unit = remember(navController) {
        {
            navController.popBackStack()
        }
    }

    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = remember {
        {
            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    scaleIn(
                        initialScale = 0.95f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    ) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
        }
    }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = remember {
        {
            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
        }
    }

    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = remember {
        {
            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
        }
    }

    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = remember {
        {
            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    scaleOut(
                        targetScale = 0.95f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    ) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SettingsRoute.Main,
        builder = {
            composable<SettingsRoute.Main>(
                enterTransition = popEnterTransition,
                exitTransition = exitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                SettingsScreen(
                    onBack = onBack,
                    onKeywords = {
                        navController.navigate(SettingsRoute.Keywords)
                    },
                    onApps = {
                        navController.navigate(SettingsRoute.Apps)
                    },
                    onNotifications = {
                        navController.navigate(SettingsRoute.Notifications)
                    },
                    onBattery = {
                        navController.navigate(SettingsRoute.Battery)
                    },
                    onPrivacy = {
                        navController.navigate(SettingsRoute.Privacy)
                    }
                )
            }

            composable<SettingsRoute.Keywords>(
                enterTransition = enterTransition,
                exitTransition = popExitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                Text("This is the keywords screen")
            }

            composable<SettingsRoute.Apps>(
                enterTransition = enterTransition,
                exitTransition = popExitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                Text("This is the apps screen")
            }

            composable<SettingsRoute.Notifications>(
                enterTransition = enterTransition,
                exitTransition = popExitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                Text("This is the notifications screen")
            }

            composable<SettingsRoute.Battery>(
                enterTransition = enterTransition,
                exitTransition = popExitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                Text("This is the battery screen")
            }

            composable<SettingsRoute.Privacy>(
                enterTransition = enterTransition,
                exitTransition = popExitTransition,
                popEnterTransition = popEnterTransition,
                popExitTransition = popExitTransition
            ) {
                Text("This is the privacy screen")
            }
        }
    )
}

sealed interface SettingsRoute : Parcelable {
    @Serializable
    @Parcelize
    data object Main : SettingsRoute

    @Serializable
    @Parcelize
    data object Keywords : SettingsRoute

    @Serializable
    @Parcelize
    data object Apps : SettingsRoute

    @Serializable
    @Parcelize
    data object Notifications : SettingsRoute

    @Serializable
    @Parcelize
    data object Battery : SettingsRoute

    @Serializable
    @Parcelize
    data object Privacy : SettingsRoute
}