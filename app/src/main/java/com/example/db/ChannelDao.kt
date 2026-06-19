package com.example.db

import androidx.room.*
import com.example.model.Channel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY category ASC")
    fun getAllChannelsFlow(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE url = :url LIMIT 1")
    suspend fun getChannelByUrl(url: String): Channel?

    @Query("SELECT * FROM channels WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoritesFlow(): Flow<List<Channel>>

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE url = :url")
    suspend fun updateFavorite(url: String, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel)

    @Query("UPDATE channels SET name = :name, category = :category, logo = :logo WHERE url = :url")
    suspend fun updateChannelMetadata(url: String, name: String, category: String, logo: String?)

    @Query("DELETE FROM channels WHERE url NOT IN (:validUrls)")
    suspend fun deleteStaleChannels(validUrls: List<String>)

    @Transaction
    suspend fun syncChannels(channels: List<Channel>) {
        val validUrls = channels.map { it.url }
        deleteStaleChannels(validUrls)
        for (channel in channels) {
            val existing = getChannelByUrl(channel.url)
            if (existing == null) {
                insertChannel(channel)
            } else {
                updateChannelMetadata(channel.url, channel.name, channel.category, channel.logo)
            }
        }
    }
}
