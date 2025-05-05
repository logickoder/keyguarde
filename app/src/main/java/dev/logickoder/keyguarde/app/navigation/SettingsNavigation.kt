package dev.logickoder.keyguarde.app.navigation

import android.os.Parcelable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.Text
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
import dev.logickoder.keyguarde.settings.KeywordsScreen
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
                Text("This is the apps screen")
            }

            screen<SettingsRoute.Notifications> {
                Text("This is the notifications screen")
            }

            screen<SettingsRoute.Battery> {
                Text("This is the battery screen")
            }

            screen<SettingsRoute.Privacy> {
                Text("This is the privacy screen")
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