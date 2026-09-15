package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceSelectionPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceSelectionPolicyTest {
    @Test fun prefersClosestQualityAtOrBelowTarget() {
        val ranked = SourceSelectionPolicy.rank(
            listOf(
                PlaybackSource("https://example.test/1080", "1080p", quality = 1080),
                PlaybackSource("https://example.test/720", "720p", quality = 720),
                PlaybackSource("https://example.test/480", "480p", quality = 480)
            ),
            preferredQuality = 720
        )
        assertEquals(720, ranked.first().quality)
    }

    @Test fun preferredLanguageWinsBeforeQuality() {
        val ranked = SourceSelectionPolicy.rank(
            listOf(
                PlaybackSource("https://example.test/en", "English", quality = 720, language = "en"),
                PlaybackSource("https://example.test/ar", "Arabic", quality = 480, language = "ar")
            ),
            preferredLanguage = "ar"
        )
        assertEquals("ar", ranked.first().language)
    }

    @Test fun proxySourcesAreExcluded() {
        val ranked = SourceSelectionPolicy.rank(
            listOf(PlaybackSource("https://example.test/proxy", "Proxy", requiresProxy = true))
        )
        assertTrue(ranked.isEmpty())
    }
}
