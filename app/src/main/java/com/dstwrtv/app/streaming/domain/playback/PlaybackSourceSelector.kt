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

    private fun qualityScore(value: String?): Int = when (value?.lowercase()) {
        "8k" -> 8
        "4k", "2160p" -> 7
        "1440p" -> 6
        "1080p", "fhd" -> 5
        "720p", "hd" -> 4
        "480p" -> 3
        "360p" -> 2
        else -> 0
    }
}
