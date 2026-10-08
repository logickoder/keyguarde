package dev.logickoder.keyguarde.onboarding.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class MessagingAppsTest {

    @Test
    fun `messaging apps are split out, keeping order`() {
        val (messaging, others) = splitMessagingApps(
            listOf("com.android.settings", "org.telegram.messenger", "com.whatsapp", "com.android.clock"),
        ) { it }

        assertEquals(listOf("org.telegram.messenger", "com.whatsapp"), messaging)
        assertEquals(listOf("com.android.settings", "com.android.clock"), others)
    }

    @Test
    fun `only installed apps are ticked by default`() {
        assertEquals(setOf("com.whatsapp"), defaultAppSelection(listOf("com.whatsapp", "com.android.clock")))
    }

    @Test
    fun `nothing is ticked when no common chat app is installed`() {
        assertEquals(emptySet<String>(), defaultAppSelection(listOf("com.google.android.gm", "com.android.clock")))
    }
}
