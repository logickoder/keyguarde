package dev.logickoder.keyguarde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.logickoder.keyguarde.app.data.AppStore
import dev.logickoder.keyguarde.app.navigation.AppNavigation
import dev.logickoder.keyguarde.app.navigation.AppRoute
import dev.logickoder.keyguarde.app.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isOnboardingComplete = runBlocking {
            AppStore.getInstance(this@MainActivity).get(AppStore.onboardingComplete).first()
        }

        setContent {
            AppTheme {
                AppNavigation(
                    start = when (isOnboardingComplete) {
                        true -> AppRoute.Main
                        else -> AppRoute.Onboarding
                    }
                )
            }
        }
    }
}