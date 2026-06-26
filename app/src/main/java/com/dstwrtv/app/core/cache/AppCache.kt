package com.dstwrtv.app.core.cache

import com.dstwrtv.app.core.constants.AppConstants
import java.util.concurrent.ConcurrentHashMap

object AppCache {
    private val memoryCache = ConcurrentHashMap<String, CacheEntry>()

    data class CacheEntry(
        val data: String,
        val timestamp: Long
    )

    fun get(key: String, maxAgeMs: Long = AppConstants.DEFAULT_CACHE_MAX_AGE_MS): String? {
        val entry = memoryCache[key] ?: return null
        if (System.currentTimeMillis() - entry.timestamp > maxAgeMs) {
            memoryCache.remove(key)
            return null
        }
        return entry.data
    }

    fun put(key: String, data: String) {
        memoryCache[key] = CacheEntry(data, System.currentTimeMillis())
    }

    fun clear() {
        memoryCache.clear()
    }
}
