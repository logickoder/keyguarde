package dev.logickoder.keyguarde.analytics

import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.home.domain.HomeAction
import dev.logickoder.keyguarde.onboarding.domain.SetupTest
import dev.logickoder.keyguarde.settings.domain.KeywordsAction
import dev.logickoder.keyguarde.settings.domain.SettingsAction
import dev.logickoder.keyguarde.settings.domain.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class AnalyticsEventsTest {

    private val match = KeywordMatch(
        id = 1,
        keywords = setOf("invoice"),
        message = "The invoice is overdue",
        chat = "Accounts",
        app = "com.whatsapp",
        timestamp = Instant.parse("2026-10-08T09:00:00Z"),
    )

    @Test
    fun `opening a match says where it went, not what it said`() {
        val event = HomeAction.OpenInApp(match).analyticsEvent()

        assertEquals(AnalyticsEvent("match_opened", mapOf("target" to "chat")), event)
    }

    @Test
    fun `steps that aren't a feature use log nothing`() {
        assertNull(HomeAction.SearchQueryChanged("invoice").analyticsEvent())
        assertNull(HomeAction.OpenMatch(match).analyticsEvent())
        assertNull(KeywordsAction.Edit(Keyword("invoice")).analyticsEvent())
    }

    @Test
    fun `keyword events never carry the word`() {
        val events = listOf(
            KeywordsAction.Add("invoice"),
            KeywordsAction.Save("invoice"),
            KeywordsAction.Delete(Keyword("invoice")),
        ).mapNotNull { it.analyticsEvent() }

        assertEquals(listOf("keyword_added", "keyword_edited", "keyword_deleted"), events.map { it.name })
        events.forEach { event -> assertFalse(event.params.values.any { "invoice" in it.toString() }) }
    }

    @Test
    fun `pause and resume are told apart`() {
        assertEquals("paused", SettingsAction.SetPaused(true).analyticsEvent()?.name)
        assertEquals(
            AnalyticsEvent("resumed", mapOf("from" to "settings")),
            SettingsAction.SetPaused(false).analyticsEvent(),
        )
    }

    @Test
    fun `theme changes report the mode`() {
        assertEquals(
            AnalyticsEvent("theme_changed", mapOf("mode" to "dark")),
            SettingsAction.SetThemeMode(ThemeMode.Dark).analyticsEvent(),
        )
    }

    @Test
    fun `only a finished setup test is logged, without its keyword`() {
        assertNull(SetupTest.Waiting.analyticsEvent(from = "settings"))
        assertEquals(
            AnalyticsEvent("setup_test", mapOf("result" to "caught", "from" to "onboarding")),
            SetupTest.Caught("invoice").analyticsEvent(from = "onboarding"),
        )
    }
}
