package com.dstwrtv.app.streaming.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TvMazeSearchResultDto(
    @Json(name = "score") val score: Double? = null,
    @Json(name = "show") val show: TvMazeShowDto? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeShowDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "genres") val genres: List<String>? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "averageRuntime") val averageRuntime: Int? = null,
    @Json(name = "premiered") val premiered: String? = null,
    @Json(name = "ended") val ended: String? = null,
    @Json(name = "officialSite") val officialSite: String? = null,
    @Json(name = "rating") val rating: TvMazeRatingDto? = null,
    @Json(name = "weight") val weight: Int? = null,
    @Json(name = "network") val network: TvMazeNetworkDto? = null,
    @Json(name = "webChannel") val webChannel: TvMazeNetworkDto? = null,
    @Json(name = "image") val image: TvMazeImageDto? = null,
    @Json(name = "summary") val summary: String? = null,
    @Json(name = "externals") val externals: TvMazeExternalsDto? = null,
    @Json(name = "embedded") val embedded: TvMazeEmbeddedDto? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeRatingDto(
    @Json(name = "average") val average: Double? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeNetworkDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "country") val country: TvMazeCountryDto? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeCountryDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "code") val code: String? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeImageDto(
    @Json(name = "medium") val medium: String? = null,
    @Json(name = "original") val original: String? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeExternalsDto(
    @Json(name = "tvrage") val tvrage: Int? = null,
    @Json(name = "thetvdb") val thetvdb: Int? = null,
    @Json(name = "imdb") val imdb: String? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeEmbeddedDto(
    @Json(name = "episodes") val episodes: List<TvMazeEpisodeDto>? = null
)

@JsonClass(generateAdapter = true)
data class TvMazeEpisodeDto(
    @Json(name = "id") val id: Int,
    @Json(name = "url") val url: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "season") val season: Int? = null,
    @Json(name = "number") val number: Int? = null,
    @Json(name = "airdate") val airdate: String? = null,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "rating") val rating: TvMazeRatingDto? = null,
    @Json(name = "image") val image: TvMazeImageDto? = null,
    @Json(name = "original") val original: String? = null,
    @Json(name = "summary") val summary: String? = null
)
