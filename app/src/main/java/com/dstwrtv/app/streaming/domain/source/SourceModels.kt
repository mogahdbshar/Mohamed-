package com.dstwrtv.app.streaming.domain.source

import com.dstwrtv.app.streaming.domain.model.MediaType

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
    val tmdbId: Int? = null,
    val provider: String? = null,
    val providerId: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val preferredLanguage: String? = null,
    val title: String? = null,
    val displayTitle: String? = null
) {
    val contentKey: String get() = providerId?.let { "${provider.orEmpty()}:$it" } ?: tmdbId?.toString() ?: error("A providerId or tmdbId is required")
    val episodeKey: String get() = buildString { append(contentKey); seasonNumber?.let { append(":s").append(it) }; episodeNumber?.let { append(":e").append(it) } }
}

data class SourceProviderHealth(val providerId: String, val successRate: Double, val averageLatencyMs: Long, val lastFailureAt: Long? = null, val temporarilyDisabledUntil: Long? = null)

data class SourceResolution(
    val request: SourceRequest,
    val sources: List<PlaybackSource>,
    val attemptedProviders: Int = 0,
    val successfulProviders: Int = 0,
    val resolvedAt: Long = System.currentTimeMillis()
)