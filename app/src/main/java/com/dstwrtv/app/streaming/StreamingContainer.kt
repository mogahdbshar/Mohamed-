package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.data.remote.CinemetaApi
import com.dstwrtv.app.streaming.data.remote.CinemetaMetadataProvider
import com.dstwrtv.app.streaming.data.remote.TvMazeApi
import com.dstwrtv.app.streaming.data.remote.TvMazeMetadataProvider
import com.dstwrtv.app.streaming.domain.metadata.MetadataEngine
import com.dstwrtv.app.streaming.domain.source.InMemorySourceHealthStore
import com.dstwrtv.app.streaming.domain.source.SourceEngine
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Composition root for the streaming subsystem.
 * No user-supplied API key is required for the baseline metadata engine.
 * Keyed providers such as TMDB remain optional adapters and are not required
 * for the baseline catalog to operate.
 */
object StreamingContainer {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(18, TimeUnit.SECONDS)
            .writeTimeout(18, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val cinemetaApi: CinemetaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://v3-cinemeta.strem.io/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CinemetaApi::class.java)
    }

    private val tvMazeApi: TvMazeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TvMazeApi::class.java)
    }

    val metadataEngine: MetadataEngine by lazy {
        MetadataEngine(
            providers = listOf(
                CinemetaMetadataProvider(cinemetaApi),
                TvMazeMetadataProvider(tvMazeApi)
            )
        )
    }

    val sourceEngine: SourceEngine by lazy {
        SourceEngine(
            providers = emptyList(),
            healthStore = InMemorySourceHealthStore()
        )
    }
}
