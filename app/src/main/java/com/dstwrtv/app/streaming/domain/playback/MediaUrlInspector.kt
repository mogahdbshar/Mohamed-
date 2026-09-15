package com.dstwrtv.app.streaming.domain.playback

object MediaUrlInspector {
    fun guessFormat(url: String, contentType: String? = null): String? {
        val type = contentType?.lowercase()?.substringBefore(';')
        when {
            type == "application/vnd.apple.mpegurl" || type == "application/x-mpegurl" -> return "hls"
            type == "application/dash+xml" -> return "dash"
            type?.startsWith("video/") == true -> return type.removePrefix("video/")
        }

        val path = url.substringBefore('?').lowercase()
        return when {
            path.endsWith(".m3u8") -> "hls"
            path.endsWith(".mpd") -> "dash"
            path.endsWith(".mp4") -> "mp4"
            path.endsWith(".mkv") -> "mkv"
            path.endsWith(".webm") -> "webm"
            path.endsWith(".mov") -> "mov"
            path.endsWith(".m4v") -> "m4v"
            path.endsWith(".ts") -> "mpeg-ts"
            else -> null
        }
    }

    fun isAdaptive(format: String?): Boolean =
        format.equals("hls", true) || format.equals("dash", true)
}
