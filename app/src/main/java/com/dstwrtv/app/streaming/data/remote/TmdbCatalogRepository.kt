package com.dstwrtv.app.streaming.data.remote

import com.dstwrtv.app.streaming.domain.model.CatalogPage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow

class TmdbCatalogRepository(
    private val api: TmdbApi,
    private val apiKeyProvider: () -> String,
    private val languageProvider: () -> String = { "ar-SA" }
) {
    private fun key(): String = apiKeyProvider().trim()

    suspend fun popularMovies(page: Int = 1): Result<CatalogPage<Movie>> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        api.popularMovies(key(), languageProvider(), page).let { response ->
            CatalogPage(response.page, response.totalPages, response.totalResults, response.results.map { it.toDomain() })
        }
    }

    suspend fun popularTvShows(page: Int = 1): Result<CatalogPage<TvShow>> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        api.popularTvShows(key(), languageProvider(), page).let { response ->
            CatalogPage(response.page, response.totalPages, response.totalResults, response.results.map { it.toDomain() })
        }
    }

    suspend fun searchMovies(query: String, page: Int = 1): Result<CatalogPage<Movie>> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        require(query.isNotBlank()) { "Search query is empty" }
        api.searchMovies(key(), query.trim(), languageProvider(), page).let { response ->
            CatalogPage(response.page, response.totalPages, response.totalResults, response.results.map { it.toDomain() })
        }
    }

    suspend fun searchTvShows(query: String, page: Int = 1): Result<CatalogPage<TvShow>> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        require(query.isNotBlank()) { "Search query is empty" }
        api.searchTvShows(key(), query.trim(), languageProvider(), page).let { response ->
            CatalogPage(response.page, response.totalPages, response.totalResults, response.results.map { it.toDomain() })
        }
    }

    suspend fun movieDetails(id: Int): Result<Movie> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        api.movieDetails(id, key(), languageProvider()).toDomain()
    }

    suspend fun tvDetails(id: Int): Result<TvShow> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        api.tvDetails(id, key(), languageProvider()).toDomain()
    }

    suspend fun seasonDetails(tvShowId: Int, seasonNumber: Int): Result<Pair<Season, List<Episode>>> = runCatching {
        require(key().isNotEmpty()) { "TMDB API key is not configured" }
        val season = api.seasonDetails(tvShowId, seasonNumber, key(), languageProvider())
        season.toDomain(tvShowId) to season.episodes.map { it.toDomain(tvShowId, seasonNumber) }
    }
}
