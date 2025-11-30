package dev.logickoder.keyguarde.app.domain.usecase

import dev.logickoder.keyguarde.app.data.dao.KeywordDao
import dev.logickoder.keyguarde.app.data.dao.KeywordMatchDao
import dev.logickoder.keyguarde.app.data.dao.WatchedAppDao
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.data.model.KeywordMatch
import dev.logickoder.keyguarde.app.data.model.WatchedApp
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * A use case for populating the database with initial data.
 * This is useful for testing and development purposes.
 */
class PrepopulateDatabaseUsecase(
    private val keywordDao: KeywordDao,
    private val watchedAppDao: WatchedAppDao,
    private val keywordMatchDao: KeywordMatchDao,
) {
    /**
     * Populates the database with a specified number of keywords and keyword matches.
     *
     * @param keywordsCount The number of keywords to generate.
     * @param matchedKeywordsCount The number of keyword matches to generate.
     */
    suspend operator fun invoke(
        keywordsCount: Int = 10,
        matchedKeywordsCount: Int = 500,
    ) {
        val app = WatchedApp(
            packageName = "com.whatsapp",
            name = "WhatsApp",
            icon = ""
        )
        watchedAppDao.insert(app)

        val keywords = (1..keywordsCount).map {
            Keyword(word = "Keyword $it")
        }
        keywordDao.insert(*keywords.toTypedArray())

        (1..matchedKeywordsCount).map {
            KeywordMatch(
                keywords = setOf(keywords.random().word),
                message = "This is a random message $it with some text.",
                chat = "Sender ${Random.nextInt(1, 20)}",
                app = app.packageName,
                timestamp = LocalDateTime.now().minusDays(Random.nextLong(0, 365))
            )
        }.forEach { match ->
            keywordMatchDao.insert(match)
        }
    }
}
