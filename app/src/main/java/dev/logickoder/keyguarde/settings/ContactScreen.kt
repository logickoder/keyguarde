package dev.logickoder.keyguarde.settings

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.settings.components.ContactItemCard
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.ContactItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val items = remember {
        listOf(
            ContactItem(
                title = "Send Feedback",
                description = "Share your thoughts, suggestions, or report issues",
                icon = Icons.Outlined.Email
            ) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:".toUri()
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("jefferyorazulike@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Keyguarde Feedback")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Hi Keyguarde team,\n\n" +
                                "App Version: ${
                                    context.packageManager.getPackageInfo(
                                        context.packageName,
                                        0
                                    ).versionName
                                }\n" +
                                "Android Version: ${Build.VERSION.RELEASE}\n" +
                                "Device: ${Build.MANUFACTURER} ${Build.MODEL}\n\n" +
                                "Feedback:\n"
                    )
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            },
            ContactItem(
                title = "Rate the App",
                description = "Help others discover Keyguarde by leaving a review",
                icon = Icons.Outlined.Star
            ) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=${context.packageName}".toUri()
                )
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    // Fallback to web version if Play Store app is not available
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                        )
                    )
                }
            },
            ContactItem(
                title = "Visit Website",
                description = "Learn more about Keyguarde and privacy policy",
                icon = Icons.Outlined.Language
            ) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://logickoder.github.io/keyguarde/".toUri()
                    )
                )
            },
            ContactItem(
                title = "Open Source",
                description = "View the source code and contribute on GitHub",
                icon = Icons.Outlined.Code
            ) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://github.com/logickoder/keyguarde".toUri()
                    )
                )
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Contact & Feedback", onBack)
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
                            text = "We'd love to hear from you! Your feedback helps us improve Keyguarde.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(items) { contactItem ->
                        ContactItemCard(item = contactItem)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.3f
                                )
                            ),
                            content = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    content = {
                                        Text(
                                            text = "Privacy First",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Your feedback emails won't include any notification data or personal information. We only collect what you choose to share.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun ContactScreenPreview() = AppTheme {
    ContactScreen(onBack = {})
}
