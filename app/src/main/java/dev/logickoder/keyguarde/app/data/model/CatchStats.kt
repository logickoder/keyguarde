package dev.logickoder.keyguarde.app.data.model

/**
 * Every message Keyguarde has caught since install. Deleting matches doesn't lower it.
 */
data class CatchStats(
    val messages: Int = 0,
    val apps: Int = 0,
)
