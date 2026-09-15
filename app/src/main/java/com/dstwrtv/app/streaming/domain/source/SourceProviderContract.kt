package com.dstwrtv.app.streaming.domain.source

/**
 * Describes a source adapter without embedding any concrete third-party
 * service or URL in the application UI.
 *
 * Providers may expose direct HLS, DASH or progressive HTTP candidates.
 * Video bytes are never relayed through the application backend.
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
