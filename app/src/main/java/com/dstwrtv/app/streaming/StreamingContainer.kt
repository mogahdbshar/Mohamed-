package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.data.remote.CinemetaApi
import com.dstwrtv.app.streaming.data.remote.CinemetaMetadataProvider
import com.dstwrtv.app.streaming.data.remote.InternetArchiveApi
import com.dstwrtv.app.streaming.data.remote.TvMazeApi
import com.dstwrtv.app.streaming.data.remote.TvMazeMetadataProvider
import com.dstwrtv.app.streaming.data.source.InternetArchiveSourceProvider
import com.dstwrtv.app.streaming.domain.metadata.MetadataEngine
import com.dstwrtv.app.streaming.domain.source.InMemorySourceHealthStore
import com.dstwrtv.app.streaming.domain.source.SourceDiscoveryEngine
import com.dstwrtv.app.streaming.domain.source.SourceEngine
import com.dstwrtv.app.streaming.domain.source.SourceProvider
import com.dstwrtv.app.streaming.domain.source.SourceRegistry
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object StreamingContainer {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(18, TimeUnit.SECONDS)
            .writeTimeout(18, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val cinemetaApi by lazy { retrofit("https://v3-cinemeta.strem.io/").create(CinemetaApi::class.java) }
    private val tvMazeApi by lazy { retrofit("https://api.tvmaze.com/").create(TvMazeApi::class.java) }
    private val internetArchiveApi by lazy { retrofit("https://archive.org/").create(InternetArchiveApi::class.java) }

    val metadataEngine: MetadataEngine by lazy {
        MetadataEngine(listOf(CinemetaMetadataProvider(cinemetaApi), TvMazeMetadataProvider(tvMazeApi)))
    }

    val sourceRegistry: SourceRegistry by lazy {
        SourceRegistry(listOf<SourceProvider>(InternetArchiveSourceProvider(api = internetArchiveApi)))
    }

    private val sourceHealthStore by lazy { InMemorySourceHealthStore() }

    val sourceEngine: SourceEngine by lazy {
        SourceEngine({ sourceRegistry.all() }, sourceHealthStore, 4)
    }

    val sourceDiscoveryEngine: SourceDiscoveryEngine by lazy {
        SourceDiscoveryEngine(sourceRegistry, sourceEngine)
    }
}
