package com.dstwrtv.app.streaming.data.local

import androidx.room.Entity

@Entity(tableName = "streaming_media_identity", primaryKeys = ["mediaType", "provider", "providerId"])
data class MediaIdentityEntity(
    val mediaType: String,
    val provider: String,
    val providerId: String,
    val imdbId: String? = null,
    val tmdbId: Int? = null,
    val tvMazeId: Int? = null,
    val canonicalTitle: String,
    val year: Int? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "streaming_catalog_cache", primaryKeys = ["cacheKey"])
data class CatalogCacheEntity(
    val cacheKey: String,
    val provider: String,
    val page: Int,
    val mediaType: String,
    val payload: String,
    val storedAt: Long = System.currentTimeMillis()
)
