package dev.logickoder.keyguarde.app

import android.os.Parcelable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.logickoder.keyguarde.onboarding.OnboardingScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.Onboarding,
        builder = {
            composable<AppRoute.Onboarding> { _ ->
                OnboardingScreen(
                    onDone = {
                        navController.navigate(AppRoute.Main) {
                            popUpTo(AppRoute.Onboarding) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<AppRoute.Main> { _ ->
                Text("This is the main screen")
            }
        }
    )
}

sealed interface AppRoute : Parcelable {
    @Serializable
    @Parcelize
    data object Onboarding : AppRoute

    @Serializable
    @Parcelize
    data object Main : AppRoute
}