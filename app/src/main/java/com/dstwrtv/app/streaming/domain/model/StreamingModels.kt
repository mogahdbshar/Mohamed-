package com.dstwrtv.app.streaming.domain.model

data class ImageSet(
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val logoPath: String? = null
)

data class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String? = null,
    val overview: String? = null,
    val releaseDate: String? = null,
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val runtimeMinutes: Int? = null,
    val genreIds: List<Int> = emptyList(),
    val images: ImageSet = ImageSet(),
    val provider: String = "tmdb",
    val providerId: String? = null
)

data class TvShow(
    val id: Int,
    val name: String,
    val originalName: String? = null,
    val overview: String? = null,
    val firstAirDate: String? = null,
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val genreIds: List<Int> = emptyList(),
    val images: ImageSet = ImageSet(),
    val provider: String = "tmdb",
    val providerId: String? = null
)

data class Season(
    val tvShowId: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String? = null,
    val airDate: String? = null,
    val episodeCount: Int = 0,
    val posterPath: String? = null
)

data class Episode(
    val tvShowId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val name: String,
    val overview: String? = null,
    val airDate: String? = null,
    val runtimeMinutes: Int? = null,
    val stillPath: String? = null,
    val rating: Double = 0.0
)

data class CatalogPage<T>(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val items: List<T>
)

data class MediaDetails(
    val type: MediaType,
    val movie: Movie? = null,
    val tvShow: TvShow? = null,
    val seasons: List<Season> = emptyList()
)
