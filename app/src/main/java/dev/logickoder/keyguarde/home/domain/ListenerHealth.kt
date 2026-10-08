package dev.logickoder.keyguarde.home.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * Tracks whether the listener is catching messages, for any screen that warns about it. The
 * screen reports what it checked on resume and when the user asks for a restart.
 */
class ListenerHealth(
    listenerConnected: Flow<Boolean>,
    private val graceMillis: Long = LISTENER_GRACE_MILLIS,
) {
    private val hasAccess = MutableStateFlow<Boolean?>(null)

    // Bumped on each restart request; every bump restarts the grace period.
    private val restarts = MutableStateFlow(0)

    // Cleared when the listener connects, so a later stop starts again from plain "Restart".
    private val hasTriedRestart = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isGraceOver: Flow<Boolean> = restarts.flatMapLatest {
        flow {
            emit(false)
            delay(graceMillis)
            emit(true)
        }
    }

    val issue: Flow<ListenerIssue> = combine(
        hasAccess,
        listenerConnected.onEach { connected -> if (connected) hasTriedRestart.update { false } },
        isGraceOver,
        hasTriedRestart,
        ::listenerIssue,
    ).distinctUntilChanged()

    fun accessChecked(granted: Boolean) {
        hasAccess.update { granted }
    }

    fun restartRequested() {
        hasTriedRestart.update { true }
        restarts.update { it + 1 }
    }

    companion object {
        // How long the system gets to bind the listener before a screen calls it stopped.
        const val LISTENER_GRACE_MILLIS = 3_000L
    }
}
