package com.dstwrtv.app.streaming.domain.metadata

import com.dstwrtv.app.streaming.domain.model.CatalogPage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MetadataEngine(
    providers: List<MetadataProvider>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val providers = providers.distinctBy { it.id }
    private val mutex = Mutex()
    private val inFlight = mutableMapOf<String, Deferred<*>>()

    suspend fun popularMovies(page: Int = 1): Result<CatalogPage<Movie>> =
        coalesced("popular-movies:$page") { aggregateMovies { it.popularMovies(page) } }

    suspend fun popularTvShows(page: Int = 1): Result<CatalogPage<TvShow>> =
        coalesced("popular-tv:$page") { aggregateTvShows { it.popularTvShows(page) } }

    suspend fun searchMovies(query: String, page: Int = 1): Result<CatalogPage<Movie>> =
        coalesced("search-movies:${query.trim().lowercase()}:$page") {
            aggregateMovies { it.searchMovies(query, page) }
        }

    suspend fun searchTvShows(query: String, page: Int = 1): Result<CatalogPage<TvShow>> =
        coalesced("search-tv:${query.trim().lowercase()}:$page") {
            aggregateTvShows { it.searchTvShows(query, page) }
        }

    suspend fun movieDetails(providerId: String, preferredProvider: String? = null): Result<Movie> =
        coalesced("movie-details:${preferredProvider.orEmpty()}:$providerId") {
            firstSuccessful(providerId, preferredProvider) { it.movieDetails(providerId) }
        }

    suspend fun tvDetails(providerId: String, preferredProvider: String? = null): Result<TvShow> =
        coalesced("tv-details:${preferredProvider.orEmpty()}:$providerId") {
            firstSuccessful(providerId, preferredProvider) { it.tvDetails(providerId) }
        }

    suspend fun seasonDetails(
        providerId: String,
        seasonNumber: Int,
        preferredProvider: String? = null
    ): Result<Pair<Season, List<Episode>>> = coalesced("season:$preferredProvider:$providerId:$seasonNumber") {
        firstSuccessful(providerId, preferredProvider) { it.seasonDetails(providerId, seasonNumber) }
    }

    private suspend fun aggregateMovies(
        call: suspend (MetadataProvider) -> Result<CatalogPage<Movie>>
    ): Result<CatalogPage<Movie>> = runCatching {
        val pages = providers.map { provider ->
            scope.async { call(provider).getOrNull() }
        }.awaitAll().filterNotNull()
        if (pages.isEmpty()) error("No metadata provider returned movie data")
        val merged = linkedMapOf<String, Movie>()
        pages.forEach { page ->
            page.items.forEach { movie ->
                val key = movie.providerId?.lowercase() ?: "${movie.title.lowercase()}|${movie.releaseDate.orEmpty()}"
                merged.putIfAbsent(key, movie)
            }
        }
        CatalogPage(
            page = pages.minOf { it.page },
            totalPages = pages.map { it.totalPages }.filter { it > 0 }.maxOrNull() ?: -1,
            totalResults = merged.size,
            items = merged.values.toList()
        )
    }

    private suspend fun aggregateTvShows(
        call: suspend (MetadataProvider) -> Result<CatalogPage<TvShow>>
    ): Result<CatalogPage<TvShow>> = runCatching {
        val pages = providers.map { provider ->
            scope.async { call(provider).getOrNull() }
        }.awaitAll().filterNotNull()
        if (pages.isEmpty()) error("No metadata provider returned TV data")
        val merged = linkedMapOf<String, TvShow>()
        pages.forEach { page ->
            page.items.forEach { show ->
                val key = show.providerId?.lowercase() ?: "${show.name.lowercase()}|${show.firstAirDate.orEmpty()}"
                merged.putIfAbsent(key, show)
            }
        }
        CatalogPage(
            page = pages.minOf { it.page },
            totalPages = pages.map { it.totalPages }.filter { it > 0 }.maxOrNull() ?: -1,
            totalResults = merged.size,
            items = merged.values.toList()
        )
    }

    private suspend fun <T> firstSuccessful(
        providerId: String,
        preferredProvider: String?,
        call: suspend (MetadataProvider) -> Result<T>
    ): Result<T> {
        val ordered = if (preferredProvider.isNullOrBlank()) {
            providers
        } else {
            providers.sortedByDescending { it.id == preferredProvider }
        }
        var lastError: Throwable? = null
        for (provider in ordered) {
            val result = runCatching { call(provider) }.getOrElse { Result.failure(it) }
            result.onFailure { lastError = it }
            if (result.isSuccess) return result
        }
        return Result.failure(lastError ?: IllegalStateException("No metadata provider is available for $providerId"))
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> coalesced(key: String, block: suspend () -> T): T {
        val deferred = mutex.withLock {
            (inFlight[key] as? Deferred<T>) ?: scope.async {
                try {
                    block()
                } finally {
                    mutex.withLock { inFlight.remove(key) }
                }
            }.also { inFlight[key] = it }
        }
        return deferred.await()
    }
}
