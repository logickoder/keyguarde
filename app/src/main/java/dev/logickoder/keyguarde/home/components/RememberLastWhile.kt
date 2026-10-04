package dev.logickoder.keyguarde.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * [value] while [active], then the last value seen while active. Lets content keep showing its
 * last state while it animates out, e.g. "3 selected" instead of "0 selected" as selection ends.
 */
@Composable
internal fun <T> rememberLastWhile(active: Boolean, value: T): T {
    // A plain holder, not snapshot state: updating it must not trigger another recomposition.
    val last = remember { arrayOf<Any?>(value) }
    if (active) last[0] = value
    @Suppress("UNCHECKED_CAST")
    return last[0] as T
}
