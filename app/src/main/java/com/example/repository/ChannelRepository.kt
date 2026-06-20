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

    private fun isArabicChannel(name: String): Boolean {
        if (name.any { it in '\u0600'..'\u06FF' }) return true
        val nameLower = name.lowercase(Locale.ROOT)
        return nameLower.contains("ar:") || 
               nameLower.contains("ar|") || 
               nameLower.contains("ar_") || 
               nameLower.contains("ar ") || 
               nameLower.contains("ar-") ||
               nameLower.contains("arabic") || 
               nameLower.contains("arab") ||
               nameLower.contains("ara")
    }

    private fun isFrenchChannel(name: String): Boolean {
        val nameLower = name.lowercase(Locale.ROOT)
        return nameLower.contains("fr:") || 
               nameLower.contains("fr|") || 
               nameLower.contains("fr_") || 
               nameLower.contains("fr ") || 
               nameLower.contains("fr-") ||
               nameLower.contains("french") ||
               (nameLower.contains("fr") && !nameLower.contains("ar"))
    }

    private fun determinePackage(name: String): String {
        val nameUpper = name.uppercase(Locale.ROOT)
        val nameLower = name.lowercase(Locale.ROOT)
        
        return when {
            // High priority Arabic Sports
            nameUpper.contains("SSC") -> {
                if (isFrenchChannel(name)) {
                    "باقة القنوات العالمية"
                } else if (isArabicChannel(name) || nameUpper.contains("AR") || nameUpper.contains("ARA") || !nameUpper.contains("FR")) {
                    "باقة قنوات SSC الرياضية"
                } else {
                    "باقة القنوات العالمية"
                }
            }
            nameUpper.contains("BEIN") && (nameUpper.contains("SPORTS") || nameUpper.contains("SP") || nameUpper.contains("SPORT")) -> {
                if (isFrenchChannel(name)) {
                    "باقة القنوات العالمية"
                } else if (isArabicChannel(name) || nameUpper.contains("AR") || nameUpper.contains("ARA") || !nameUpper.contains("FR")) {
                    "باقة قنوات beIN Sports العربية"
                } else {
                    "باقة القنوات العالمية"
                }
            }
            nameUpper.contains("AD SPORT") || nameUpper.contains("AD_SPORT") || nameUpper.contains("AD SPORTS") -> "باقة قنوات أبوظبي الرياضية"
            nameLower.contains("alkass") || nameLower.contains("الكس") || nameLower.contains("الكأس") -> "باقة قنوات الكأس الرياضية"
            
            // VIP / Entertainment
            nameLower.contains("shahid") || nameLower.contains("sh") && nameLower.contains("vip") -> "باقة VIP شاهد"
            nameUpper.contains("OSN") -> "باقة قنوات OSN الترفيهية"
            nameUpper.contains("NETFLIX") || nameUpper.contains("AFLAM") -> "باقة أفلام ومسلسلات نتفليكس"
            
            // Arabic network packages
            nameUpper.contains("MBC") -> "باقة قنوات MBC الكاملة"
            nameLower.contains("rotana") || nameLower.contains("روتانا") -> "باقة قنوات روتانا"
            
            // Kids / News / Documentary
            nameLower.contains("spacetoon") || nameLower.contains("kids") || nameLower.contains("أطفال") || nameLower.contains("كرتون") -> "باقة قنوات الأطفال والكرتون"
            nameLower.contains("news") || nameLower.contains("أخبار") || nameLower.contains("الجزيرة") || nameLower.contains("jazeera") || nameLower.contains("العربية") || nameLower.contains("arabiya") -> "باقة الأخبار والبرامج السياسية"
            nameLower.contains("national") || nameLower.contains("nat geo") || nameLower.contains("وثائق") || nameLower.contains("doc") -> "باقة القنوات الوثائقية"
            
            // Generic Arabic Channels
            isArabicChannel(name) -> "باقة القنوات العربية العامة"
            
            // PPV Box / Events
            nameUpper.contains("PPV") || nameUpper.contains("BOXING") || nameUpper.contains("EVENT") -> "باقة الأحداث الرياضية والبوكسينج"
            
            // Others
            else -> "باقة القنوات العالمية الأخرى"
        }
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

                // Execute ultra-strict family filtering and map them to beautiful Arabic packages
                val processedList = rawList.filter { isFamilyFriendly(it) }
                    .map { channel ->
                        val cleanName = channel.name.trim()
                        channel.copy(
                            name = cleanName,
                            category = determinePackage(cleanName)
                        )
                    }

                channelDao.syncChannels(processedList)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
