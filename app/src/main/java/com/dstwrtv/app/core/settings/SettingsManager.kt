package com.dstwrtv.app.core.settings

import android.content.Context
import android.content.SharedPreferences
import com.dstwrtv.app.core.constants.AppConstants

class SettingsManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        AppConstants.PREFS_NAME, 
        Context.MODE_PRIVATE
    )

    var customM3uUrl: String?
        get() {
            val raw = prefs.getString(AppConstants.KEY_CUSTOM_M3U_URL, null) ?: return null
            if (raw.isBlank()) return null
            // Attempt to decrypt. If raw is unencrypted legacy string, decryptString will return null
            // in which case we fall back to raw (unencrypted cleartext) for 100% backwards compatibility.
            return com.dstwrtv.app.core.util.CryptoHelper.decryptString(raw) ?: raw
        }
        set(value) {
            prefs.edit().apply {
                if (value.isNullOrBlank()) {
                    remove(AppConstants.KEY_CUSTOM_M3U_URL)
                } else {
                    val encrypted = com.dstwrtv.app.core.util.CryptoHelper.encryptString(value.trim()) ?: value.trim()
                    putString(AppConstants.KEY_CUSTOM_M3U_URL, encrypted)
                }
            }.apply()
        }

    private val remoteConfigManager by lazy {
        (context.applicationContext as? com.dstwrtv.app.DstwrApplication)?.remoteConfigManager
    }

    var sourceMode: String
        get() {
            val hideUI = remoteConfigManager?.hideDeveloperUI ?: false
            if (hideUI) return "user_only"
            return prefs.getString(AppConstants.KEY_SOURCE_MODE, "merged") ?: "merged"
        }
        set(value) = prefs.edit().putString(AppConstants.KEY_SOURCE_MODE, value).apply()

    var showDevPackage: Boolean
        get() {
            val hideUI = remoteConfigManager?.hideDeveloperUI ?: false
            if (hideUI) return false
            return prefs.getBoolean(AppConstants.KEY_SHOW_DEV_PACKAGE, true)
        }
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_SHOW_DEV_PACKAGE, value).apply()

    var appTheme: String
        get() = prefs.getString(AppConstants.KEY_APP_THEME, "classic_dark") ?: "classic_dark"
        set(value) = prefs.edit().putString(AppConstants.KEY_APP_THEME, value).apply()
}
