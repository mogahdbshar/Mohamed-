package com.dstwrtv.app.streaming.domain.source

import com.dstwrtv.app.streaming.domain.model.MediaType

/**
 * A playback candidate returned by a source provider.
 * The URL is consumed directly by the device player. This layer never proxies video.
 */
data class PlaybackSource(
    val url: String,
    val label: String,
    val quality: Int? = null,
    val format: String? = null,
    val language: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val isEmbedded: Boolean = false,
    val requiresProxy: Boolean = false
)

data class SourceRequest(
    val mediaType: MediaType,
    val tmdbId: Int,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val preferredLanguage: String? = null
)

data class SourceProviderHealth(
    val providerId: String,
    val successRate: Double,
    val averageLatencyMs: Long,
    val lastFailureAt: Long? = null,
    val temporarilyDisabledUntil: Long? = null
)

data class SourceResolution(
    val request: SourceRequest,
    val sources: List<PlaybackSource>,
    val resolvedAt: Long = System.currentTimeMillis()
)
