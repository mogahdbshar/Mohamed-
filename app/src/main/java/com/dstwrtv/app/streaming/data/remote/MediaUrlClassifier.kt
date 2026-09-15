package com.dstwrtv.app.streaming.data.remote

/** Lightweight content-type inference used before creating a Media3 MediaItem. */
object MediaUrlClassifier {
    enum class Type { HLS, DASH, PROGRESSIVE, UNKNOWN }

    fun classify(url: String, contentType: String? = null): Type {
        val type = contentType?.lowercase().orEmpty()
        if ("application/vnd.apple.mpegurl" in type || "application/x-mpegurl" in type) return Type.HLS
        if ("application/dash+xml" in type) return Type.DASH

        val clean = url.substringBefore('?').substringBefore('#').lowercase()
        return when {
            clean.endsWith(".m3u8") -> Type.HLS
            clean.endsWith(".mpd") -> Type.DASH
            clean.endsWith(".mp4") || clean.endsWith(".mkv") || clean.endsWith(".webm") ||
                clean.endsWith(".mov") || clean.endsWith(".m4v") -> Type.PROGRESSIVE
            else -> Type.UNKNOWN
        }
    }
}
