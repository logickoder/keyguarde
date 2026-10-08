package dev.logickoder.keyguarde.onboarding.domain

import dev.logickoder.keyguarde.app.data.AppRepository.Companion.TELEGRAM_PACKAGE_NAME
import dev.logickoder.keyguarde.app.data.AppRepository.Companion.WHATSAPP_PACKAGE_NAME

/**
 * Chat and mail apps, where keyword matches are most likely. Shown first during setup so the
 * user doesn't dig through system apps to find them.
 */
val MessagingPackages = setOf(
    WHATSAPP_PACKAGE_NAME,
    "com.whatsapp.w4b", // WhatsApp Business
    TELEGRAM_PACKAGE_NAME,
    "org.thunderdog.challegram", // Telegram X
    "org.thoughtcrime.securesms", // Signal
    "com.facebook.orca", // Messenger
    "com.instagram.android",
    "com.google.android.apps.messaging", // Google Messages
    "com.samsung.android.messaging",
    "com.android.mms",
    "com.google.android.apps.dynamite", // Google Chat
    "com.Slack",
    "com.discord",
    "com.microsoft.teams",
    "com.viber.voip",
    "com.tencent.mm", // WeChat
    "jp.naver.line.android",
    "com.snapchat.android",
    "ch.threema.app",
    "im.vector.app", // Element
    "com.google.android.gm", // Gmail
    "com.microsoft.office.outlook",
)

/** Ticked up front when installed: the apps most people set Keyguarde up for. */
private val PreselectedPackages = setOf(
    WHATSAPP_PACKAGE_NAME,
    "com.whatsapp.w4b",
    TELEGRAM_PACKAGE_NAME,
    "org.thoughtcrime.securesms",
)

/**
 * Splits [apps] into known messaging apps and everything else, keeping each side's order.
 */
fun <T> splitMessagingApps(apps: List<T>, packageName: (T) -> String): Pair<List<T>, List<T>> =
    apps.partition { packageName(it) in MessagingPackages }

/**
 * The apps to tick before the user touches anything. Only installed ones, so a phone without
 * WhatsApp doesn't start with a selection the user can't see.
 */
fun defaultAppSelection(installed: Collection<String>): Set<String> =
    installed.filterTo(mutableSetOf()) { it in PreselectedPackages }
