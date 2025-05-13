package dev.logickoder.keyguarde.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.logickoder.keyguarde.settings.components.SettingsCard
import dev.logickoder.keyguarde.settings.components.SettingsIconText
import dev.logickoder.keyguarde.settings.components.SettingsTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val openPrivacyPolicy = remember {
        {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://logickoder.github.io/keyguarde/privacy-policy".toUri()
            )
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar("Privacy", onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(16.dp),
                content = {
                    SettingsCard(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        content = {
                            SettingsIconText(
                                icon = Icons.Rounded.Security,
                                text = "Local Processing Only",
                                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Keyguarde processes all notifications locally on your device. No message data is ever stored externally or transmitted to any server.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    SettingsCard(
                        containerColor = MaterialTheme.colorScheme.surface,
                        content = {
                            SettingsIconText(
                                icon = Icons.Outlined.Visibility,
                                text = "What We Access",
                                iconTint = MaterialTheme.colorScheme.primary,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Keyguarde only reads notification content to check for keywords. We never access your messages, contacts, or other personal data directly.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = openPrivacyPolicy,
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Article,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Privacy Policy")
                        }
                    )
                }
            )
        }
    )
}