package dev.logickoder.keyguarde.app.domain.usecase

import dev.logickoder.keyguarde.app.data.AppRepository

/**
 * Starts the recent match count over. The listener service sees the change and clears the count
 * notification.
 */
class ResetMatchCountUsecase(
    private val repository: AppRepository,
) {
    suspend operator fun invoke() {
        repository.resetRecentMatches()
    }
}
