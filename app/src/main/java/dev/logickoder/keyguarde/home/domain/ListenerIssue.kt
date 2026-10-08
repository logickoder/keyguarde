package dev.logickoder.keyguarde.home.domain

/**
 * Why Keyguarde might not be catching new messages, worst first. At most one shows at a time.
 */
enum class ListenerIssue {
    None,

    /** The user turned off notification access, so the system never binds the listener. */
    AccessOff,

    /** Access is on but the listener isn't bound, usually because the system killed the process. */
    Stopped,

    /**
     * Still stopped after the user tried a restart. Some phones only rebind a dead listener when
     * notification access is turned off and on again, so the advice changes to that.
     */
    StillStopped,
}

/**
 * @param hasAccess null until the screen has checked, so nothing shows before the first check.
 * @param isGraceOver false for a moment after launch or a restart, while the system binds the
 * listener, so the banner doesn't flash on every app open.
 * @param hasTriedRestart whether the user tapped Restart since the listener last connected.
 * @param isPaused the user paused Keyguarde, which is meant to be quiet, so problems wait for Resume.
 */
fun listenerIssue(
    hasAccess: Boolean?,
    isConnected: Boolean,
    isGraceOver: Boolean,
    hasTriedRestart: Boolean,
    isPaused: Boolean = false,
): ListenerIssue = when {
    hasAccess == null || isPaused -> ListenerIssue.None
    !hasAccess -> ListenerIssue.AccessOff
    isConnected || !isGraceOver -> ListenerIssue.None
    hasTriedRestart -> ListenerIssue.StillStopped
    else -> ListenerIssue.Stopped
}
