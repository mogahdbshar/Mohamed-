package com.dstwrtv.app.streaming.domain.source

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

class InMemorySourceHealthStore(
    private val failureCooldownMs: Long = 5 * 60 * 1000L
) : SourceHealthStore {
    private data class Stats(
        var success: Int = 0,
        var failure: Int = 0,
        var totalLatencyMs: Long = 0L,
        var lastFailureAt: Long? = null,
        var disabledUntil: Long? = null
    )

    private val stats = ConcurrentHashMap<String, Stats>()

    override fun isTemporarilyUnavailable(providerId: String, nowMs: Long): Boolean =
        stats[providerId]?.disabledUntil?.let { it > nowMs } == true

    override fun recordSuccess(providerId: String, latencyMs: Long) {
        val value = stats.computeIfAbsent(providerId) { Stats() }
        synchronized(value) {
            value.success++
            value.totalLatencyMs += max(0L, latencyMs)
            value.disabledUntil = null
        }
    }

    override fun recordFailure(providerId: String, nowMs: Long) {
        val value = stats.computeIfAbsent(providerId) { Stats() }
        synchronized(value) {
            value.failure++
            value.lastFailureAt = nowMs
            value.disabledUntil = nowMs + failureCooldownMs
        }
    }

    override fun snapshot(): List<SourceProviderHealth> = stats.map { (id, value) ->
        synchronized(value) {
            val total = value.success + value.failure
            SourceProviderHealth(
                providerId = id,
                successRate = if (total == 0) 0.0 else value.success.toDouble() / total,
                averageLatencyMs = if (value.success == 0) 0L else value.totalLatencyMs / value.success,
                lastFailureAt = value.lastFailureAt,
                temporarilyDisabledUntil = value.disabledUntil
            )
        }
    }
}
