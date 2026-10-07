package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.components.HeroLayout

/**
 * What Keyguarde does, shown with the product instead of described: a notification with its
 * keyword picked out, and the access toggle the user will meet in Settings.
 */
@Composable
fun IntroPage(modifier: Modifier = Modifier) {
    val keyword = stringResource(R.string.intro_sample_keyword)
    val keywordColor = MaterialTheme.colorScheme.primary
    val headline = stringResource(R.string.intro_headline, keyword)
    val styledHeadline = remember(headline, keyword, keywordColor) {
        highlight(headline, keyword, keywordColor)
    }

    HeroLayout(
        modifier = modifier,
        hero = { IntroIllustration(keyword = keyword) },
        content = {
            Text(
                text = styledHeadline,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.intro_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.m),
            )
            Text(
                text = stringResource(R.string.intro_privacy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.l, bottom = Spacing.xl),
            )
        }
    )
}

/**
 * Pictures, not controls: hidden from TalkBack, since the headline says the same thing.
 */
@Composable
private fun IntroIllustration(keyword: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {},
        verticalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            MockNotification(keyword = keyword)
            MockAccessToggle(modifier = Modifier.padding(horizontal = Spacing.xl))
        }
    )
}

@Composable
private fun MockNotification(keyword: String) {
    val keywordColor = MaterialTheme.colorScheme.primary
    val message = stringResource(R.string.intro_sample_message, keyword)
    val styledMessage = remember(message, keyword, keywordColor) { highlight(message, keyword, keywordColor) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(Radius.l))
            .padding(Spacing.l),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape),
                contentAlignment = Alignment.Center,
                content = {
                    Text(
                        text = stringResource(R.string.intro_sample_initials),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                content = {
                    Text(
                        text = stringResource(R.string.intro_sample_chat),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = styledMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )
        }
    )
}

@Composable
private fun MockAccessToggle(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(Radius.l))
            .padding(horizontal = Spacing.l, vertical = Spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = true,
                onCheckedChange = null,
                // Neutral like the rest of the app; the real toggle in Settings uses the system colour.
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.onSurface,
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    )
}

private fun highlight(text: String, keyword: String, color: Color): AnnotatedString = buildAnnotatedString {
    append(text)
    val start = text.indexOf(keyword, ignoreCase = true)
    if (start >= 0) {
        addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color), start, start + keyword.length)
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun IntroPagePreview() = AppTheme {
    IntroPage()
}
