package com.dstwrtv.app.streaming.domain.watch

sealed interface WatchState {
    data object NotStarted : WatchState
    data class InProgress(val progress: WatchProgress) : WatchState
    data object Completed : WatchState
}

object WatchProgressPolicy {
    fun completed(positionMs: Long, durationMs: Long): Boolean {
        if (durationMs <= 0L) return false
        val remaining = durationMs - positionMs
        return positionMs >= 30_000L && (remaining <= 30_000L || positionMs.toDouble() / durationMs >= 0.92)
    }
}
