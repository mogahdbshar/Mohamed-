package com.example.repository

import com.example.db.ChannelDao
import com.example.model.Channel
import com.example.util.CryptoHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale

class ChannelRepository(private val channelDao: ChannelDao) {

    val allChannels: Flow<List<Channel>> = channelDao.getAllChannelsFlow()
    val favoriteChannels: Flow<List<Channel>> = channelDao.getFavoritesFlow()

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, Channel::class.java)
    private val adapter = moshi.adapter<List<Channel>>(listType)

    // Robust list of NSFW/Adult content keywords to execute a literal and strict block
    private val blocklist = listOf(
        "سكس", "اباحي", "إباحي", "بورن", "شرمط", "قحبة", "مثير", "عرى", "عري",
        "adult", "porn", "xxx", "18+", "nsfw", "sex", "erotic", "nude"
    )

    private fun isFamilyFriendly(channel: Channel): Boolean {
        val nameLower = channel.name.lowercase(Locale.ROOT)
        val catLower = channel.category.lowercase(Locale.ROOT)
        
        // Return false (block) if any keyword is present
        for (keyword in blocklist) {
            if (nameLower.contains(keyword) || catLower.contains(keyword)) {
                return false
            }
        }
        return true
    }

    suspend fun toggleFavorite(url: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            channelDao.updateFavorite(url, isFavorite)
        }
    }

    suspend fun syncChannels(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://raw.githubusercontent.com/mogahdbshar/app-core-assets/refs/heads/main/system_config.dat")
                .header("User-Agent", "IPTVSmarters/1.0.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP error ${response.code}"))
                }
                
                val bodyString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val decryptedJson = CryptoHelper.decrypt(bodyString.trim()) ?: return@withContext Result.failure(Exception("Decryption error"))
                val rawList = adapter.fromJson(decryptedJson) ?: emptyList()

                // Execute ultra-strict family filtering matching user demand
                val filteredList = rawList.filter { isFamilyFriendly(it) }

                channelDao.syncChannels(filteredList)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
