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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                question = "Why does Keyguarde need notification access?",
                answer = "Keyguarde needs notification access to read the content of your notifications and match them against your keywords. This permission is essential for the app to function, but all processing happens locally on your device."
            ),
            FaqItem(
                question = "Does it read my messages?",
                answer = "Keyguarde only reads the text content of notifications as they appear. It does not access your message history, media, or any other data within your messaging apps. All processing is done locally on your device."
            ),
            FaqItem(
                question = "Will it drain my battery?",
                answer = "Keyguarde is designed to be lightweight and battery-efficient. It only activates when new notifications arrive, and uses minimal resources while running in the background. The app has been optimized to have negligible impact on your device's battery life."
            ),
            FaqItem(
                question = "I'm not getting matches after installing. What should I do?",
                answer = "If you're not receiving keyword matches after setup, try restarting your device. This ensures the notification listener service starts properly and can monitor your notifications. Also make sure you've granted notification access permission and selected the apps you want to monitor."
            ),
            FaqItem(
                question = "How do I add or remove keywords?",
                answer = "You can manage your keywords from the home screen by tapping the '+' button to add new keywords. To remove keywords, go to Settings > Keyword Filters where you can view and delete existing keywords."
            ),
            FaqItem(
                question = "Can I select which apps to monitor?",
                answer = "Yes! Go to Settings > Watched Apps to choose which messaging apps Keyguarde should monitor. By default, WhatsApp and Telegram are selected, but you can add or remove apps as needed."
            ),
            FaqItem(
                question = "Is my data private and secure?",
                answer = "Absolutely. All notification processing happens locally on your device. No messages, notification data, or personal information is stored externally or transmitted to any servers. Your privacy is our top priority."
            ),
            FaqItem(
                question = "How do keyword matches work?",
                answer = "Keyguarde matches whole words only and is case-insensitive. For example, 'react' will match 'React' but not 'reacted'. Multiple keywords can be matched in the same message, and you'll be notified when any of your keywords appear."
            )
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Frequently Asked Questions", onBack)
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
                            text = "Find answers to common questions about Keyguarde",
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
