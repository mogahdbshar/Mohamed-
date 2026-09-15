package com.dstwrtv.app.streaming.domain.playback

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlaybackSession {
    private val mutex = Mutex()
    private val attempted = LinkedHashSet<String>()

    suspend fun nextSource(sources: List<com.dstwrtv.app.streaming.domain.source.PlaybackSource>): com.dstwrtv.app.streaming.domain.source.PlaybackSource? = mutex.withLock {
        sources.firstOrNull { attempted.add(normalize(it.url)) }
    }

    suspend fun reset() = mutex.withLock { attempted.clear() }

    suspend fun attemptedCount(): Int = mutex.withLock { attempted.size }

    private fun normalize(url: String): String = url.trim().removeSuffix("/")
}
