package com.dstwrtv.app.streaming.data.source

/**
 * Runtime-safe description of a source adapter. Concrete endpoints can be
 * supplied by a future signed configuration without changing the UI/player.
 */
data class SourceProviderConfig(
    val id: String,
    val baseUrl: String,
    val priority: Int = 0,
    val enabled: Boolean = true,
    val supportsMovies: Boolean = false,
    val supportsTvEpisodes: Boolean = false,
    val supportsSubtitles: Boolean = false,
    val supportsMultipleQualities: Boolean = false,
    val expiresAtEpochMs: Long? = null
) {
    fun isUsable(nowMs: Long = System.currentTimeMillis()): Boolean =
        enabled &&
            id.isNotBlank() &&
            baseUrl.startsWith("https://") &&
            (expiresAtEpochMs == null || expiresAtEpochMs > nowMs)
}
