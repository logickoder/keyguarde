package dev.logickoder.keyguarde.app.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val AppScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)