package dev.logickoder.keyguarde.app.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import dev.logickoder.keyguarde.home.domain.keywordRegex

/** How a matched keyword looks inside message text, everywhere in the app. */
fun keywordSpanStyle(color: Color): SpanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = color)

/**
 * [text] with each of [keywords] styled as a match, by the listener's own whole-word rule, so the
 * app never lights up a word the listener would skip.
 */
fun highlightKeywords(text: String, keywords: Collection<String>, color: Color): AnnotatedString =
    buildAnnotatedString {
        append(text)
        keywordRegex(keywords)?.findAll(text)?.forEach { match ->
            addStyle(keywordSpanStyle(color), match.range.first, match.range.last + 1)
        }
    }
