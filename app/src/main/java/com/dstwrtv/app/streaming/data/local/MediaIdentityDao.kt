package com.dstwrtv.app.streaming.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaIdentityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MediaIdentityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MediaIdentityEntity>)

    @Query("SELECT * FROM streaming_media_identity WHERE mediaType = :mediaType AND provider = :provider AND providerId = :providerId LIMIT 1")
    suspend fun get(mediaType: String, provider: String, providerId: String): MediaIdentityEntity?

    @Query("SELECT * FROM streaming_media_identity WHERE imdbId = :imdbId LIMIT 1")
    suspend fun getByImdbId(imdbId: String): MediaIdentityEntity?
}

@Dao
interface CatalogCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CatalogCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CatalogCacheEntity>)

    @Query("SELECT * FROM streaming_catalog_cache WHERE mediaType = :mediaType AND provider = :provider AND providerId = :providerId LIMIT 1")
    suspend fun get(mediaType: String, provider: String, providerId: String): CatalogCacheEntity?

    @Query("SELECT * FROM streaming_catalog_cache WHERE mediaType = :mediaType AND expiresAt > :now ORDER BY rating DESC, voteCount DESC LIMIT :limit")
    suspend fun getFresh(mediaType: String, now: Long, limit: Int): List<CatalogCacheEntity>

    @Query("SELECT * FROM streaming_catalog_cache WHERE mediaType = :mediaType ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun getStale(mediaType: String, limit: Int): List<CatalogCacheEntity>

    @Query("DELETE FROM streaming_catalog_cache WHERE expiresAt < :cutoff")
    suspend fun deleteExpired(cutoff: Long): Int
}
