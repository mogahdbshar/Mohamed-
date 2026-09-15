package com.dstwrtv.app.streaming.data.remote

import com.dstwrtv.app.streaming.domain.metadata.MetadataProvider
import com.dstwrtv.app.streaming.domain.model.CatalogPage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.ImageSet
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow

/** Keyless TV metadata provider with bounded client-side request pacing. */
class TvMazeMetadataProvider(
    private val api: TvMazeApi,
    private val pageSize: Int = 250,
    private val limiter: ProviderRequestLimiter = ProviderRequestLimiter(500L)
) : MetadataProvider {
    override val id: String = "tvmaze"

    override suspend fun popularMovies(page: Int): Result<CatalogPage<Movie>> =
        Result.failure(UnsupportedOperationException("TVMaze is a TV-only metadata provider"))

    override suspend fun searchMovies(query: String, page: Int): Result<CatalogPage<Movie>> =
        Result.failure(UnsupportedOperationException("TVMaze is a TV-only metadata provider"))

    override suspend fun popularTvShows(page: Int): Result<CatalogPage<TvShow>> = runCatching {
        require(page >= 1) { "Page must be positive" }
        limiter.awaitTurn()
        val shows = api.shows(page - 1)
            .sortedWith(compareByDescending<TvMazeShowDto> { it.rating?.average ?: 0.0 }
                .thenByDescending { it.weight ?: 0 })
        CatalogPage(
            page = page,
            totalPages = if (shows.size < pageSize) page else -1,
            totalResults = -1,
            items = shows.map { it.toDomain() }
        )
    }

    override suspend fun searchTvShows(query: String, page: Int): Result<CatalogPage<TvShow>> = runCatching {
        require(query.isNotBlank()) { "Search query is empty" }
        require(page == 1) { "TVMaze search does not expose page offsets" }
        limiter.awaitTurn()
        val shows = api.searchShows(query.trim())
            .mapNotNull { it.show }
            .sortedByDescending { it.rating?.average ?: 0.0 }
        CatalogPage(page = 1, totalPages = 1, totalResults = shows.size, items = shows.map { it.toDomain() })
    }

    override suspend fun movieDetails(providerId: String): Result<Movie> =
        Result.failure(UnsupportedOperationException("TVMaze is a TV-only metadata provider"))

    override suspend fun tvDetails(providerId: String): Result<TvShow> = runCatching {
        limiter.awaitTurn()
        api.show(parseId(providerId)).toDomain()
    }

    override suspend fun seasonDetails(
        providerId: String,
        seasonNumber: Int
    ): Result<Pair<Season, List<Episode>>> = runCatching {
        require(seasonNumber >= 0) { "Season number must not be negative" }
        limiter.awaitTurn()
        val show = api.show(parseId(providerId), embed = "episodes")
        val episodes = show.embedded?.episodes.orEmpty()
            .filter { it.season == seasonNumber }
            .sortedBy { it.number ?: Int.MAX_VALUE }
        val showId = show.id
        Season(tvShowId = showId, seasonNumber = seasonNumber, name = "Season $seasonNumber", episodeCount = episodes.size) to
            episodes.mapNotNull { episode ->
                val number = episode.number ?: return@mapNotNull null
                Episode(
                    tvShowId = showId,
                    seasonNumber = seasonNumber,
                    episodeNumber = number,
                    name = episode.name.orEmpty().ifBlank { "Episode $number" },
                    overview = cleanSummary(episode.summary),
                    airDate = episode.airdate,
                    runtimeMinutes = episode.runtime,
                    stillPath = episode.image?.original ?: episode.image?.medium,
                    rating = episode.rating?.average ?: 0.0
                )
            }
    }

    private fun TvMazeShowDto.toDomain(): TvShow = TvShow(
        id = id,
        name = name.orEmpty(),
        originalName = name,
        overview = cleanSummary(summary),
        firstAirDate = premiered,
        rating = rating?.average ?: 0.0,
        voteCount = 0,
        numberOfSeasons = embedded?.episodes.orEmpty().mapNotNull { it.season }.distinct().size.takeIf { it > 0 },
        numberOfEpisodes = embedded?.episodes?.size,
        genreIds = emptyList(),
        images = ImageSet(posterPath = image?.original ?: image?.medium, backdropPath = null, logoPath = null),
        provider = "tvmaze",
        providerId = "tvmaze:$id"
    )

    private fun parseId(providerId: String): Int {
        require(providerId.startsWith("tvmaze:")) { "TVMaze requires a tvmaze:<id> provider id" }
        return providerId.removePrefix("tvmaze:").toIntOrNull() ?: error("Invalid TVMaze provider id")
    }

    private fun cleanSummary(value: String?): String? = value
        ?.replace(Regex("<[^>]+>"), "")
        ?.replace("&amp;", "&")
        ?.replace("&quot;", "\"")
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}
