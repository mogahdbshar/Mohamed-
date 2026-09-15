package com.dstwrtv.app.streaming.data.remote

import com.dstwrtv.app.streaming.domain.model.*

fun TmdbMovieDto.toDomain() = Movie(
    id = id,
    title = title ?: name ?: originalTitle.orEmpty(),
    originalTitle = originalTitle,
    overview = overview,
    releaseDate = releaseDate,
    rating = voteAverage,
    voteCount = voteCount,
    runtimeMinutes = runtime,
    genreIds = genreIds.orEmpty(),
    images = ImageSet(posterPath = posterPath, backdropPath = backdropPath)
)

fun TmdbTvDto.toDomain() = TvShow(
    id = id,
    name = name ?: title ?: originalName.orEmpty(),
    originalName = originalName,
    overview = overview,
    firstAirDate = firstAirDate,
    rating = voteAverage,
    voteCount = voteCount,
    numberOfSeasons = numberOfSeasons,
    numberOfEpisodes = numberOfEpisodes,
    genreIds = genreIds.orEmpty(),
    images = ImageSet(posterPath = posterPath, backdropPath = backdropPath)
)

fun TmdbSeasonDto.toDomain(tvShowId: Int) = Season(
    tvShowId = tvShowId,
    seasonNumber = seasonNumber,
    name = name ?: "Season $seasonNumber",
    overview = overview,
    airDate = airDate,
    episodeCount = episodeCount,
    posterPath = posterPath
)

fun TmdbEpisodeDto.toDomain(tvShowId: Int, seasonNumber: Int) = Episode(
    tvShowId = tvShowId,
    seasonNumber = seasonNumber,
    episodeNumber = episodeNumber,
    name = name ?: "Episode $episodeNumber",
    overview = overview,
    airDate = airDate,
    runtimeMinutes = runtime,
    stillPath = stillPath,
    rating = voteAverage
)
