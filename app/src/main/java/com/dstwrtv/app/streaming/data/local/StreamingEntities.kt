package com.dstwrtv.app.streaming.data.local

import androidx.room.Entity

@Entity(tableName = "streaming_movies")
data class MovieEntity(
    @androidx.room.PrimaryKey val id: Int,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val releaseDate: String?,
    val rating: Double,
    val voteCount: Int,
    val runtimeMinutes: Int?,
    val genreIds: String,
    val posterPath: String?,
    val backdropPath: String?,
    val updatedAt: Long
)

@Entity(tableName = "streaming_tv_shows")
data class TvShowEntity(
    @androidx.room.PrimaryKey val id: Int,
    val name: String,
    val originalName: String?,
    val overview: String?,
    val firstAirDate: String?,
    val rating: Double,
    val voteCount: Int,
    val numberOfSeasons: Int?,
    val numberOfEpisodes: Int?,
    val genreIds: String,
    val posterPath: String?,
    val backdropPath: String?,
    val updatedAt: Long
)

@Entity(tableName = "streaming_seasons", primaryKeys = ["tvShowId", "seasonNumber"])
data class SeasonEntity(
    val tvShowId: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String?,
    val airDate: String?,
    val episodeCount: Int,
    val posterPath: String?,
    val updatedAt: Long
)

@Entity(tableName = "streaming_episodes", primaryKeys = ["tvShowId", "seasonNumber", "episodeNumber"])
data class EpisodeEntity(
    val tvShowId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val name: String,
    val overview: String?,
    val airDate: String?,
    val runtimeMinutes: Int?,
    val stillPath: String?,
    val rating: Double,
    val updatedAt: Long
)

@Entity(tableName = "streaming_watch_history")
data class WatchHistoryEntity(
    @androidx.room.PrimaryKey val mediaKey: String,
    val mediaType: String,
    val tmdbId: Int,
    val seasonNumber: Int?,
    val episodeNumber: Int?,
    val positionMs: Long,
    val durationMs: Long,
    val watchedAt: Long
)

@Entity(tableName = "streaming_favorites", primaryKeys = ["mediaType", "tmdbId", "seasonNumber", "episodeNumber"])
data class StreamingFavoriteEntity(
    val mediaType: String,
    val tmdbId: Int,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val addedAt: Long = System.currentTimeMillis()
)
