package com.dstwrtv.app.streaming.data.remote

import com.dstwrtv.app.streaming.domain.metadata.MetadataProvider
import com.dstwrtv.app.streaming.domain.model.CatalogPage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.ImageSet
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import java.security.MessageDigest

class CinemetaMetadataProvider(
    private val api: CinemetaApi,
    private val pageSize: Int = 100
) : MetadataProvider {
    override val id: String = "cinemeta"

    override suspend fun popularMovies(page: Int): Result<CatalogPage<Movie>> = runCatching {
        require(page >= 1) { "Page must be positive" }
        val response = api.catalog("movie", "top", skip = (page - 1) * pageSize)
        catalogMovies(response.metas, page)
    }

    override suspend fun popularTvShows(page: Int): Result<CatalogPage<TvShow>> = runCatching {
        require(page >= 1) { "Page must be positive" }
        val response = api.catalog("series", "imdbRating", skip = (page - 1) * pageSize)
        catalogTvShows(response.metas, page)
    }

    override suspend fun searchMovies(query: String, page: Int): Result<CatalogPage<Movie>> = runCatching {
        require(query.isNotBlank()) { "Search query is empty" }
        val response = api.catalog("movie", "top", search = query.trim(), skip = (page - 1) * pageSize)
        catalogMovies(response.metas, page)
    }

    override suspend fun searchTvShows(query: String, page: Int): Result<CatalogPage<TvShow>> = runCatching {
        require(query.isNotBlank()) { "Search query is empty" }
        val response = api.catalog("series", "imdbRating", search = query.trim(), skip = (page - 1) * pageSize)
        catalogTvShows(response.metas, page)
    }

    override suspend fun movieDetails(providerId: String): Result<Movie> = runCatching {
        require(providerId.startsWith("tt")) { "Cinemeta requires an IMDb id" }
        val meta = api.meta("movie", providerId).meta ?: error("Cinemeta returned no movie metadata")
        meta.toMovie()
    }

    override suspend fun tvDetails(providerId: String): Result<TvShow> = runCatching {
        require(providerId.startsWith("tt")) { "Cinemeta requires an IMDb id" }
        val meta = api.meta("series", providerId).meta ?: error("Cinemeta returned no series metadata")
        meta.toTvShow()
    }

    override suspend fun seasonDetails(providerId: String, seasonNumber: Int): Result<Pair<Season, List<Episode>>> = runCatching {
        require(providerId.startsWith("tt")) { "Cinemeta requires an IMDb id" }
        require(seasonNumber >= 0) { "Season number must not be negative" }
        val meta = api.meta("series", providerId).meta ?: error("Cinemeta returned no series metadata")
        val videos = meta.videos.orEmpty().filter { it.season == seasonNumber }.sortedBy { it.episode ?: Int.MAX_VALUE }
        val tvId = stableId(providerId)
        val season = Season(
            tvShowId = tvId,
            seasonNumber = seasonNumber,
            name = "Season $seasonNumber",
            episodeCount = videos.size
        )
        season to videos.mapNotNull { video ->
            val episodeNumber = video.episode ?: return@mapNotNull null
            Episode(
                tvShowId = tvId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
                name = video.title.orEmpty().ifBlank { "Episode $episodeNumber" },
                overview = video.overview,
                airDate = video.released?.take(10),
                runtimeMinutes = parseRuntime(video.runtime),
                stillPath = video.thumbnail,
                rating = video.imdbRating ?: 0.0
            )
        }
    }

    private fun catalogMovies(items: List<CinemetaMetaDto>, page: Int): CatalogPage<Movie> =
        CatalogPage(page, if (items.size < pageSize) page else -1, -1, items.mapNotNull { it.toMovieOrNull() })

    private fun catalogTvShows(items: List<CinemetaMetaDto>, page: Int): CatalogPage<TvShow> =
        CatalogPage(page, if (items.size < pageSize) page else -1, -1, items.mapNotNull { it.toTvShowOrNull() })

    private fun CinemetaMetaDto.toMovieOrNull(): Movie? = runCatching { toMovie() }.getOrNull()

    private fun CinemetaMetaDto.toTvShowOrNull(): TvShow? = runCatching { toTvShow() }.getOrNull()

    private fun CinemetaMetaDto.toMovie(): Movie {
        val imdbId = id?.takeIf { it.startsWith("tt") } ?: error("Movie has no IMDb id")
        return Movie(
            id = stableId(imdbId),
            title = name.orEmpty().ifBlank { error("Movie has no title") },
            originalTitle = name,
            overview = description,
            releaseDate = released?.take(10) ?: releaseInfo,
            rating = imdbRating ?: 0.0,
            voteCount = imdbVotes ?: 0,
            runtimeMinutes = parseRuntime(runtime),
            images = ImageSet(posterPath = poster, backdropPath = background, logoPath = logo),
            provider = idProvider,
            providerId = imdbId
        )
    }

    private fun CinemetaMetaDto.toTvShow(): TvShow {
        val imdbId = id?.takeIf { it.startsWith("tt") } ?: error("Series has no IMDb id")
        val videos = videos.orEmpty()
        val seasons = videos.mapNotNull { it.season }.filter { it >= 0 }.distinct().size
        val episodes = videos.mapNotNull { it.episode }.distinct().size
        return TvShow(
            id = stableId(imdbId),
            name = name.orEmpty().ifBlank { error("Series has no title") },
            originalName = name,
            overview = description,
            firstAirDate = released?.take(10) ?: releaseInfo,
            rating = imdbRating ?: 0.0,
            voteCount = imdbVotes ?: 0,
            numberOfSeasons = seasons.takeIf { it > 0 },
            numberOfEpisodes = episodes.takeIf { it > 0 },
            images = ImageSet(posterPath = poster, backdropPath = background, logoPath = logo),
            provider = idProvider,
            providerId = imdbId
        )
    }

    private fun parseRuntime(value: String?): Int? = value?.trim()?.let {
        Regex("(\\d+)").find(it)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun stableId(value: String): Int {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return ((digest[0].toInt() and 0xff) shl 24) or
            ((digest[1].toInt() and 0xff) shl 16) or
            ((digest[2].toInt() and 0xff) shl 8) or
            (digest[3].toInt() and 0xff)
    }

    private val idProvider: String = "cinemeta"
}
