package com.dstwrtv.app.streaming.domain.source

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock

/**
 * Automatic multi-provider discovery. The caller supplies content identity,
 * never a playback URL. Providers are queried concurrently and candidates are
 * normalized by SourceEngine before playback.
 */
class SourceDiscoveryEngine(
    private val registry: SourceRegistry,
    private val sourceEngine: SourceEngine,
    private val cache: SourceResultCache = SourceResultCache(),
    private val maxConcurrentProviders: Int = 4,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val inFlightMutex = Mutex()
    private val inFlight = mutableMapOf<String, kotlinx.coroutines.Deferred<SourceDiscoveryResult>>()

    suspend fun discover(request: SourceDiscoveryRequest): SourceDiscoveryResult {
        val key = buildKey(request)
        cache.get(key)?.let { cached ->
            return SourceDiscoveryResult(cached, 0, 0, 0L)
        }

        val existing = inFlightMutex.withLock { inFlight[key] }
        if (existing != null) return existing.await()

        val deferred = scope.async {
            val started = System.currentTimeMillis()
            val providers = registry.enabledFor(
                SourceRequest(
                    mediaType = request.mediaType,
                    provider = request.provider,
                    providerId = request.providerId,
                    seasonNumber = request.seasonNumber,
                    episodeNumber = request.episodeNumber,
                    preferredLanguage = request.preferredLanguage
                )
            )
            val semaphore = Semaphore(maxConcurrentProviders.coerceAtLeast(1))
            val responses = coroutineScope {
                providers.map { provider ->
                    async {
                        semaphore.acquire()
                        try {
                            runCatching {
                                provider.resolve(
                                    SourceRequest(
                                        mediaType = request.mediaType,
                                        provider = request.provider,
                                        providerId = request.providerId,
                                        seasonNumber = request.seasonNumber,
                                        episodeNumber = request.episodeNumber,
                                        preferredLanguage = request.preferredLanguage
                                    )
                                )
                            }
                        } finally {
                            semaphore.release()
                        }
                    }
                }.awaitAll()
            }
            val candidates = responses.flatMap { it.getOrElse { emptyList() } }
            val ranked = SourceSelectionPolicy.rank(
                candidates,
                request.preferredLanguage,
                request.preferredQuality
            )
            val finalSources = ranked.distinctBy { it.url }.take(12)
            cache.put(key, finalSources)
            SourceDiscoveryResult(
                sources = finalSources,
                attemptedProviders = providers.size,
                successfulProviders = responses.count { it.isSuccess && it.getOrNull()?.isNotEmpty() == true },
                durationMs = System.currentTimeMillis() - started
            )
        }

        inFlightMutex.withLock { inFlight[key] = deferred }
        return try {
            deferred.await()
        } finally {
            inFlightMutex.withLock { if (inFlight[key] === deferred) inFlight.remove(key) }
        }
    }

    private fun buildKey(request: SourceDiscoveryRequest): String =
        listOf(
            request.mediaType.name,
            request.provider.lowercase(),
            request.providerId,
            request.seasonNumber ?: 0,
            request.episodeNumber ?: 0,
            request.preferredLanguage.orEmpty().lowercase(),
            request.preferredQuality ?: 0
        ).joinToString(":")
}
