package com.dstwrtv.app

import org.junit.Test
import java.net.URL
import java.net.HttpURLConnection
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import okhttp3.OkHttpClient
import okhttp3.Request

@RunWith(RobolectricTestRunner::class)
class CryptoTest {
    @Test
    fun fetchGithub() {
        try {
            val url = "https://raw.githubusercontent.com/mogahdbshar/app-core-assets/refs/heads/main/system_config.dat"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept-Encoding", "gzip")
                .build()
            
            val response = client.newCall(request).execute()
            val body = response.body?.string()?.trim() ?: ""
            println("Downloaded body length: ${body.length}, starts with: ${body.take(20)}")

            val decrypted = com.dstwrtv.app.core.util.CryptoHelper.decrypt(body) ?: throw Exception("decryption failed")
            println("decrypted starts with: " + decrypted.take(200))

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
