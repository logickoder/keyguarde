package dev.logickoder.keyguarde.app.domain.usecase

import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.dao.KeywordDao
import dev.logickoder.keyguarde.app.data.dao.KeywordMatchDao
import dev.logickoder.keyguarde.app.data.dao.WatchedAppDao
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

/**
 * Fills a debug build's database with realistic matches: real-looking chats, messages that
 * actually contain their keyword (at the start, middle or end, short and long), and a spread of
 * timestamps from minutes to months ago.
 */
class PrepopulateDatabaseUsecase(
    private val keywordDao: KeywordDao,
    private val watchedAppDao: WatchedAppDao,
    private val keywordMatchDao: KeywordMatchDao,
) {
    suspend operator fun invoke(matchCount: Int = 300) {
        val random = Random(42)
        val apps = listOf(
            WatchedApp(packageName = WHATSAPP_PACKAGE_NAME, name = "WhatsApp", icon = ""),
            WatchedApp(packageName = TELEGRAM_PACKAGE_NAME, name = "Telegram", icon = ""),
        )
        watchedAppDao.insert(*apps.toTypedArray())
        keywordDao.insert(*Keywords.map { Keyword(word = it) }.toTypedArray())

        repeat(matchCount) { index ->
            val keyword = Keywords[random.nextInt(Keywords.size)]
            val template = Templates[random.nextInt(Templates.size)]
            val message = template.replace("{kw}", keyword).replaceFirstChar { it.uppercase() }
            val age = when {
                index < 6 -> Instant.now().minus(random.nextLong(1, 180), ChronoUnit.MINUTES)
                index < 40 -> Instant.now().minus(random.nextLong(3, 72), ChronoUnit.HOURS)
                else -> Instant.now().minus(random.nextLong(3, 365), ChronoUnit.DAYS)
            }
            keywordMatchDao.insert(
                KeywordMatch(
                    keywords = setOf(keyword),
                    // Rows that collide on (message, chat, app) are dropped by the unique index; fine for a seed.
                    message = message,
                    chat = Chats[random.nextInt(Chats.size)],
                    app = apps[random.nextInt(apps.size)].packageName,
                    timestamp = age,
                )
            )
        }
    }

    private companion object {
        val Keywords = listOf(
            "urgent", "invoice", "meeting", "deadline", "payment",
            "delivery", "interview", "rent", "exam", "call me",
        )

        val Chats = listOf(
            "Design Team", "Mum", "Landlord", "Project Atlas", "Football Sunday",
            "Ada Okafor", "Uni Friends", "Logistics Ops", "Church Choir", "Dev Squad",
            "Tunde", "Accounts Payable – Lagos Office",
        )

        val Templates = listOf(
            "{kw}: please check the group before 5pm",
            "Quick one about the {kw}, are you free later?",
            "Morning all, round-up from yesterday's standup and the planning session that ran long. " +
                "Nothing blocking on design. The {kw} for March is still outstanding though, can someone chase?",
            "Sorry I missed your message, I was driving. Is the {kw} still on for tomorrow?",
            "Reminder that the {kw} is due Friday",
            "Thanks everyone for coming out today, great turnout. Photos are in the shared album. " +
                "Next week we start at 8 instead of 9, and bring water. Also, {kw}",
            "Hey! Long time. How's the new job going? We should catch up soon. Oh and the {kw} thing you asked about is sorted",
            "{kw}",
            "The courier says the {kw} will arrive between 2 and 4",
            "Not sure if you saw the email from finance this morning but they flagged a few line items " +
                "on the quarterly report and want them fixed before the {kw} on Monday, otherwise it slips again",
        )
    }
}
