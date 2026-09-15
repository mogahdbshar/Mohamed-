package com.dstwrtv.app.streaming.data.source

import com.dstwrtv.app.streaming.domain.source.DescribedSourceProvider
import com.dstwrtv.app.streaming.domain.source.SourceProvider
import com.dstwrtv.app.streaming.domain.source.SourceProviderCapabilities
import com.dstwrtv.app.streaming.data.source.StremioAddonSourceProvider
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient

object SourceProviderFactory {
    fun create(
        config: SourceProviderConfig,
        client: OkHttpClient,
        moshi: Moshi = Moshi.Builder().build()
    ): SourceProvider? {
        if (!config.isUsable()) return null
        return ConfiguredStremioProvider(
            delegate = StremioAddonSourceProvider(
                id = config.id,
                priority = config.priority,
                baseUrl = config.baseUrl,
                client = client,
                moshi = moshi
            ),
            capabilities = SourceProviderCapabilities(
                supportsMovies = config.supportsMovies,
                supportsTvEpisodes = config.supportsTvEpisodes,
                supportsSubtitles = config.supportsSubtitles,
                supportsMultipleQualities = config.supportsMultipleQualities,
                supportsEmbeddedPlayback = true
            )
        )
    }

    private class ConfiguredStremioProvider(
        private val delegate: SourceProvider,
        override val capabilities: SourceProviderCapabilities
    ) : DescribedSourceProvider {
        override val id: String get() = delegate.id
        override val priority: Int get() = delegate.priority
        override suspend fun resolve(request: com.dstwrtv.app.streaming.domain.source.SourceRequest) =
            delegate.resolve(request)
    }
}
