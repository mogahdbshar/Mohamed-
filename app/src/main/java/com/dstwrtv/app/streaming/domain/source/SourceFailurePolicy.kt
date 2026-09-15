package com.dstwrtv.app.streaming.domain.source

/**
 * Small, bounded playback retry policy. Source failover belongs to the source
 * layer, while transport retry stays deliberately conservative.
 */
class SourceFailurePolicy(
    private val maxAttemptsPerSource: Int = 2,
    private val maxTotalAttempts: Int = 6
) {
    fun maxAttemptsPerSource(): Int = maxAttemptsPerSource.coerceIn(1, 3)

    fun maxTotalAttempts(): Int = maxOf(
        maxAttemptsPerSource(),
        maxTotalAttempts.coerceIn(1, 8)
    )

    fun shouldTryNextSource(attempt: Int): Boolean =
        attempt < maxTotalAttempts()
}
