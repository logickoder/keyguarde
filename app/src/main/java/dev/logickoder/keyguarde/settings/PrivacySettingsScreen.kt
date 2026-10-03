package dev.logickoder.keyguarde.settings

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.logickoder.keyguarde.R
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
                "https://logickoder.dev/keyguarde/privacy-policy".toUri()
            )
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.privacy), onBack)
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
                                text = stringResource(R.string.local_processing_only),
                                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.local_processing_only_desc),
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
                                text = stringResource(R.string.what_we_access),
                                iconTint = MaterialTheme.colorScheme.primary,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.what_we_access_desc),
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
                            Text(stringResource(R.string.privacy_policy))
                        }
                    )
                }
            )
        }
    )
}