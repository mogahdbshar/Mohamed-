package com.dstwrtv.app.core.constants

object AppConstants {
    const val PREFS_NAME = "dstwr_prefs"
    const val KEY_CUSTOM_M3U_URL = "custom_m3u_url"
    const val KEY_SOURCE_MODE = "source_mode"
    const val KEY_SHOW_DEV_PACKAGE = "show_dev_package"
    const val KEY_APP_THEME = "app_theme"

    // Default configuration URL
    const val OFFICIAL_CONFIG_URL = "https://raw.githubusercontent.com/mogahdbshar/app-core-assets/refs/heads/main/system_config.dat"
    const val FALLBACK_M3U_URL = "https://iptv-org.github.io/iptv/languages/ara.m3u"
    
    // Remote Control Configuration
    const val REMOTE_CONTROL_URL = "https://mohamed-1-be24.onrender.com/config"
    const val KEY_ENABLE_DEV_CHANNELS = "remote_enable_dev_channels"
    const val KEY_HIDE_DEV_UI = "remote_hide_dev_ui"
    const val KEY_REMOTE_ANNOUNCEMENT = "remote_announcement"
    const val KEY_REMOTE_M3U_URL = "remote_m3u_url"

    // Cache limits
    const val DEFAULT_CACHE_MAX_AGE_MS = 10 * 60 * 1000L // 10 minutes
}
