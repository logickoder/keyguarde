package dev.logickoder.keyguarde.home.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ListenerIssueTest {

    @Test
    fun `nothing shows before the screen has checked access`() {
        assertEquals(ListenerIssue.None, listenerIssue(hasAccess = null, isConnected = false, isGraceOver = true, hasTriedRestart = false))
    }

    @Test
    fun `access off wins over a stopped listener`() {
        assertEquals(ListenerIssue.AccessOff, listenerIssue(hasAccess = false, isConnected = false, isGraceOver = true, hasTriedRestart = false))
    }

    @Test
    fun `a listener still binding isn't called stopped`() {
        assertEquals(ListenerIssue.None, listenerIssue(hasAccess = true, isConnected = false, isGraceOver = false, hasTriedRestart = false))
    }

    @Test
    fun `a listener unbound after the grace period is stopped`() {
        assertEquals(ListenerIssue.Stopped, listenerIssue(hasAccess = true, isConnected = false, isGraceOver = true, hasTriedRestart = false))
    }

    @Test
    fun `a connected listener with access is fine`() {
        assertEquals(ListenerIssue.None, listenerIssue(hasAccess = true, isConnected = true, isGraceOver = true, hasTriedRestart = false))
    }

    @Test
    fun `still stopped after a restart asks for an access toggle`() {
        assertEquals(
            ListenerIssue.StillStopped,
            listenerIssue(hasAccess = true, isConnected = false, isGraceOver = true, hasTriedRestart = true),
        )
    }
}
