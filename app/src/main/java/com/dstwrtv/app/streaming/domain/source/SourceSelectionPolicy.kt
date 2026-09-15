package com.dstwrtv.app.streaming.domain.source

/**
 * Central source-ranking policy. Keeping ranking here prevents individual
 * providers from dictating playback behavior and makes failover predictable.
 */
object SourceSelectionPolicy {
    fun rank(
        sources: List<PlaybackSource>,
        preferredLanguage: String? = null,
        preferredQuality: Int? = null
    ): List<PlaybackSource> = sources
        .filter { it.url.isNotBlank() && !it.requiresProxy }
        .sortedWith(
            compareByDescending<PlaybackSource> { source ->
                if (!preferredLanguage.isNullOrBlank() &&
                    source.language.equals(preferredLanguage, ignoreCase = true)
                ) 1 else 0
            }
                .thenByDescending { source ->
                    val quality = source.quality ?: 0
                    when {
                        preferredQuality == null -> quality
                        quality <= preferredQuality -> quality
                        else -> Int.MIN_VALUE + quality
                    }
                }
                .thenBy { it.isEmbedded }
        )
}
