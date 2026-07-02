package com.dstwrtv.app.core.telemetry

import android.content.Context
import android.os.Build
import android.util.Log
import com.dstwrtv.app.core.network.NetworkClient
import com.dstwrtv.app.core.settings.RemoteConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID

class TelemetryReporter(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("dstwr_telemetry_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)

    // Securely generate and store a unique, anonymous Installation ID
    val installationId: String
        get() {
            var id = sharedPrefs.getString("install_id", null)
            if (id == null) {
                id = UUID.randomUUID().toString()
                sharedPrefs.edit().putString("install_id", id).apply()
            }
            return id
        }

    fun reportPing(remoteConfigManager: RemoteConfigManager, status: String = "active", channelName: String? = null) {
        val url = remoteConfigManager.telemetryUrl
        if (url.isBlank() || !url.startsWith("http")) {
            Log.d("Telemetry", "Telemetry skipped: URL is blank or invalid")
            return
        }

        scope.launch {
            try {
                val payload = JSONObject().apply {
                    put("installation_id", installationId)
                    put("app_version_code", 1) // version code
                    put("app_version_name", "2.1.0")
                    put("status", status)
                    put("device_model", "${Build.MANUFACTURER} ${Build.MODEL}")
                    put("os_version", "Android ${Build.VERSION.RELEASE}")
                    if (channelName != null) {
                        put("active_channel", channelName)
                    }
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                
                val request = okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("User-Agent", "DSTWRTV/Telemetry")
                    .addHeader("Content-Type", "application/json")
                    .build()

                NetworkClient.okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Log.d("Telemetry", "Telemetry heartbeat reported successfully!")
                    } else {
                        Log.w("Telemetry", "Telemetry server returned error: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Telemetry", "Failed to report telemetry heartbeat: ${e.message}")
            }
        }
    }
}
