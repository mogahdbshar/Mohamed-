package com.dstwrtv.app.streaming.domain.source

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Small bounded process cache for resolved source candidates. */
class SourceResultCache(
    private val maxEntries: Int = 64,
    private val freshTtlMs: Long = 5 * 60 * 1000L,
    private val staleTtlMs: Long = 30 * 60 * 1000L
) {
    private data class Entry(val sources: List<PlaybackSource>, val storedAt: Long)
    private val mutex = Mutex()
    private val values = LinkedHashMap<String, Entry>(16, 0.75f, true)

    suspend fun get(key: String, nowMs: Long = System.currentTimeMillis()): List<PlaybackSource>? = mutex.withLock {
        val entry = values[key] ?: return@withLock null
        if (nowMs - entry.storedAt > staleTtlMs) {
            values.remove(key)
            return@withLock null
        }
        entry.sources
    }

    suspend fun put(key: String, sources: List<PlaybackSource>, nowMs: Long = System.currentTimeMillis()) = mutex.withLock {
        if (sources.isEmpty()) return@withLock
        values[key] = Entry(sources, nowMs)
        while (values.size > maxEntries.coerceAtLeast(1)) values.remove(values.entries.first().key)
    }

    fun isFresh(storedAt: Long, nowMs: Long = System.currentTimeMillis()): Boolean =
        nowMs - storedAt <= freshTtlMs
}
