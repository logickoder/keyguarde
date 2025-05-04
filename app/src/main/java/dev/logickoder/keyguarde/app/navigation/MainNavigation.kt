package dev.logickoder.keyguarde.app.navigation

import android.os.Parcelable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            composable<MainRoute.Home> { _ ->
                Text("This is the home screen")
            }
            composable<MainRoute.Settings> { _ ->
                Text("This is the settings screen")
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