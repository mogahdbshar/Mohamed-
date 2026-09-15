package com.dstwrtv.app.streaming.core

object TmdbImageUrlBuilder {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    fun poster(path: String?, width: Int = 342): String? = build(path, "w$width")
    fun backdrop(path: String?, width: Int = 780): String? = build(path, "w$width")
    fun logo(path: String?, width: Int = 342): String? = build(path, "w$width")
    fun still(path: String?, width: Int = 780): String? = build(path, "w$width")

    private fun build(path: String?, size: String): String? {
        val clean = path?.trim()?.removePrefix("/") ?: return null
        if (clean.isEmpty()) return null
        return BASE_URL + size + "/" + clean
    }
}
