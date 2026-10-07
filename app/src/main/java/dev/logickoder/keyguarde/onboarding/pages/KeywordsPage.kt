package dev.logickoder.keyguarde.onboarding.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.data.model.Keyword
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.KeywordInput
import dev.logickoder.keyguarde.onboarding.domain.parseKeyword
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Type keywords one after another, or tap a suggestion. Added keywords show as the same pills
 * the rest of the app uses, so the user sees what a match will look like.
 */
@Composable
fun KeywordsPage(
    keywords: ImmutableList<Keyword>,
    onAdd: (String) -> Unit,
    onRemove: (Keyword) -> Unit,
    modifier: Modifier = Modifier,
) {
    val suggestions = stringArrayResource(R.array.keyword_suggestions)
        .filter { suggestion -> keywords.none { it.word.equals(suggestion, ignoreCase = true) } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.l),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                content = {
                    Text(
                        text = stringResource(R.string.keywords_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = stringResource(R.string.keywords_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            )

            KeywordField(existing = keywords.map { it.word }, onAdd = onAdd)

            if (keywords.isNotEmpty()) {
                PillSection(
                    label = stringResource(R.string.keywords_added_label),
                    content = {
                        keywords.forEach { keyword ->
                            AddedPill(word = keyword.word, onRemove = { onRemove(keyword) })
                        }
                    }
                )
            }

            if (suggestions.isNotEmpty()) {
                PillSection(
                    label = stringResource(R.string.keywords_suggestions_label),
                    content = {
                        suggestions.forEach { word -> SuggestionPill(word = word, onAdd = { onAdd(word) }) }
                    }
                )
            }
        }
    )
}

@Composable
private fun KeywordField(existing: List<String>, onAdd: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    // Shown only after a submit, so the field doesn't nag while the user is still typing.
    var rejected by rememberSaveable { mutableStateOf<String?>(null) }
    val tooShort = stringResource(R.string.keyword_too_short)

    val submit = {
        when (val input = parseKeyword(text, existing)) {
            KeywordInput.Empty -> Unit
            KeywordInput.TooShort -> rejected = tooShort
            is KeywordInput.Duplicate -> rejected = input.word
            is KeywordInput.Valid -> {
                onAdd(input.word)
                text = ""
                rejected = null
            }
        }
    }
    // A duplicate stops being one once its pill is removed, so the error goes with it.
    val error = when (val word = rejected) {
        null -> null
        tooShort -> tooShort
        else -> word.takeIf { existing.contains(it) }?.let { stringResource(R.string.keyword_duplicate, it) }
    }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            rejected = null
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.keyword)) },
        placeholder = { Text(stringResource(R.string.keywords_placeholder)) },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Done),
        // Done adds the keyword and keeps the keyboard up for the next one.
        keyboardActions = KeyboardActions(onDone = { submit() }),
        trailingIcon = {
            IconButton(
                onClick = submit,
                enabled = text.isNotBlank(),
                content = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_keyword)) }
            )
        },
        shape = RoundedCornerShape(Radius.m),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PillSection(label: String, content: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                modifier = Modifier.animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                content = { content() }
            )
        }
    )
}

/** Tapping anywhere on the pill removes it; the × only says so. */
@Composable
private fun AddedPill(word: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(Radius.pill))
            .clickable(
                onClickLabel = stringResource(R.string.keywords_remove, word),
                role = Role.Button,
                onClick = onRemove,
            )
            .padding(start = Spacing.m, end = Spacing.s, top = Spacing.s, bottom = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            PillText(word = word, color = MaterialTheme.colorScheme.primary)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    )
}

/** Grey, not teal: a suggestion isn't a keyword until it's added. */
@Composable
private fun SuggestionPill(word: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(Radius.pill))
            .clickable(
                onClickLabel = stringResource(R.string.keywords_add_suggestion, word),
                role = Role.Button,
                onClick = onAdd,
            )
            .padding(start = Spacing.s, end = Spacing.m, top = Spacing.s, bottom = Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            PillText(word = word, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    )
}

/** Uppercase on screen, but the word as typed for TalkBack, which can spell capitals out. */
@Composable
private fun PillText(word: String, color: Color) {
    Text(
        text = word.uppercase(),
        style = KeywordPillStyle,
        color = color,
        modifier = Modifier.clearAndSetSemantics { contentDescription = word },
    )
}

@Preview(showBackground = true)
@Composable
private fun KeywordsPagePreview() = AppTheme {
    KeywordsPage(
        keywords = persistentListOf(Keyword(word = "invoice"), Keyword(word = "call me")),
        onAdd = {},
        onRemove = {},
    )
}
