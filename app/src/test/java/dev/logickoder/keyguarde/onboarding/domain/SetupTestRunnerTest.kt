package dev.logickoder.keyguarde.onboarding.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SetupTestRunnerTest {

    @Test
    fun `a caught test reports its result once`() = runTest {
        val received = MutableSharedFlow<String>()
        val finished = mutableListOf<SetupTest>()
        val runner = SetupTestRunner(backgroundScope, received, keywords = { listOf("invoice") }, onFinished = { finished += it })
        runCurrent()

        runner.start()
        received.emit("Testing Keyguarde: does it catch “invoice”?")
        advanceUntilIdle()

        assertEquals(listOf<SetupTest>(SetupTest.Caught("invoice")), finished)
    }

    @Test
    fun `a test that times out reports missed once`() = runTest {
        val finished = mutableListOf<SetupTest>()
        val runner = SetupTestRunner(backgroundScope, MutableSharedFlow(), keywords = { listOf("invoice") }, onFinished = { finished += it })

        runner.start()
        advanceTimeBy(SetupTestRunner.TEST_TIMEOUT_MILLIS + 1)

        assertEquals(listOf<SetupTest>(SetupTest.Missed), finished)
    }
}
