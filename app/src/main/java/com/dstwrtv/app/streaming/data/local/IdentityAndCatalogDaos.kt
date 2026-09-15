package com.dstwrtv.app.streaming.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MediaIdentityDao {
    @Query("SELECT * FROM streaming_media_identity WHERE mediaType = :mediaType AND provider = :provider AND providerId = :providerId LIMIT 1")
    suspend fun get(mediaType: String, provider: String, providerId: String): MediaIdentityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MediaIdentityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MediaIdentityEntity>)
}

@Dao
interface CatalogCacheDao {
    @Query("SELECT * FROM streaming_catalog_cache WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun get(cacheKey: String): CatalogCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CatalogCacheEntity)

    @Query("DELETE FROM streaming_catalog_cache WHERE storedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}
