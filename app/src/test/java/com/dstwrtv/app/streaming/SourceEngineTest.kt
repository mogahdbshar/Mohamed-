package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.source.InMemorySourceHealthStore
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceEngine
import com.dstwrtv.app.streaming.domain.source.SourceProvider
import com.dstwrtv.app.streaming.domain.source.SourceRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SourceEngineTest {
    @Test
    fun ranksProvidersAndFiltersProxySources() = runBlocking {
        val providerA = object : SourceProvider {
            override val id = "a"
            override val priority = 10
            override suspend fun resolve(request: SourceRequest) = listOf(
                PlaybackSource("https://a.example/video.m3u8", "A", quality = 720),
                PlaybackSource("https://a.example/proxy", "A proxy", requiresProxy = true)
            )
        }
        val providerB = object : SourceProvider {
            override val id = "b"
            override val priority = 5
            override suspend fun resolve(request: SourceRequest) = listOf(
                PlaybackSource("https://b.example/video.m3u8", "B", quality = 1080)
            )
        }

        val engine = SourceEngine(listOf(providerB, providerA), InMemorySourceHealthStore())
        val result = engine.resolve(SourceRequest(MediaType.MOVIE, 123))

        assertEquals(2, result.sources.size)
        assertEquals("https://a.example/video.m3u8", result.sources[0].url)
        assertFalse(result.sources.any { it.requiresProxy })
    }
}
