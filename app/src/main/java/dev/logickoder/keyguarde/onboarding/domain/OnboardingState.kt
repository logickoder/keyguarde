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
import dev.logickoder.keyguarde.app.domain.NotificationHelper
import dev.logickoder.keyguarde.app.domain.NotificationHelper.isListenerServiceEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    val selectedApps = mutableStateListOf(WHATSAPP_PACKAGE_NAME, TELEGRAM_PACKAGE_NAME)

    val keywords = mutableStateListOf<Keyword>()

    val apps = mutableStateListOf<AppInfo>()

    var permissionGranted by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    init {
        var loadAppsJob: Job? = null
        scope.launch(Dispatchers.Default) {
            loadAppsJob = launch {
                apps.addAll(repository.getInstalledApps())
            }
            launch {
                currentScreen.collectLatest { screen ->
                    when {
                        screen == OnboardingPage.Permissions -> {
                            while (isActive) {
                                permissionGranted = isListenerServiceEnabled(context)
                                delay(1_000)
                            }
                        }

                        screen == OnboardingPage.AppSelection && apps.isEmpty() -> {
                            if (!loadAppsJob.isActive) {
                                loadAppsJob = launch {
                                    apps.addAll(repository.getInstalledApps())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun save() {
        if (isSaving) {
            return
        }

        scope.launch {
            repository.onboardingCompleted()

            repository.addKeyword(*keywords.toTypedArray())

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

            NotificationHelper.startListenerService(context)
            NotificationHelper.requestListenerServiceRebind(context)

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