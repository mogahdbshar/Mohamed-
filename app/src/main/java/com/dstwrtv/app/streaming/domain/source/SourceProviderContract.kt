package com.dstwrtv.app.streaming.domain.source

/**
 * Capability contract for internal source adapters.
 * Adapters return candidates only; the app never proxies video bytes.
 */
data class SourceProviderCapabilities(
    val supportsMovies: Boolean = false,
    val supportsTvEpisodes: Boolean = false,
    val supportsChannels: Boolean = false,
    val supportsSubtitles: Boolean = false,
    val supportsMultipleQualities: Boolean = false,
    val supportsEmbeddedPlayback: Boolean = false
)

interface DescribedSourceProvider : SourceProvider {
    val capabilities: SourceProviderCapabilities
}
