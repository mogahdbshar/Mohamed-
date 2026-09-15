package com.dstwrtv.app.streaming.data.remote

import com.squareup.moshi.Json

data class CinemetaCatalogResponse(
    @Json(name = "metas") val metas: List<CinemetaMetaDto> = emptyList()
)

data class CinemetaMetaResponse(
    @Json(name = "meta") val meta: CinemetaMetaDto? = null
)

data class CinemetaMetaDto(
    val id: String? = null,
    val type: String? = null,
    val name: String? = null,
    val releaseInfo: String? = null,
    val poster: String? = null,
    val background: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val imdbRating: Double? = null,
    val imdbVotes: Int? = null,
    val runtime: String? = null,
    val genres: List<String>? = null,
    val released: String? = null,
    val videos: List<CinemetaVideoDto>? = null
)

data class CinemetaVideoDto(
    val id: String? = null,
    val title: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val released: String? = null,
    val overview: String? = null,
    val thumbnail: String? = null,
    val runtime: String? = null,
    val imdbRating: Double? = null
)
