package com.dstwrtv.app.db

import androidx.room.*
import com.dstwrtv.app.model.Channel
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

    @Query("SELECT url FROM channels WHERE isFavorite = 1")
    suspend fun getFavoriteUrls(): List<String>

    @Query("DELETE FROM channels")
    suspend fun deleteAllChannels()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannelsList(channels: List<Channel>)

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getChannelsCount(): Int

    @Transaction
    suspend fun syncChannels(channels: List<Channel>) {
        val favoriteUrls = getFavoriteUrls().toSet()
        deleteAllChannels()
        
        val preparedChannels = channels.map { channel ->
            if (favoriteUrls.contains(channel.url)) {
                channel.copy(isFavorite = true)
            } else {
                channel
            }
        }
        
        val chunkSize = 150
        preparedChannels.chunked(chunkSize).forEach { chunk ->
            insertChannelsList(chunk)
        }
    }
}
