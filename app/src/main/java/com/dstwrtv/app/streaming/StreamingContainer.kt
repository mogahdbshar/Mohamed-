package com.dstwrtv.app.streaming

import com.dstwrtv.app.BuildConfig
import com.dstwrtv.app.streaming.data.remote.TmdbCatalogRepository
import com.dstwrtv.app.streaming.data.remote.TmdbClient
import com.dstwrtv.app.streaming.domain.source.InMemorySourceHealthStore
import com.dstwrtv.app.streaming.domain.source.SourceEngine

/**
 * Small composition root for the new streaming subsystem.
 * It is intentionally independent from the existing IPTV repository and remote-control code.
 */
object StreamingContainer {
    val catalogRepository: TmdbCatalogRepository by lazy {
        TmdbCatalogRepository(
            api = TmdbClient.create(),
            apiKeyProvider = { BuildConfig.TMDB_API_KEY }
        )
    }

    val sourceEngine: SourceEngine by lazy {
        SourceEngine(
            providers = emptyList(),
            healthStore = InMemorySourceHealthStore()
        )
    }
}
