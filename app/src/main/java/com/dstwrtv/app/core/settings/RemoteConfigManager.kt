package com.dstwrtv.app.core.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dstwrtv.app.core.constants.AppConstants
import com.dstwrtv.app.core.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RemoteConfigManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        AppConstants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // Helper functions for reading/writing prefs safely
    private fun getStr(key: String, default: String): String = prefs.getString(key, default) ?: default
    private fun putStr(key: String, value: String) = prefs.edit().putString(key, value).apply()
    private fun getBool(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    private fun putBool(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    private fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    private fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()

    // 1. Developer channels and UI Toggle (as previously implemented)
    var enableDeveloperChannels: Boolean
        get() = getBool(AppConstants.KEY_ENABLE_DEV_CHANNELS, true)
        private set(value) = putBool(AppConstants.KEY_ENABLE_DEV_CHANNELS, value)

    var hideDeveloperUI: Boolean
        get() = getBool(AppConstants.KEY_HIDE_DEV_UI, false)
        private set(value) = putBool(AppConstants.KEY_HIDE_DEV_UI, value)

    var remoteAnnouncement: String
        get() = getStr(AppConstants.KEY_REMOTE_ANNOUNCEMENT, "")
        private set(value) = putStr(AppConstants.KEY_REMOTE_ANNOUNCEMENT, value)

    var remoteM3uUrl: String
        get() = getStr(AppConstants.KEY_REMOTE_M3U_URL, "")
        private set(value) = putStr(AppConstants.KEY_REMOTE_M3U_URL, value)

    // 2. App Status & Maintenance Overlays
    var appStatus: String
        get() = getStr("remote_app_status", "active")
        private set(value) = putStr("remote_app_status", value)

    var maintenanceTitle: String
        get() = getStr("remote_maintenance_title", "تحت الصيانة")
        private set(value) = putStr("remote_maintenance_title", value)

    var maintenanceMessage: String
        get() = getStr("remote_maintenance_message", "التطبيق حالياً في صيانة دورية مبرمجة لتقديم أفضل جودة وسيرفرات أسرع. نعتذر عن الإزعاج ونعدكم بالعودة قريباً جداً!")
        private set(value) = putStr("remote_maintenance_message", value)

    var suspendedTitle: String
        get() = getStr("remote_suspended_title", "تنبيه إيقاف الخدمة")
        private set(value) = putStr("remote_suspended_title", value)

    var suspendedMessage: String
        get() = getStr("remote_suspended_message", "تم إيقاف هذا الإصدار بشكل نهائي أو مؤقت من قبل الإدارة. يرجى مراجعة الدعم الفني للاستفسار وتحديث التطبيق.")
        private set(value) = putStr("remote_suspended_message", value)

    // 3. Force Update Settings
    var minAppVersion: Int
        get() = getInt("remote_min_app_version", 1)
        private set(value) = putInt("remote_min_app_version", value)

    var latestAppVersion: Int
        get() = getInt("remote_latest_app_version", 1)
        private set(value) = putInt("remote_latest_app_version", value)

    var updateUrl: String
        get() = getStr("remote_update_url", "https://t.me/your_telegram_channel")
        private set(value) = putStr("remote_update_url", value)

    var updateMessage: String
        get() = getStr("remote_update_message", "يتوفر إصدار جديد يحتوي على قنوات إضافية وإصلاح للمشاكل الحالية. يرجى التحديث فوراً لتجنب انقطاع البث!")
        private set(value) = putStr("remote_update_message", value)

    // 4. Advertising & Codes Control
    var enableAds: Boolean
        get() = getBool("remote_enable_ads", false)
        private set(value) = putBool("remote_enable_ads", value)

    var adProvider: String
        get() = getStr("remote_ad_provider", "none")
        private set(value) = putStr("remote_ad_provider", value)

    var adBannerId: String
        get() = getStr("remote_ad_banner_id", "")
        private set(value) = putStr("remote_ad_banner_id", value)

    var adInterstitialId: String
        get() = getStr("remote_ad_interstitial_id", "")
        private set(value) = putStr("remote_ad_interstitial_id", value)

    var customAdImageUrl: String
        get() = getStr("remote_custom_ad_image_url", "")
        private set(value) = putStr("remote_custom_ad_image_url", value)

    var customAdClickUrl: String
        get() = getStr("remote_custom_ad_click_url", "")
        private set(value) = putStr("remote_custom_ad_click_url", value)

    var customAdDisplayLocation: String
        get() = getStr("remote_custom_ad_display_location", "none")
        private set(value) = putStr("remote_custom_ad_display_location", value)

    // 5. Rich Announcement alerts
    var announcementShow: Boolean
        get() = getBool("remote_announcement_show", false)
        private set(value) = putBool("remote_announcement_show", value)

    var announcementTitle: String
        get() = getStr("remote_announcement_title", "تنبيه هام")
        private set(value) = putStr("remote_announcement_title", value)

    var announcementMessage: String
        get() = getStr("remote_announcement_message", "")
        private set(value) = putStr("remote_announcement_message", value)

    var announcementType: String
        get() = getStr("remote_announcement_type", "banner")
        private set(value) = putStr("remote_announcement_type", value)

    var announcementSkippable: Boolean
        get() = getBool("remote_announcement_skippable", true)
        private set(value) = putBool("remote_announcement_skippable", value)

    // 6. Security and Stream Protection
    var enableCrypto: Boolean
        get() = getBool("remote_enable_crypto", true)
        private set(value) = putBool("remote_enable_crypto", value)

    var cryptoAlgorithm: String
        get() = getStr("remote_crypto_algorithm", "AES")
        private set(value) = putStr("remote_crypto_algorithm", value)

    var userAgentOverride: String
        get() = getStr("remote_user_agent_override", "")
        private set(value) = putStr("remote_user_agent_override", value)

    var refererOverride: String
        get() = getStr("remote_referer_override", "")
        private set(value) = putStr("remote_referer_override", value)

    var allowedCountries: String
        get() = getStr("remote_allowed_countries", "ALL")
        private set(value) = putStr("remote_allowed_countries", value)

    // 7. Dynamic Aesthetics & Design Override
    var appAccentColor: String
        get() = getStr("remote_app_accent_color", "")
        private set(value) = putStr("remote_app_accent_color", value)

    var supportButtonUrl: String
        get() = getStr("remote_support_button_url", "https://t.me/your_telegram_channel")
        private set(value) = putStr("remote_support_button_url", value)

    var supportButtonVisible: Boolean
        get() = getBool("remote_support_button_visible", true)
        private set(value) = putBool("remote_support_button_visible", value)

    var developerCategoryName: String
        get() = getStr("remote_developer_category_name", "باقات المطور")
        private set(value) = putStr("remote_developer_category_name", value)

    var showOnboardingAlways: Boolean
        get() = getBool("remote_show_onboarding_always", false)
        private set(value) = putBool("remote_show_onboarding_always", value)

    // 8. Channel and UI Visibility Rules
    var hiddenCategories: String
        get() = getStr("remote_hidden_categories", "")
        private set(value) = putStr("remote_hidden_categories", value)

    var hiddenChannels: String
        get() = getStr("remote_hidden_channels", "")
        private set(value) = putStr("remote_hidden_channels", value)

    var hideAllChannels: Boolean
        get() = getBool("remote_hide_all_channels", false)
        private set(value) = putBool("remote_hide_all_channels", value)

    var hiddenTabs: String
        get() = getStr("remote_hidden_tabs", "")
        private set(value) = putStr("remote_hidden_tabs", value)

    // 9. Telemetry & User Analytics
    var telemetryUrl: String
        get() = getStr("remote_telemetry_url", "")
        private set(value) = putStr("remote_telemetry_url", value)

    suspend fun fetchConfig(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = NetworkClient.newRequest(AppConstants.REMOTE_CONTROL_URL)
            NetworkClient.okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w("RemoteConfig", "Failed to fetch remote config: ${response.code}")
                    return@withContext false
                }
                val body = response.body?.string() ?: return@withContext false
                if (body.isNotBlank()) {
                    val json = JSONObject(body)
                    
                    // Parse primary parameters with defaults fallback
                    enableDeveloperChannels = json.optBoolean("enable_developer_channels", true)
                    hideDeveloperUI = json.optBoolean("hide_all_developer_options", false)
                    remoteAnnouncement = json.optString("remote_announcement", "")
                    remoteM3uUrl = json.optString("remote_m3u_url", "")

                    // App Status & Maintenance Overlays
                    appStatus = json.optString("app_status", "active")
                    maintenanceTitle = json.optString("maintenance_title", "تحت الصيانة")
                    maintenanceMessage = json.optString("maintenance_message", "التطبيق حالياً في صيانة دورية مبرمجة لتقديم أفضل جودة وسيرفرات أسرع. نعتذر عن الإزعاج ونعدكم بالعودة قريباً جداً!")
                    suspendedTitle = json.optString("suspended_title", "تنبيه إيقاف الخدمة")
                    suspendedMessage = json.optString("suspended_message", "تم إيقاف هذا الإصدار بشكل نهائي أو مؤقت من قبل الإدارة. يرجى مراجعة الدعم الفني للاستفسار وتحديث التطبيق.")

                    // Force Update Settings
                    minAppVersion = json.optInt("min_app_version", 1)
                    latestAppVersion = json.optInt("latest_app_version", 1)
                    updateUrl = json.optString("update_url", "https://t.me/your_telegram_channel")
                    updateMessage = json.optString("update_message", "يتوفر إصدار جديد يحتوي على قنوات إضافية وإصلاح للمشاكل الحالية. يرجى التحديث فوراً لتجنب انقطاع البث!")

                    // Advertising & Codes Control
                    enableAds = json.optBoolean("enable_ads", false)
                    adProvider = json.optString("ad_provider", "none")
                    adBannerId = json.optString("ad_banner_id", "")
                    adInterstitialId = json.optString("ad_interstitial_id", "")
                    customAdImageUrl = json.optString("custom_ad_image_url", "")
                    customAdClickUrl = json.optString("custom_ad_click_url", "")
                    customAdDisplayLocation = json.optString("custom_ad_display_location", "none")

                    // Rich Announcement alerts
                    announcementShow = json.optBoolean("announcement_show", false)
                    announcementTitle = json.optString("announcement_title", "تنبيه هام")
                    announcementMessage = json.optString("announcement_message", "")
                    announcementType = json.optString("announcement_type", "banner")
                    announcementSkippable = json.optBoolean("announcement_skippable", true)

                    // Security and Stream Protection
                    enableCrypto = json.optBoolean("enable_crypto", true)
                    cryptoAlgorithm = json.optString("crypto_algorithm", "AES")
                    userAgentOverride = json.optString("user_agent_override", "")
                    refererOverride = json.optString("referer_override", "")
                    allowedCountries = json.optString("allowed_countries", "ALL")

                    // Dynamic Aesthetics & Design Override
                    appAccentColor = json.optString("app_accent_color", "")
                    supportButtonUrl = json.optString("support_button_url", "https://t.me/your_telegram_channel")
                    supportButtonVisible = json.optBoolean("support_button_visible", true)
                    developerCategoryName = json.optString("developer_category_name", "باقات المطور")
                    showOnboardingAlways = json.optBoolean("show_onboarding_always", false)

                    // Channel and UI Visibility Rules
                    hiddenCategories = json.optString("hidden_categories", "")
                    hiddenChannels = json.optString("hidden_channels", "")
                    hideAllChannels = json.optBoolean("hide_all_channels", false)
                    hiddenTabs = json.optString("hidden_tabs", "")

                    // Telemetry & User Analytics
                    telemetryUrl = json.optString("telemetry_url", "")

                    Log.d("RemoteConfig", "Remote configuration updated successfully with all parameters!")
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Log.e("RemoteConfig", "Error fetching remote config", e)
        }
        return@withContext false
    }
}
