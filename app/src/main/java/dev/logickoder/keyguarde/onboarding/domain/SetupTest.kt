package dev.logickoder.keyguarde.onboarding.domain

import dev.logickoder.keyguarde.home.domain.keywordRegex

/**
 * The last setup step: Keyguarde posts itself a notification and waits for its own listener to
 * report it back. Proves the listener is actually bound, which granting access alone doesn't.
 */
sealed interface SetupTest {
    data object Idle : SetupTest
    data object Waiting : SetupTest
    data class Caught(val keyword: String) : SetupTest

    /** Nothing came back in time, or it came back without the keyword. */
    data object Missed : SetupTest
}

/**
 * The keyword the listener's text matched, using the same whole-word rule as real matches.
 */
fun caughtKeyword(text: String, keywords: Collection<String>): String? =
    keywordRegex(keywords)?.find(text)?.value
