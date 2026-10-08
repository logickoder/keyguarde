package dev.logickoder.keyguarde.onboarding.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Runs the [SetupTest] for any screen that offers it. The screen posts the test notification, then
 * calls [start]; what the listener reports back, or the timeout, settles the result.
 *
 * @param received text of each setup test the listener saw.
 * @param keywords the words a caught test must contain.
 * @param onFinished runs once per test, when it ends Caught or Missed.
 */
class SetupTestRunner(
    private val scope: CoroutineScope,
    received: Flow<String>,
    private val keywords: suspend () -> Collection<String>,
    private val timeoutMillis: Long = TEST_TIMEOUT_MILLIS,
    private val onFinished: (SetupTest) -> Unit = {},
) {
    private val _state = MutableStateFlow<SetupTest>(SetupTest.Idle)
    val state: StateFlow<SetupTest> = _state.asStateFlow()

    private var timeoutJob: Job? = null

    init {
        scope.launch {
            received.collect { text -> onReceived(text) }
        }
    }

    fun start() {
        _state.update { SetupTest.Waiting }
        timeoutJob?.cancel()
        timeoutJob = scope.launch {
            delay(timeoutMillis)
            if (_state.compareAndSet(SetupTest.Waiting, SetupTest.Missed)) onFinished(SetupTest.Missed)
        }
    }

    fun reset() {
        timeoutJob?.cancel()
        _state.update { SetupTest.Idle }
    }

    private suspend fun onReceived(text: String) {
        if (_state.value != SetupTest.Waiting) return
        timeoutJob?.cancel()
        val keyword = caughtKeyword(text, keywords())
        val result = keyword?.let { SetupTest.Caught(it) } ?: SetupTest.Missed
        _state.update { result }
        onFinished(result)
    }

    companion object {
        // How long the listener gets to report the test back; usually it takes well under a second.
        const val TEST_TIMEOUT_MILLIS = 5_000L
    }
}
