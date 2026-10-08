package dev.logickoder.keyguarde.home.components

import android.text.format.DateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private val formatters = ConcurrentHashMap<Pair<Locale, String>, DateTimeFormatter>()

/**
 * The locale's own pattern for [skeleton] (e.g. "MMMd"), built once per locale. Building one takes
 * an ICU lookup and a pattern parse, too slow to repeat for every list row.
 */
internal fun localizedFormatter(locale: Locale, skeleton: String): DateTimeFormatter =
    formatters.getOrPut(locale to skeleton) {
        DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, skeleton), locale)
    }
