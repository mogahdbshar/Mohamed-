package com.dstwrtv.app.streaming.data.remote

import kotlinx.coroutines.delay

object ProviderRetry {
    suspend fun <T> execute(
        attempts: Int = 2,
        initialDelayMs: Long = 400L,
        block: suspend () -> T
    ): Result<T> {
        var last: Result<T>? = null
        repeat(attempts.coerceIn(1, 3)) { index ->
            val result = runCatching { block() }
            last = result
            if (result.isSuccess) return result
            if (index + 1 < attempts) {
                delay((initialDelayMs * (index + 1)).coerceAtMost(2_000L))
            }
        }
        return last ?: Result.failure(IllegalStateException("Provider retry did not execute"))
    }
}
