package com.dstwrtv.app.streaming.domain.metadata

import com.dstwrtv.app.streaming.domain.model.CatalogPage
import com.dstwrtv.app.streaming.domain.model.Episode
import com.dstwrtv.app.streaming.domain.model.Movie
import com.dstwrtv.app.streaming.domain.model.Season
import com.dstwrtv.app.streaming.domain.model.TvShow

interface MetadataProvider {
    val id: String

    suspend fun popularMovies(page: Int = 1): Result<CatalogPage<Movie>>
    suspend fun popularTvShows(page: Int = 1): Result<CatalogPage<TvShow>>
    suspend fun searchMovies(query: String, page: Int = 1): Result<CatalogPage<Movie>>
    suspend fun searchTvShows(query: String, page: Int = 1): Result<CatalogPage<TvShow>>
    suspend fun movieDetails(providerId: String): Result<Movie>
    suspend fun tvDetails(providerId: String): Result<TvShow>
    suspend fun seasonDetails(providerId: String, seasonNumber: Int): Result<Pair<Season, List<Episode>>>
}
