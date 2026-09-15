package com.dstwrtv.app.streaming.domain.playback

import com.dstwrtv.app.streaming.domain.model.PlaybackRequest
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceFailurePolicy

/** Coordinates bounded source failover without duplicating the video player. */
class PlaybackCoordinator(
    private val failurePolicy: SourceFailurePolicy = SourceFailurePolicy()
) {
    fun orderedSources(sources: List<PlaybackSource>): List<PlaybackSource> =
        sources.distinctBy { it.url }.take(failurePolicy.maxTotalAttempts())

    fun nextSource(sources: List<PlaybackSource>, failedIndex: Int): PlaybackSource? =
        sources.getOrNull(failedIndex + 1)

    fun canAttempt(request: PlaybackRequest, attempt: Int): Boolean =
        request.source.url.isNotBlank() && attempt < failurePolicy.maxTotalAttempts()
}
