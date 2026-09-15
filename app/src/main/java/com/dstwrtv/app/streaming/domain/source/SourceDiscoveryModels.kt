package com.dstwrtv.app.streaming.domain.source

import com.dstwrtv.app.streaming.domain.model.MediaType

/** Canonical request sent to the automatic source discovery pipeline. */
data class SourceDiscoveryRequest(
    val mediaType: MediaType,
    val provider: String,
    val providerId: String,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val preferredLanguage: String? = null,
    val preferredQuality: Int? = null,
    val title: String? = null
)

data class SourceDiscoveryResult(
    val sources: List<PlaybackSource>,
    val attemptedProviders: Int,
    val successfulProviders: Int,
    val durationMs: Long
)
