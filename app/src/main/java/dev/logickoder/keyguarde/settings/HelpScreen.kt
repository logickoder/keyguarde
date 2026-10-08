package dev.logickoder.keyguarde.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.net.toUri
import dev.logickoder.keyguarde.BuildConfig
import dev.logickoder.keyguarde.R
import dev.logickoder.keyguarde.app.components.LocalToastManager
import dev.logickoder.keyguarde.app.domain.openStoreListing
import dev.logickoder.keyguarde.app.domain.openUrl
import dev.logickoder.keyguarde.app.theme.AppTheme
import dev.logickoder.keyguarde.app.theme.Spacing
import dev.logickoder.keyguarde.settings.components.FaqRow
import dev.logickoder.keyguarde.settings.components.SettingsDivider
import dev.logickoder.keyguarde.settings.components.SettingsRow
import dev.logickoder.keyguarde.settings.components.SettingsSection
import dev.logickoder.keyguarde.settings.components.SettingsTopBar
import dev.logickoder.keyguarde.settings.domain.FaqItem

private const val FEEDBACK_EMAIL = "jeffery@logickoder.dev"
private const val WEBSITE_URL = "https://logickoder.dev/keyguarde/"
private const val SOURCE_URL = "https://github.com/logickoder/keyguarde"

private val Faqs = listOf(
    FaqItem(R.string.faq_no_matches_q, R.string.faq_no_matches_a),
    FaqItem(R.string.faq_matching_q, R.string.faq_matching_a),
    FaqItem(R.string.faq_manage_keywords_q, R.string.faq_manage_keywords_a),
    FaqItem(R.string.faq_select_apps_q, R.string.faq_select_apps_a),
    FaqItem(R.string.faq_notification_access_q, R.string.faq_notification_access_a),
    FaqItem(R.string.faq_reads_messages_q, R.string.faq_reads_messages_a),
    FaqItem(R.string.faq_privacy_q, R.string.faq_privacy_a),
    FaqItem(R.string.faq_battery_q, R.string.faq_battery_a),
)

/**
 * Answers first, then ways to reach a person. The most common problem, missing matches, leads.
 */
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val toastManager = LocalToastManager.current
    val emailMissing = stringResource(R.string.email_app_missing, FEEDBACK_EMAIL)
    val subject = stringResource(R.string.feedback_email_subject)
    val body = stringResource(
        R.string.feedback_email_body,
        BuildConfig.VERSION_NAME,
        Build.VERSION.RELEASE,
        "${Build.MANUFACTURER} ${Build.MODEL}",
    )

    HelpContent(
        onBack = onBack,
        onEmail = {
            val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri())
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body)
            try {
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                toastManager.show(emailMissing)
            }
        },
        onRate = { context.openStoreListing() },
        onOpenUrl = { url -> context.openUrl(url) },
        modifier = modifier,
    )
}

@Composable
private fun HelpContent(
    onBack: () -> Unit,
    onEmail: () -> Unit,
    onRate: () -> Unit,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopBar(stringResource(R.string.settings_help), onBack)
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = Spacing.l),
                content = {
                    SettingsSection(
                        title = stringResource(R.string.help_questions),
                        content = {
                            Faqs.forEachIndexed { index, faq ->
                                if (index > 0) SettingsDivider()
                                FaqRow(faq)
                            }
                        }
                    )
                    SettingsSection(
                        title = stringResource(R.string.help_contact),
                        content = {
                            SettingsRow(
                                title = stringResource(R.string.send_feedback),
                                description = stringResource(R.string.send_feedback_desc),
                                showChevron = false,
                                onClick = onEmail,
                            )
                            SettingsDivider()
                            SettingsRow(
                                title = stringResource(R.string.rate_app),
                                showChevron = false,
                                onClick = onRate,
                            )
                            SettingsDivider()
                            SettingsRow(
                                title = stringResource(R.string.visit_website),
                                showChevron = false,
                                onClick = { onOpenUrl(WEBSITE_URL) },
                            )
                            SettingsDivider()
                            SettingsRow(
                                title = stringResource(R.string.open_source),
                                showChevron = false,
                                onClick = { onOpenUrl(SOURCE_URL) },
                            )
                        }
                    )
                }
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun HelpContentPreview() = AppTheme {
    HelpContent(onBack = {}, onEmail = {}, onRate = {}, onOpenUrl = {})
}
