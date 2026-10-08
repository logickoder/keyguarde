package dev.logickoder.keyguarde.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.Radius
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.onboarding.domain.KeywordInput
import dev.logickoder.keyguarde.onboarding.domain.parseKeyword

/**
 * Edits one keyword, with the same checks as adding one. Delete sits here too, so it's
 * reachable without the swipe.
 *
 * @param others every other keyword, for the duplicate check.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordEditSheet(
    word: String,
    others: List<String>,
    onSave: (String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(word, selection = TextRange(word.length)))
    }
    // Shown only after a submit, so the field doesn't nag while the user is still typing.
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val tooShort = stringResource(R.string.keyword_too_short)
    val duplicate = stringResource(R.string.keyword_duplicate, text.text.trim().lowercase())
    val focusRequester = remember { FocusRequester() }

    val submit = {
        when (val input = parseKeyword(text.text, others)) {
            KeywordInput.Empty, KeywordInput.TooShort -> error = tooShort
            is KeywordInput.Duplicate -> error = duplicate
            is KeywordInput.Valid -> onSave(input.word)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(start = Spacing.xl, end = Spacing.xl, bottom = Spacing.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.l),
                content = {
                    Text(
                        text = stringResource(R.string.keyword_edit_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            error = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        label = { Text(stringResource(R.string.keyword)) },
                        singleLine = true,
                        isError = error != null,
                        supportingText = error?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        shape = RoundedCornerShape(Radius.m),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Button(
                        onClick = submit,
                        // Nothing to save until the word changes.
                        enabled = text.text.trim().lowercase() != word,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = RoundedCornerShape(Radius.l),
                        // Dark neutral like every primary button: teal is reserved for matched keywords.
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        content = { Text(stringResource(R.string.save)) },
                    )
                    // Quiet on purpose: destructive, and undoable from the snackbar.
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            Text(
                                text = stringResource(R.string.keyword_delete),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    )
                }
            )
        }
    )
}
