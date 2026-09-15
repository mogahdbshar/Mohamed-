package com.dstwrtv.app.streaming.domain.metadata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Small bounded process cache for metadata responses.
 * Fresh data is returned immediately. Stale data can be returned while a
 * single background refresh updates the entry. Failed Result values are not
 * retained, so temporary provider outages do not poison the cache.
 */
class MetadataCache(
    private val maxEntries: Int = 160,
    private val freshTtlMs: Long = 15 * 60 * 1000L,
    private val staleTtlMs: Long = 24 * 60 * 60 * 1000L,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private data class Entry(val value: Any, val freshUntil: Long, val staleUntil: Long)

    private val mutex = Mutex()
    private val entries = LinkedHashMap<String, Entry>(maxEntries, 0.75f, true)
    private val refreshes = mutableMapOf<String, Job>()

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> getOrLoad(key: String, loader: suspend () -> T): T {
        val now = System.currentTimeMillis()
        val existing = mutex.withLock { entries[key] }
        if (existing != null && now < existing.freshUntil) {
            return existing.value as T
        }
        if (existing != null && now < existing.staleUntil) {
            scheduleRefresh(key, loader)
            return existing.value as T
        }
        return loadNow(key, loader)
    }

    private suspend fun <T> loadNow(key: String, loader: suspend () -> T): T {
        val value = loader()
        put(key, value)
        return value
    }

    private suspend fun <T> scheduleRefresh(key: String, loader: suspend () -> T) {
        mutex.withLock {
            if (refreshes[key]?.isActive == true) return
            refreshes[key] = scope.launch {
                try {
                    val value = loader()
                    put(key, value)
                } catch (_: Throwable) {
                    // Keep the stale value when background refresh fails.
                } finally {
                    mutex.withLock { refreshes.remove(key) }
                }
            }
        }
    }

    private suspend fun <T> put(key: String, value: T) {
        if (value is Result<*> && value.isFailure) return
        val now = System.currentTimeMillis()
        mutex.withLock {
            entries[key] = Entry(value as Any, now + freshTtlMs, now + staleTtlMs)
            while (entries.size > maxEntries) {
                entries.entries.iterator().next().also { entries.remove(it.key) }
            }
        }
    }
}
