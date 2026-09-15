package com.dstwrtv.app.streaming.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Small client-side pacing gate used by public metadata providers.
 * It prevents bursts, respects Retry-After, and keeps request volume bounded.
 */
class ProviderRequestLimiter(
    private val minimumIntervalMs: Long = 500L
) {
    private val mutex = Mutex()
    private var nextAllowedAt = 0L

    suspend fun awaitTurn(nowMs: Long = System.currentTimeMillis()) {
        val waitMs = mutex.withLock {
            val wait = (nextAllowedAt - nowMs).coerceAtLeast(0L)
            nextAllowedAt = maxOf(nextAllowedAt, nowMs) + minimumIntervalMs
            wait
        }
        if (waitMs > 0) delay(waitMs)
    }

    suspend fun awaitRetry(retryAfterMs: Long) {
        delay(retryAfterMs.coerceIn(250L, 30_000L))
    }
}
