package com.example.repository

import android.content.Context
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
import java.util.concurrent.TimeUnit

class ChannelRepository(private val channelDao: ChannelDao) {

    val allChannels: Flow<List<Channel>> = channelDao.getAllChannelsFlow()
    val favoriteChannels: Flow<List<Channel>> = channelDao.getFavoritesFlow()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
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
        
        return when {
            // High priority Arabic Sports
            nameUpper.contains("SSC") -> {
                if (isFrenchChannel(name)) {
                    "باقة القنوات العالمية"
                } else if (isArabicChannel(name, logo) || nameUpper.contains("AR") || nameUpper.contains("ARA") || !nameUpper.contains("FR")) {
                    "باقة قنوات SSC الرياضية"
                } else {
                    "باقة القنوات العالمية"
                }
            }
            nameUpper.contains("BEIN") && (nameUpper.contains("SPORTS") || nameUpper.contains("SP") || nameUpper.contains("SPORT")) -> {
                if (isFrenchChannel(name)) {
                    "باقة القنوات العالمية"
                } else if (isArabicChannel(name, logo) || nameUpper.contains("AR") || nameUpper.contains("ARA") || !nameUpper.contains("FR")) {
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
            isArabicChannel(name, logo) -> "باقة القنوات العربية العامة"
            
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

    private val fallbackChannels = listOf(
        Channel(name = "الجزيرة الإخبارية", url = "https://live-hls-web-aja.getaj.net/AJA/index.m3u8", logo = "", category = "باقة الأخبار والبرامج السياسية"),
        Channel(name = "العربية الإخبارية", url = "https://alarabiya-f.akamaihd.net/i/alarabiya_1@306260/master.m3u8", logo = "", category = "باقة الأخبار والبرامج السياسية"),
        Channel(name = "التلفزيون الكويتي", url = "https://media.blulive.me/kuwait/1/index.m3u8", logo = "", category = "باقة القنوات العربية العامة"),
        Channel(name = "السعودية الأولى", url = "https://webtv-kacnd.sba.net.sa/saudi1/index.m3u8", logo = "", category = "باقة القنوات العربية العامة"),
        Channel(name = "روتانا سينما", url = "https://rotanacinema.msq.net.sa/rotanacinema/index.m3u8", logo = "", category = "باقة قنوات روتانا"),
        Channel(name = "سبيستون", url = "https://spacetoon.m3u8.com/stream/index.m3u8", logo = "", category = "باقة قنوات الأطفال والكرتون")
    )

    private suspend fun fetchFallbackM3u(): List<Channel> = withContext(Dispatchers.IO) {
        val urls = listOf(
            "https://iptv-org.github.io/iptv/languages/ara.m3u",
            "https://iptv-org.github.io/iptv/categories/sports.m3u"
        )
        val allChannels = mutableListOf<Channel>()
        
        for (url in urls) {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: return@use
                        val lines = body.split("\n")
                        var currentName = ""
                        var currentLogo = ""
                        var currentGroupName = "باقة القنوات العامة"
                        for (line in lines) {
                            if (line.startsWith("#EXTINF:")) {
                                val nameMatch = Regex(",(.*)").find(line)
                                currentName = nameMatch?.groupValues?.get(1)?.trim() ?: "قناة غير معروفة"
                                
                                val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(line)
                                currentLogo = logoMatch?.groupValues?.get(1) ?: ""
                                
                                val groupMatch = Regex("group-title=\"([^\"]+)\"").find(line)
                                currentGroupName = groupMatch?.groupValues?.get(1) ?: "باقة القنوات العامة"
                            } else if (line.startsWith("http")) {
                                allChannels.add(
                                    Channel(
                                        name = currentName,
                                        url = line.trim(),
                                        logo = currentLogo,
                                        category = currentGroupName
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        allChannels
    }

    suspend fun syncChannels(context: Context, passedCustomUrl: String? = null): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val sharedPrefs = context.getSharedPreferences("dstwr_prefs", Context.MODE_PRIVATE)
            
            // 1. If user explicitly updated the url via UI, save or clear it
            if (passedCustomUrl != null) {
                if (passedCustomUrl.trim().isBlank()) {
                    sharedPrefs.edit().remove("custom_m3u_url").apply()
                } else {
                    sharedPrefs.edit().putString("custom_m3u_url", passedCustomUrl.trim()).apply()
                }
            }
            
            val activeCustomUrl = sharedPrefs.getString("custom_m3u_url", null)
            
            // 2. Fetch and decode official system curated list (decrypt default channels)
            val request = Request.Builder()
                .url("https://raw.githubusercontent.com/mogahdbshar/app-core-assets/refs/heads/main/system_config.dat")
                .header("User-Agent", "IPTVSmarters/1.0.0")
                .build()

            val systemChannels = client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("System source connection error: ${response.code}")
                }
                val bodyString = response.body?.string() ?: throw Exception("System configurations are empty")
                val decryptedJson = CryptoHelper.decrypt(bodyString.trim()) ?: throw Exception("System configuration decryption failed")
                val rawList = adapter.fromJson(decryptedJson) ?: emptyList()
                
                rawList.filter { isFamilyFriendly(it) }
                    .map { channel ->
                        val cleanName = channel.name.trim()
                        channel.copy(
                            name = cleanName,
                            category = determinePackage(cleanName, channel.logo)
                        )
                    }
            }

            // 3. Fetch and parse custom M3U/M3U8 playlist from activeCustomUrl if active
            val customChannels = mutableListOf<Channel>()
            if (!activeCustomUrl.isNullOrBlank()) {
                try {
                    val customRequest = Request.Builder()
                        .url(activeCustomUrl)
                        .header("User-Agent", "IPTVSmarters/1.0.0")
                        .build()
                        
                    client.newCall(customRequest).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw Exception("Custom M3U connection error: ${response.code}")
                        }
                        val body = response.body?.string() ?: ""
                        if (body.isNotBlank()) {
                            val lines = body.split("\n")
                            var currentName = ""
                            var currentLogo = ""
                            var currentGroup = "باقة القنوات المضافة"
                            for (line in lines) {
                                val trimmedLine = line.trim()
                                if (trimmedLine.startsWith("#EXTINF:")) {
                                    val nameMatch = Regex(",(.*)").find(trimmedLine)
                                    currentName = nameMatch?.groupValues?.get(1)?.trim() ?: "قناة مخصصة"
                                    
                                    val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(trimmedLine)
                                    currentLogo = logoMatch?.groupValues?.get(1) ?: ""

                                    val groupMatch = Regex("group-title=\"([^\"]+)\"").find(trimmedLine)
                                    currentGroup = groupMatch?.groupValues?.get(1) ?: "باقة القنوات المضافة"
                                } else if (trimmedLine.startsWith("http")) {
                                    val ch = Channel(
                                        name = currentName,
                                        url = trimmedLine,
                                        logo = currentLogo,
                                        category = currentGroup
                                    )
                                    if (isFamilyFriendly(ch)) {
                                        customChannels.add(ch)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Re-throw so user gets the accurate error message on button click
                    if (passedCustomUrl != null) {
                        throw Exception("فشل في قراءة الرابط المخصص: ${e.localizedMessage}")
                    }
                    e.printStackTrace()
                }
            }

            // 4. Source Filter Layer (Multi Source IPTV System)
            val sourceMode = sharedPrefs.getString("source_mode", "merged") ?: "merged"
            val showDevPackage = sharedPrefs.getBoolean("show_dev_package", true)
            
            val finalSystemChannels = if ((sourceMode == "merged" || sourceMode == "dev_only") && showDevPackage) {
                systemChannels
            } else {
                emptyList()
            }
            
            val finalCustomChannels = if (sourceMode == "merged" || sourceMode == "user_only") {
                customChannels
            } else {
                emptyList()
            }

            val mergedList = (finalSystemChannels + finalCustomChannels).distinctBy { it.url }
            
            channelDao.syncChannels(mergedList)
            
            if (mergedList.isNotEmpty()) {
                Result.success(customChannels.size)
            } else {
                Result.failure(Exception("لا توجد قنوات صالحة للبث في الوضع الحالي"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            
            val arabicErrorMsg = when {
                e is java.net.SocketTimeoutException -> "عذراً، السيرفر لا يستجيب في الوقت الحالي. تحقق من سرعة الإنترنت."
                e is java.net.UnknownHostException -> "لا يوجد اتصال بالإنترنت، تأكد من اتصالك بالشبكة."
                e.message?.contains("401") == true || e.message?.contains("403") == true -> "انتهت صلاحية اشتراكك، أو البيانات المدخلة غير صحيحة."
                e.message?.contains("404") == true -> "الرابط المدخل غير موجود أو السيرفر متوقف."
                e.message?.contains("decryption failed") == true -> "فشل في قراءة بيانات النظام الأساسية."
                else -> e.localizedMessage ?: "حدث خطأ غير معروف."
            }
            
            // If completely empty DB, load local system fallbacks
            val currentCount = channelDao.getChannelsCount()
            if (currentCount == 0) {
                val sharedPrefs = context.getSharedPreferences("dstwr_prefs", Context.MODE_PRIVATE)
                val sourceMode = sharedPrefs.getString("source_mode", "merged") ?: "merged"
                val showDevPackage = sharedPrefs.getBoolean("show_dev_package", true)
                
                val fallbackList = fetchFallbackM3u()
                val finalFallbackList = if ((sourceMode == "merged" || sourceMode == "dev_only") && showDevPackage) fallbackList else emptyList()
                
                if (finalFallbackList.isNotEmpty()) {
                    channelDao.syncChannels(finalFallbackList)
                } else if ((sourceMode == "merged" || sourceMode == "dev_only") && showDevPackage) {
                    channelDao.syncChannels(fallbackChannels)
                }
                Result.failure(Exception("$arabicErrorMsg (جاري عرض القنوات المحفوظة مسبقاً)"))
            } else {
                Result.failure(Exception("$arabicErrorMsg (القنوات المحفوظة لا تزال متوفرة)"))
            }
        }
    }
}
