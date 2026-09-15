package com.dstwrtv.app.streaming.data.source

import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceProvider
import com.dstwrtv.app.streaming.domain.source.SourceRequest
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Generic Stremio-compatible stream adapter.
 * It knows the protocol, not a specific stream site. The endpoint is supplied
 * by the provider registry/configuration layer, so the app can add or retire
 * adapters without changing the UI or player.
 */
class StremioAddonSourceProvider(
    override val id: String,
    override val priority: Int,
    private val baseUrl: String,
    private val client: OkHttpClient,
    private val moshi: Moshi = Moshi.Builder().build()
) : SourceProvider {
    private val adapter = moshi.adapter(StreamResponseDto::class.java)

    override suspend fun resolve(request: SourceRequest): List<PlaybackSource> = withContext(Dispatchers.IO) {
        val type = when (request.mediaType) {
            MediaType.MOVIE -> "movie"
            MediaType.TV_SHOW -> "series"
        }
        val id = request.providerId.trim()
        if (id.isBlank()) return@withContext emptyList()

        val streamId = buildString {
            append(id)
            if (request.mediaType == MediaType.TV_SHOW) {
                val season = request.seasonNumber ?: return@withContext emptyList()
                val episode = request.episodeNumber ?: return@withContext emptyList()
                append(":").append(season).append(":").append(episode)
            }
        }
        val endpoint = baseUrl.trimEnd('/').toHttpUrl().newBuilder()
            .addPathSegments("stream/$type/$streamId.json")
            .build()
        val response = client.newCall(Request.Builder().url(endpoint).get().build()).execute()
        if (!response.isSuccessful) return@withContext emptyList()
        val body = response.body?.string().orEmpty()
        val parsed = runCatching { adapter.fromJson(body) }.getOrNull() ?: return@withContext emptyList()
        parsed.streams.orEmpty().mapNotNull { stream ->
            val url = stream.url?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
                ?: return@mapNotNull null
            PlaybackSource(
                url = url,
                label = stream.title.orEmpty().ifBlank { id },
                quality = stream.behaviorHints?.videoSize,
                format = stream.behaviorHints?.filename?.substringAfterLast('.', "").takeIf { it.isNotBlank() },
                language = stream.language,
                headers = stream.headers.orEmpty(),
                isEmbedded = false,
                requiresProxy = false
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class StreamResponseDto(
    val streams: List<StreamDto>?
)

@JsonClass(generateAdapter = true)
data class StreamDto(
    val url: String?,
    val title: String?,
    val language: String?,
    val headers: Map<String, String>?,
    val behaviorHints: BehaviorHintsDto?
)

@JsonClass(generateAdapter = true)
data class BehaviorHintsDto(
    val filename: String?,
    val videoSize: Int?
)
