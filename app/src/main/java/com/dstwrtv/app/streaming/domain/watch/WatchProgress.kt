package com.dstwrtv.app.streaming.domain.watch

import com.dstwrtv.app.streaming.domain.model.MediaType

data class WatchProgress(
    val contentKey: String,
    val mediaType: MediaType,
    val positionMs: Long,
    val durationMs: Long,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val completed: Boolean = false,
    val updatedAtEpochMs: Long = System.currentTimeMillis()
) {
    val percent: Int
        get() = if (durationMs <= 0L) 0 else ((positionMs.toDouble() / durationMs) * 100.0).toInt().coerceIn(0, 100)
}
