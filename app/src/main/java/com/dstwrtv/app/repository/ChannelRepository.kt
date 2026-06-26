package com.dstwrtv.app.repository

import com.dstwrtv.app.core.cache.AppCache
import com.dstwrtv.app.core.network.NetworkClient
import com.dstwrtv.app.core.settings.SettingsManager
import com.dstwrtv.app.db.ChannelDao
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.core.util.CryptoHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.Locale

class ChannelRepository(
    private val channelDao: ChannelDao,
    private val settingsManager: SettingsManager
) {

    val allChannels: Flow<List<Channel>> = channelDao.getAllChannelsFlow()
    val favoriteChannels: Flow<List<Channel>> = channelDao.getFavoritesFlow()

    suspend fun getChannelsCount(): Int {
        return withContext(Dispatchers.IO) {
            channelDao.getChannelsCount()
        }
    }

    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, Channel::class.java)
    private val adapter = moshi.adapter<List<Channel>>(listType)

    private val parser = com.dstwrtv.app.core.util.M3UParser()

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

    private fun isArabicChannel(name: String, logo: String? = null): Boolean {
        if (name.any { it in '\u0600'..'\u06FF' }) return true
        val nameLower = name.lowercase(Locale.ROOT)
        val nameUpper = name.uppercase(Locale.ROOT)
        
        val logoLower = logo?.lowercase(Locale.ROOT) ?: ""
        
        val hasArInLogo = logoLower.contains("/ar/") || 
                          logoLower.contains("/arabic/") || 
                          logoLower.contains("/arab/") || 
                          logoLower.contains("_ar.") || 
                          logoLower.contains("-ar.") || 
                          logoLower.contains("/ar_") ||
                          logoLower.endsWith("ar.png") ||
                          logoLower.endsWith("ar_logo.png")

        val hasArInName = nameUpper.startsWith("AR") || 
                          nameUpper.startsWith("Ar") || 
                          nameLower.contains("ar:") || 
                          nameLower.contains("ar|") || 
                          nameLower.contains("ar_") || 
                          nameLower.contains("ar ") || 
                          nameLower.contains("ar-") ||
                          nameLower.contains("arabic") || 
                          nameLower.contains("arab") ||
                          nameLower.contains("ara")

        return hasArInName || hasArInLogo
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

    private fun determinePackage(name: String, logo: String?): String {
        val nameUpper = name.uppercase(Locale.ROOT)
        val nameLower = name.lowercase(Locale.ROOT)
        
        val category = when {
            // High priority Arabic Sports
            nameUpper.contains("SSC") -> {
                if (isFrenchChannel(name)) {
                    "القنوات العالمية"
                } else if (isArabicChannel(name, logo) || nameUpper.contains("AR") || nameUpper.contains("ARA") || !nameUpper.contains("FR")) {
                    "قنوات SSC الرياضية"
                } else {
                    "القنوات العالمية"
                }
            }
            nameUpper.contains("BEIN") && (nameUpper.contains("SPORTS") || nameUpper.contains("SP") || nameUpper.contains("SPORT")) -> {
                if (isFrenchChannel(name)) {
                    "القنوات العالمية"
                } else if (isArabicChannel(name, logo) || nameUpper.contains("AR") || nameUpper.contains("ARA") || !nameUpper.contains("FR")) {
                    "قنوات beIN Sports العربية"
                } else {
                    "القنوات العالمية"
                }
            }
            nameUpper.contains("AD SPORT") || nameUpper.contains("AD_SPORT") || nameUpper.contains("AD SPORTS") -> "قنوات أبوظبي الرياضية"
            nameLower.contains("alkass") || nameLower.contains("الكس") || nameLower.contains("الكأس") -> "قنوات الكأس الرياضية"
            
            // VIP / Entertainment
            nameLower.contains("shahid") || nameLower.contains("sh") && nameLower.contains("vip") -> "قنوات شاهد"
            nameUpper.contains("OSN") -> "قنوات OSN الترفيهية"
            nameUpper.contains("NETFLIX") || nameUpper.contains("AFLAM") -> "أفلام ومسلسلات مختارة"
            
            // Arabic network packages
            nameUpper.contains("MBC") -> "قنوات MBC"
            nameLower.contains("rotana") || nameLower.contains("روتانا") -> "قنوات روتانا"
            
            // Kids / News / Documentary
            nameLower.contains("spacetoon") || nameLower.contains("kids") || nameLower.contains("أطفال") || nameLower.contains("كرتون") -> "قنوات الأطفال والكرتون"
            nameLower.contains("news") || nameLower.contains("أخبار") || nameLower.contains("الجزيرة") || nameLower.contains("jazeera") || nameLower.contains("العربية") || nameLower.contains("arabiya") -> "قنوات الأخبار والسياسة"
            nameLower.contains("national") || nameLower.contains("nat geo") || nameLower.contains("وثائق") || nameLower.contains("doc") -> "القنوات الوثائقية"
            
            // Generic Arabic Channels
            isArabicChannel(name, logo) -> "القنوات العربية العامة"
            
            // PPV Box / Events
            nameUpper.contains("PPV") || nameUpper.contains("BOXING") || nameUpper.contains("EVENT") -> "الأحداث الرياضية المباشرة"
            
            // Others
            else -> "القنوات العالمية"
        }
        return parser.cleanCategory(category)
    }

    suspend fun toggleFavorite(url: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            channelDao.updateFavorite(url, isFavorite)
        }
    }

    private val fallbackChannels = listOf(
        Channel(name = "الجزيرة الإخبارية", url = "https://live-hls-web-aja.getaj.net/AJA/index.m3u8", logo = "", category = "قنوات الأخبار والسياسة"),
        Channel(name = "العربية الإخبارية", url = "https://alarabiya-f.akamaihd.net/i/alarabiya_1@306260/master.m3u8", logo = "", category = "قنوات الأخبار والسياسة"),
        Channel(name = "روتانا سينما", url = "https://rotanacinema.msq.net.sa/rotanacinema/index.m3u8", logo = "", category = "قنوات روتانا"),
        Channel(name = "سبيستون", url = "https://spacetoon.m3u8.com/stream/index.m3u8", logo = "", category = "قنوات الأطفال والكرتون")
    )

    private suspend fun fetchFallbackM3u(): List<Channel> = withContext(Dispatchers.IO) {
        val urls = listOf(
            "https://iptv-org.github.io/iptv/languages/ara.m3u"
        )
        val list = mutableListOf<Channel>()
        for (url in urls) {
            try {
                val body = fetchUrlWithRedirects(url)
                if (body.isNotBlank()) {
                    list.addAll(parser.parse(body, "القنوات العربية العامة"))
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        list
    }

    private suspend fun fetchUrlWithRedirects(targetUrl: String, bypassCache: Boolean = false): String = withContext(Dispatchers.IO) {
        if (targetUrl.isBlank()) return@withContext ""
        
        // Read memory cache
        if (!bypassCache) {
            val cachedData = AppCache.get(targetUrl)
            if (cachedData != null) {
                return@withContext cachedData
            }
        }

        var currentUrl = targetUrl
        var redirects = 0
        val maxRedirects = 5
        var resultBody = ""
        
        while (redirects < maxRedirects) {
            val request = NetworkClient.newRequest(currentUrl)
                
            NetworkClient.okHttpClient.newCall(request).execute().use { response ->
                if (response.isRedirect || response.code in 300..399) {
                    val location = response.header("Location")
                    if (!location.isNullOrBlank()) {
                        currentUrl = if (location.startsWith("http", ignoreCase = true)) {
                            location
                        } else {
                            val baseUri = java.net.URI(currentUrl)
                            baseUri.resolve(location).toString()
                        }
                        redirects++
                        return@use 
                    }
                }
                
                if (!response.isSuccessful) {
                    throw Exception("IPTV Server error: ${response.code}")
                }
                resultBody = response.body?.string() ?: ""
                redirects = maxRedirects 
            }
        }
        
        // Write to memory cache
        if (resultBody.isNotBlank()) {
            AppCache.put(targetUrl, resultBody)
        }
        resultBody
    }

    suspend fun syncChannels(passedCustomUrl: String? = null): Result<Int> = withContext(Dispatchers.IO) {
        try {
            if (passedCustomUrl != null) {
                val candidate = passedCustomUrl.trim()
                settingsManager.customM3uUrl = candidate
            }
            
            val activeCustomUrl = settingsManager.customM3uUrl
            
            // 2. Fetch System Configuration
            var systemChannels = emptyList<Channel>()
            try {
                val officialUrl = "https://raw.githubusercontent.com/mogahdbshar/app-core-assets/refs/heads/main/system_config.dat"
                val bodyString = fetchUrlWithRedirects(officialUrl)
                if (bodyString.isNotBlank()) {
                    val decryptedJson = CryptoHelper.decrypt(bodyString.trim()) ?: throw Exception("decryption failed")
                    val rawList = adapter.fromJson(decryptedJson) ?: emptyList()
                    
                    systemChannels = rawList.filter { isFamilyFriendly(it) }
                        .map { channel ->
                            val cleanName = channel.name.trim()
                            channel.copy(
                                name = cleanName,
                                category = determinePackage(cleanName, channel.logo)
                            )
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                systemChannels = fallbackChannels
            }

            // 3. Fetch Custom Playlist
            val customChannels = mutableListOf<Channel>()
            if (!activeCustomUrl.isNullOrBlank()) {
                try {
                    val body = fetchUrlWithRedirects(activeCustomUrl)
                    if (body.isNotBlank()) {
                        val parsed = parser.parse(body, "القنوات المضافة")
                        customChannels.addAll(parsed.filter { isFamilyFriendly(it) }.map {
                            it.copy(category = parser.cleanCategory(it.category))
                        })
                    }
                } catch (e: Exception) {
                    if (passedCustomUrl != null) throw e
                }
            }

            // 4. Merge and Sync
            val sourceMode = settingsManager.sourceMode
            val showDev = settingsManager.showDevPackage
            
            val finalSystem = if ((sourceMode == "merged" || sourceMode == "dev_only") && showDev) systemChannels else emptyList()
            val finalCustom = if (sourceMode == "merged" || sourceMode == "user_only") customChannels else emptyList()

            val mergedList = (finalSystem + finalCustom).distinctBy { it.url }
            channelDao.syncChannels(mergedList)
            
            Result.success(customChannels.size)
        } catch (e: Exception) {
            e.printStackTrace()
            val arabicError = when {
                e is java.net.SocketTimeoutException -> "عذراً، انتظر طويلاً للسيرفر. تحقق من سرعة الإنترنت."
                e is java.net.UnknownHostException -> "لا يوجد اتصال بالإنترنت حالياً."
                else -> e.localizedMessage ?: "حدث خطأ أثناء مزامنة البيانات."
            }
            
            if (channelDao.getChannelsCount() == 0) {
                channelDao.syncChannels(fallbackChannels)
                Result.failure(Exception("$arabicError (تم تحميل قنوات الطوارئ)"))
            } else {
                Result.failure(Exception("$arabicError (عرض البيانات المخزنة)"))
            }
        }
    }

}
