package dev.logickoder.keyguarde.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.FaqItemCard
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.FaqItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val faqs = remember {
        listOf(
            FaqItem(
                question = R.string.faq_notification_access_q,
                answer = R.string.faq_notification_access_a
            ),
            FaqItem(
                question = R.string.faq_reads_messages_q,
                answer = R.string.faq_reads_messages_a
            ),
            FaqItem(
                question = R.string.faq_battery_q,
                answer = R.string.faq_battery_a
            ),
            FaqItem(
                question = R.string.faq_no_matches_q,
                answer = R.string.faq_no_matches_a
            ),
            FaqItem(
                question = R.string.faq_manage_keywords_q,
                answer = R.string.faq_manage_keywords_a
            ),
            FaqItem(
                question = R.string.faq_select_apps_q,
                answer = R.string.faq_select_apps_a
            ),
            FaqItem(
                question = R.string.faq_privacy_q,
                answer = R.string.faq_privacy_a
            ),
            FaqItem(
                question = R.string.faq_matching_q,
                answer = R.string.faq_matching_a
            )
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.faq_title), onBack)
        },
        content = { scaffoldPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    item {
                        Text(
                            text = stringResource(R.string.faq_desc),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(faqs) { faq ->
                        FaqItemCard(faq = faq)
                    }
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun FaqScreenPreview() = AppTheme {
    FaqScreen(onBack = {})
}
