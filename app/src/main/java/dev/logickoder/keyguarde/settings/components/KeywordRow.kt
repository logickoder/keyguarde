package dev.logickoder.keyguarde.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Spacing

/**
 * One keyword, in teal like everywhere a keyword shows, with how many matches it has caught, so a
 * keyword that never fires stands out. Tapping opens it for editing.
 */
@Composable
fun KeywordRow(
    word: String,
    matchCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClickLabel = stringResource(R.string.keyword_edit_label), role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.l),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.m),
        content = {
            Text(
                text = word,
                style = KeywordPillStyle.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = when (matchCount) {
                    0 -> stringResource(R.string.keyword_no_matches)
                    else -> pluralStringResource(R.plurals.keyword_match_count, matchCount, matchCount)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun KeywordRowPreview() = AppTheme {
    KeywordRow(word = "invoice", matchCount = 12, onClick = {})
}
