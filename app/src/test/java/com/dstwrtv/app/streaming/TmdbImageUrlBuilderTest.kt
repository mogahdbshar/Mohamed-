package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.core.TmdbImageUrlBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TmdbImageUrlBuilderTest {
    @Test
    fun buildsSizeAwarePosterUrl() {
        assertEquals(
            "https://image.tmdb.org/t/p/w342/abc.jpg",
            TmdbImageUrlBuilder.poster("/abc.jpg")
        )
    }

    @Test
    fun returnsNullForMissingPath() {
        assertNull(TmdbImageUrlBuilder.backdrop(null))
        assertNull(TmdbImageUrlBuilder.poster("   "))
    }
}
