package dev.logickoder.keyguarde.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.logickoder.keyguarde.app.data.model.Keyword

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDialog(
    initialKeyword: Keyword? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit // word, context, useSemanticMatching
) {
    var word by remember(initialKeyword) {
        mutableStateOf(
            TextFieldValue(
                text = initialKeyword?.word.orEmpty(),
                selection = TextRange(initialKeyword?.word?.length ?: 0)
            )
        )
    }
    var context by remember(initialKeyword) {
        mutableStateOf(
            TextFieldValue(
                text = initialKeyword?.context.orEmpty(),
                selection = TextRange(initialKeyword?.context?.length ?: 0)
            )
        )
    }
    var useSemanticMatching by remember(initialKeyword) {
        mutableStateOf(initialKeyword?.useSemanticMatching ?: false)
    }
    var isValid by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 6.dp,
                content = {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        content = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if (initialKeyword == null) "Add New Keyword" else "Edit Keyword",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth()
                                    .focusRequester(focusRequester),
                                value = word,
                                onValueChange = { word = it },
                                label = { Text("Keyword") },
                                singleLine = true,
                                isError = !isValid,
                                supportingText = if (!isValid) {
                                    { Text("Keyword must be at least 2 characters") }
                                } else null,
                            )

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = context,
                                onValueChange = { context = it },
                                label = { Text("Context (Optional)") },
                                maxLines = 3,
                                supportingText = {
                                    Text("Describe what this keyword relates to for better AI matching")
                                },
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    Checkbox(
                                        checked = useSemanticMatching,
                                        onCheckedChange = { useSemanticMatching = it }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Use AI semantic matching",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    TextButton(
                                        onClick = onDismiss,
                                        content = {
                                            Text("Cancel")
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    val trimmedWord = word.text.trim().lowercase()
                                    val trimmedContext = context.text.trim()
                                    Button(
                                        onClick = {
                                            isValid = trimmedWord.length >= 2
                                            if (isValid) {
                                                onSave(trimmedWord, trimmedContext, useSemanticMatching)
                                            }
                                        },
                                        enabled = trimmedWord.isNotEmpty(),
                                        content = {
                                            Text("Save")
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}