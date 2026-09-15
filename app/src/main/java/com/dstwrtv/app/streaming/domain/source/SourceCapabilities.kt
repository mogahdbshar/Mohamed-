package com.dstwrtv.app.streaming.domain.source

import com.dstwrtv.app.streaming.domain.model.MediaType

data class SourceCapabilities(
    val mediaTypes: Set<MediaType>,
    val supportsEpisodes: Boolean = false,
    val supportsSubtitles: Boolean = false,
    val supportsMultipleQualities: Boolean = false,
    val supportsEmbeddedPlayback: Boolean = false,
    val requiresAuthentication: Boolean = false
)

data class SourceProviderDescriptor(
    val id: String,
    val displayName: String,
    val priority: Int,
    val capabilities: SourceCapabilities,
    val enabledByDefault: Boolean = true
)
