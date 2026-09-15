package com.dstwrtv.app.streaming.data.local

import androidx.room.Entity

/** Stable cross-provider identity used to join metadata, playback and history. */
@Entity(tableName = "streaming_media_identity", primaryKeys = ["mediaType", "provider", "providerId"])
data class MediaIdentityEntity(
    val mediaType: String,
    val provider: String,
    val providerId: String,
    val imdbId: String? = null,
    val tmdbId: Int? = null,
    val tvmazeId: Int? = null,
    val title: String,
    val year: Int? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "streaming_catalog_cache", primaryKeys = ["mediaType", "provider", "providerId"])
data class CatalogCacheEntity(
    val mediaType: String,
    val provider: String,
    val providerId: String,
    val title: String,
    val originalTitle: String? = null,
    val overview: String? = null,
    val year: Int? = null,
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val genres: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis()
)
