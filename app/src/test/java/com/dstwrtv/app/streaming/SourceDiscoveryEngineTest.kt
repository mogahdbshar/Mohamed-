package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.source.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SourceDiscoveryEngineTest {
    @Test
    fun duplicateSourcesAreCollapsed() = runBlocking {
        val provider = object : SourceProvider {
            override val id = "test"
            override val priority = 10
            override suspend fun resolve(request: SourceRequest) = listOf(
                PlaybackSource("https://example.test/a.m3u8", "A"),
                PlaybackSource("https://example.test/a.m3u8", "A duplicate")
            )
        }
        val registry = SourceRegistry(listOf(provider))
        val engine = SourceEngine(registry.all())
        val discovery = SourceDiscoveryEngine(registry, engine)
        val result = discovery.discover(
            SourceDiscoveryRequest(MediaType.MOVIE, "tmdb", "123")
        )
        assertEquals(1, result.sources.size)
        assertEquals("https://example.test/a.m3u8", result.sources.first().url)
    }
}
