package dev.logickoder.keyguarde.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.settingsPopTransition
import dev.logickoder.keyguarde.app.navigation.NavigationAnimations.settingsTransition
import dev.logickoder.keyguarde.settings.BatterySettingsScreen
import dev.logickoder.keyguarde.settings.ContactScreen
import dev.logickoder.keyguarde.settings.FaqScreen
import dev.logickoder.keyguarde.settings.KeywordsScreen
import dev.logickoder.keyguarde.settings.NotificationSettingsScreen
import dev.logickoder.keyguarde.settings.PrivacySettingsScreen
import dev.logickoder.keyguarde.settings.SettingsScreen
import dev.logickoder.keyguarde.settings.WatchedAppsScreen
import kotlinx.serialization.Serializable

@Composable
fun SettingsNavigation(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val backStack = rememberNavBackStack(SettingsRoute.Main)

    val goBack: () -> Unit = remember(backStack) {
        {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        transitionSpec = settingsTransition,
        popTransitionSpec = settingsPopTransition,
        predictivePopTransitionSpec = { settingsPopTransition() },
        entryProvider = entryProvider {
            entry<SettingsRoute.Main> {
                SettingsScreen(
                    onBack = onBack,
                    onNavigate = {
                        backStack.add(it)
                    },
                )
            }

            entry<SettingsRoute.Keywords> {
                KeywordsScreen(
                    onBack = goBack,
                )
            }

            entry<SettingsRoute.Apps> {
                WatchedAppsScreen(
                    onBack = goBack,
                )
            }

            entry<SettingsRoute.Notifications> {
                NotificationSettingsScreen(
                    onBack = goBack,
                )
            }

            entry<SettingsRoute.Battery> {
                BatterySettingsScreen(
                    onBack = goBack,
                )
            }

            entry<SettingsRoute.Privacy> {
                PrivacySettingsScreen(
                    onBack = goBack,
                )
            }

            entry<SettingsRoute.Faqs> {
                FaqScreen(
                    onBack = goBack,
                )
            }

            entry<SettingsRoute.Contact> {
                ContactScreen(
                    onBack = goBack,
                )
            }
        }
    )
}

sealed interface SettingsRoute : NavKey {
    @Serializable
    data object Main : SettingsRoute

    @Serializable
    data object Keywords : SettingsRoute

    @Serializable
    data object Apps : SettingsRoute

    @Serializable
    data object Notifications : SettingsRoute

    @Serializable
    data object Battery : SettingsRoute

    @Serializable
    data object Privacy : SettingsRoute

    @Serializable
    data object Faqs : SettingsRoute

    @Serializable
    data object Contact : SettingsRoute
}
