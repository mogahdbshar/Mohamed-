package com.dstwrtv.app.streaming.domain.playback

class PlaybackRetryPolicy(
    private val maxAttempts: Int = 2,
    private val retryDelaysMs: LongArray = longArrayOf(1500L, 3000L)
) {
    fun shouldRetry(attempt: Int, retryable: Boolean): Boolean =
        retryable && attempt < maxAttempts

    fun delayFor(attempt: Int): Long =
        retryDelaysMs.getOrElse(attempt.coerceAtLeast(0)) { retryDelaysMs.lastOrNull() ?: 0L }
}
