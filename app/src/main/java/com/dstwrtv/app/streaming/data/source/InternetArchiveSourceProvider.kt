package com.dstwrtv.app.streaming.data.source

import com.dstwrtv.app.streaming.data.remote.InternetArchiveApi
import com.dstwrtv.app.streaming.domain.model.MediaType
import com.dstwrtv.app.streaming.domain.source.DescribedSourceProvider
import com.dstwrtv.app.streaming.domain.source.PlaybackSource
import com.dstwrtv.app.streaming.domain.source.SourceProviderCapabilities
import com.dstwrtv.app.streaming.domain.source.SourceRequest
import java.net.URLEncoder

/** Direct playback adapter for publicly accessible Internet Archive media. */
class InternetArchiveSourceProvider(
    override val id: String = "internet_archive",
    override val priority: Int = 20,
    private val api: InternetArchiveApi
) : DescribedSourceProvider {
    override val capabilities = SourceProviderCapabilities(
        supportsMovies = true, supportsTvEpisodes = false, supportsChannels = false,
        supportsSubtitles = false, supportsMultipleQualities = true, supportsEmbeddedPlayback = false
    )

    override suspend fun resolve(request: SourceRequest): List<PlaybackSource> {
        if (request.mediaType != MediaType.MOVIE) return emptyList()
        val title = request.title?.trim().orEmpty()
        if (title.isBlank()) return emptyList()
        val safeQuery = title.replace("\"", "")
        val docs = runCatching {
            api.search("title:(\"$safeQuery\")", rows = 10).response?.docs.orEmpty()
        }.getOrDefault(emptyList())

        val results = mutableListOf<PlaybackSource>()
        for (doc in docs) {
            val identifier = doc.identifier ?: continue
            val files = runCatching { api.metadata(identifier).files }.getOrDefault(emptyList())
            val file = files.firstOrNull { candidate ->
                val name = candidate.name ?: return@firstOrNull false
                val format = candidate.format.orEmpty().uppercase()
                format.contains("MPEG4") || format.contains("H.264") ||
                    name.endsWith(".mp4", true) || name.endsWith(".m3u8", true)
            } ?: continue
            val name = file.name ?: continue
            val encoded = name.split('/').joinToString("/") {
                URLEncoder.encode(it, "UTF-8").replace("+", "%20")
            }
            results += PlaybackSource(
                url = "https://archive.org/download/$identifier/$encoded",
                label = doc.title ?: identifier,
                quality = when {
                    name.contains("2160", true) -> 2160
                    name.contains("1080", true) -> 1080
                    name.contains("720", true) -> 720
                    else -> null
                },
                format = if (name.endsWith(".m3u8", true)) "hls" else "mp4"
            )
        }
        return results
    }
}
