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
import dev.logickoder.keyguarde.settings.BatterySettingsScreen
import dev.logickoder.keyguarde.settings.FaqScreen
import dev.logickoder.keyguarde.settings.KeywordsScreen
import dev.logickoder.keyguarde.settings.NotificationSettingsScreen
import dev.logickoder.keyguarde.settings.PrivacySettingsScreen
import dev.logickoder.keyguarde.settings.SettingsScreen
import dev.logickoder.keyguarde.settings.WatchedAppsScreen
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
                    onNavigate = {
                        navController.navigate(it)
                    },
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

            screen<SettingsRoute.Faqs> {
                FaqScreen(
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

    @Serializable
    @Parcelize
    data object Faqs : SettingsRoute
}