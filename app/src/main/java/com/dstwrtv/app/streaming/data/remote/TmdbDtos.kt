package com.dstwrtv.app.streaming.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbPageDto<T>(
    val page: Int = 1,
    @Json(name = "total_pages") val totalPages: Int = 1,
    @Json(name = "total_results") val totalResults: Int = 0,
    val results: List<T> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TmdbMovieDto(
    val id: Int,
    @Json(name = "title") val title: String? = null,
    @Json(name = "original_title") val originalTitle: String? = null,
    val name: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "vote_count") val voteCount: Int = 0,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "genre_ids") val genreIds: List<Int>? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbTvDto(
    val id: Int,
    val name: String? = null,
    @Json(name = "original_name") val originalName: String? = null,
    val title: String? = null,
    val overview: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "vote_count") val voteCount: Int = 0,
    @Json(name = "number_of_seasons") val numberOfSeasons: Int? = null,
    @Json(name = "number_of_episodes") val numberOfEpisodes: Int? = null,
    @Json(name = "genre_ids") val genreIds: List<Int>? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbSeasonDto(
    @Json(name = "season_number") val seasonNumber: Int = 0,
    val name: String? = null,
    val overview: String? = null,
    @Json(name = "air_date") val airDate: String? = null,
    @Json(name = "episode_count") val episodeCount: Int = 0,
    @Json(name = "poster_path") val posterPath: String? = null,
    val episodes: List<TmdbEpisodeDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TmdbEpisodeDto(
    @Json(name = "episode_number") val episodeNumber: Int = 0,
    val name: String? = null,
    val overview: String? = null,
    @Json(name = "air_date") val airDate: String? = null,
    val runtime: Int? = null,
    @Json(name = "still_path") val stillPath: String? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0
)
