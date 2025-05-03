package dev.logickoder.keyguarde.app

import android.os.Parcelable
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
                OnboardingScreen()
            }
        }
    )
}

sealed interface AppRoute : Parcelable {
    @Serializable
    @Parcelize
    data object Onboarding : AppRoute
}