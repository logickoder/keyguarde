package dev.logickoder.keyguarde.app.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.KeywordPillStyle
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.KeywordInput
import dev.logickoder.keyguarde.onboarding.domain.parseKeyword

/**
 * Type a keyword and press Done or +. Checks run on submit, not while typing: too short, or
 * already added.
 */
@Composable
fun KeywordField(
    existing: List<String>,
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier.fillMaxWidth(),
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

/** Grey, not teal: a suggestion isn't a keyword until it's added. */
@Composable
fun SuggestionPill(word: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(Radius.pill))
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
            KeywordPillText(word = word, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    )
}

@Composable
fun KeywordPillText(word: String, color: Color) {
    Text(text = word, style = KeywordPillStyle, color = color)
}
