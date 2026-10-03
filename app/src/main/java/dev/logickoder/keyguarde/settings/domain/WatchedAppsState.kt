package dev.logickoder.keyguarde.settings.domain

import dev.logickoder.keyguarde.onboarding.domain.AppInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class WatchedAppsState(
    val apps: ImmutableList<AppInfo> = persistentListOf(),
    val watchedPackages: ImmutableSet<String> = persistentSetOf(),
)
