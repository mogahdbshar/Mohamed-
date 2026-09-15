package com.dstwrtv.app.streaming.domain.source

/**
 * Central source-ranking policy. Keeps language and quality preferences useful
 * without making an unavailable preferred quality a hard failure.
 */
object SourceSelectionPolicy {
    fun rank(
        sources: List<PlaybackSource>,
        preferredLanguage: String? = null,
        preferredQuality: Int? = null
    ): List<PlaybackSource> = sources
        .filter { it.url.isNotBlank() && !it.requiresProxy }
        .distinctBy { it.url.trim().removeSuffix("/") }
        .sortedWith(
            compareByDescending<PlaybackSource> { source ->
                if (!preferredLanguage.isNullOrBlank() &&
                    source.language.equals(preferredLanguage, ignoreCase = true)
                ) 1 else 0
            }
                .thenBy { source -> qualityDistance(source.quality, preferredQuality) }
                .thenByDescending { it.quality ?: 0 }
                .thenBy { it.isEmbedded }
        )

    private fun qualityDistance(quality: Int?, preferred: Int?): Int {
        if (preferred == null) return -(quality ?: 0)
        if (quality == null) return 100_000
        return if (quality <= preferred) {
            preferred - quality
        } else {
            10_000 + quality - preferred
        }
    }
}
