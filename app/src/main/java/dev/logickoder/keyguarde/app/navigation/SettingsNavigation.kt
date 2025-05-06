package dev.logickoder.keyguarde.app.navigation

import android.os.Parcelable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.settingsEnterTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.settingsExitTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.settingsPopEnterTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.settingsPopExitTransition
import dev.logickoder.keyguarde.settings.*
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

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SettingsRoute.Main,
        builder = {
            screen<SettingsRoute.Main> {
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

            screen<SettingsRoute.Keywords> {
                KeywordsScreen(
                    onBack = goBack,
                )
            }

            screen<SettingsRoute.Apps> {
                WatchedAppsScreen(
                    onBack = goBack,
                )
            }

            screen<SettingsRoute.Notifications> {
                NotificationSettingsScreen(
                    onBack = goBack,
                )
            }

            screen<SettingsRoute.Battery> {
                BatterySettingsScreen(
                    onBack = goBack,
                )
            }

            screen<SettingsRoute.Privacy> {
                PrivacySettingsScreen(
                    onBack = goBack,
                )
            }
        }
    )
}

private inline fun <reified T : SettingsRoute> NavGraphBuilder.screen(
    noinline content: @Composable() (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) = composable<T>(
    enterTransition = settingsEnterTransition,
    exitTransition = settingsExitTransition,
    popEnterTransition = settingsPopEnterTransition,
    popExitTransition = settingsPopExitTransition,
    content = content
)

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