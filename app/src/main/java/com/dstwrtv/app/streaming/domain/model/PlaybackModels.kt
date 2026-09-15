package com.dstwrtv.app.streaming.domain.model

import com.dstwrtv.app.streaming.domain.source.PlaybackSource

data class PlaybackRequest(
    val mediaType: MediaType,
    val title: String,
    val source: PlaybackSource,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val startPositionMs: Long = 0L
)

data class PlaybackProgress(
    val contentKey: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val progressPercent: Int
        get() = if (durationMs <= 0L) 0 else ((positionMs * 100L) / durationMs)
            .toInt()
            .coerceIn(0, 100)
}
