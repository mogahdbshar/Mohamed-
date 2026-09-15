package com.dstwrtv.app.streaming.domain.source

interface SourceProvider {
    val id: String
    val priority: Int

    suspend fun resolve(request: SourceRequest): List<PlaybackSource>
}

interface SourceHealthStore {
    fun isTemporarilyUnavailable(providerId: String, nowMs: Long = System.currentTimeMillis()): Boolean
    fun recordSuccess(providerId: String, latencyMs: Long)
    fun recordFailure(providerId: String, nowMs: Long = System.currentTimeMillis())
    fun snapshot(): List<SourceProviderHealth>
}
