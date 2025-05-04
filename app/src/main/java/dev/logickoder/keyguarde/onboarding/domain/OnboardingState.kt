package dev.logickoder.keyguarde.onboarding.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.logickoder.keyguarde.app.data.AppRepository
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OnboardingState(
    private val context: Context,
    val controller: NavHostController,
    private val scope: CoroutineScope,
    private val onDone: () -> Unit,
) {
    private val repository = AppRepository.getInstance(context)

    val currentScreen = controller.currentBackStackEntryFlow.map { entry ->
        OnboardingPage.entries.firstOrNull { it.name == entry.destination.route }
            ?: OnboardingPage.Welcome
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OnboardingPage.Welcome,
    )

    val selectedApps = mutableStateListOf<String>()

    val keywords = mutableStateListOf<String>()

    val apps = mutableStateListOf<AppInfo>()

    var permissionGranted by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        scope.launch(Dispatchers.Default) {
            launch {
                currentScreen.collectLatest { screen ->
                    if (screen == OnboardingPage.Permissions) {
                        while (isActive) {
                            Napier.e {
                                "Checking permission in OnboardingState: $screen"
                            }
                            permissionGranted = context.isListenerServiceEnabled()
                            delay(1_000)
                        }
                    }
                }
            }

            apps.addAll(repository.getInstalledApps())
            // automatically select whatsapp and telegram if available
            apps.find { it.packageName == WHATSAPP_PACKAGE_NAME }?.let {
                selectedApps.add(it.packageName)
            }
            apps.find { it.packageName == TELEGRAM_PACKAGE_NAME }?.let {
                selectedApps.add(it.packageName)
            }
        }
    }

    fun save() {
        if (isSaving) {
            return
        }

        scope.launch {
            repository.onboardingCompleted()

            repository.addKeyword(*keywords.map { Keyword(word = it) }.toTypedArray())

            val watchedApps = apps.filter { selectedApps.contains(it.packageName) }.map { app ->
                WatchedApp(
                    packageName = app.packageName,
                    name = app.name,
                    icon = saveIconToFile(
                        app.icon,
                        app.packageName,
                        context
                    )
                )
            }
            repository.addWatchedApp(*watchedApps.toTypedArray())

            onDone()
        }.invokeOnCompletion {
            isSaving = false
        }
    }
}

@Composable
fun rememberOnboardingState(onDone: () -> Unit): OnboardingState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberNavController()
    return remember {
        OnboardingState(
            context = context,
            controller = controller,
            scope = scope,
            onDone = onDone,
        )
    }
}