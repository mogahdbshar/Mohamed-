package com.dstwrtv.app.streaming.domain.playback

import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourcePolicy

class PlaybackSourceSelector(
    private val policy: SourcePolicy = SourcePolicy()
) {
    fun select(candidates: List<PlaybackSource>): List<PlaybackSource> = candidates
        .asSequence()
        .filter { it.url.isNotBlank() }
        .filter { !it.requiresProxy }
        .filter { isAllowedScheme(it.url, it.isEmbedded) }
        .distinctBy { normalize(it.url) }
        .sortedWith(
            compareByDescending<PlaybackSource> { if (policy.preferDirect && !it.isEmbedded) 1 else 0 }
                .thenByDescending { if (policy.preferAdaptiveFormats && it.format.equals("hls", true)) 2 else 0 }
                .thenByDescending { qualityScore(it.quality) }
        )
        .take(policy.maxSources)
        .toList()

    private fun isAllowedScheme(url: String, embedded: Boolean): Boolean = when {
        embedded -> policy.allowEmbeddedHttps && url.startsWith("https://", true)
        url.startsWith("https://", true) -> true
        url.startsWith("http://", true) -> policy.allowHttpDirect
        else -> false
    }

    private fun normalize(url: String): String = url.trim().removeSuffix("/")

    private fun qualityScore(value: Int?): Int = when (value) {
        2160, 4320 -> 7
        1440 -> 6
        1080 -> 5
        720 -> 4
        480 -> 3
        360 -> 2
        else -> 0
    }
}
