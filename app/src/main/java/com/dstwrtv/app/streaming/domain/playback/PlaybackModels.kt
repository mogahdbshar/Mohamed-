package com.dstwrtv.app.streaming.domain.playback

import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.source.PlaybackSource

data class PlaybackRequest(
    val contentKey: String,
    val mediaType: MediaType,
    val title: String,
    val sourceCandidates: List<PlaybackSource>,
    val startPositionMs: Long = 0L,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null
)

data class PlaybackResult(
    val source: PlaybackSource,
    val attemptedSources: Int
)

enum class PlaybackFailureKind {
    NETWORK,
    HTTP,
    FORMAT,
    SOURCE_UNAVAILABLE,
    UNSUPPORTED,
    UNKNOWN
}

data class PlaybackFailure(
    val kind: PlaybackFailureKind,
    val message: String? = null,
    val retryable: Boolean = true
)
