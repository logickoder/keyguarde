package dev.logickoder.keyguarde.home.domain

import dev.logickoder.keyguarde.app.data.model.KeywordMatch

/**
 * What the Matches list renders: matches, plus a divider above the ones that arrived since the
 * user's last visit.
 */
sealed interface MatchListItem {
    val key: Any

    data class Match(val match: KeywordMatch, val isNew: Boolean) : MatchListItem {
        override val key: Any get() = match.id
    }

    data object NewDivider : MatchListItem {
        override val key: Any get() = "new-since-last-visit"
    }
}
