package com.dstwrtv.app.streaming.domain.source

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SourceDiscoveryEngine(
    private val registry: SourceRegistry,
    private val sourceEngine: SourceEngine,
    private val cache: SourceResultCache = SourceResultCache(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val mutex = Mutex()
    private val inFlight = mutableMapOf<String, kotlinx.coroutines.Deferred<SourceDiscoveryResult>>()

    suspend fun discover(request: SourceDiscoveryRequest): SourceDiscoveryResult {
        val key = requestKey(request)
        cache.get(key)?.let { return SourceDiscoveryResult(it, 0, 0, 0L) }
        val deferred = mutex.withLock {
            inFlight[key] ?: scope.async {
                val started = System.currentTimeMillis()
                val resolution = sourceEngine.resolve(SourceRequest(
                    mediaType = request.mediaType, provider = request.provider, providerId = request.providerId,
                    seasonNumber = request.seasonNumber, episodeNumber = request.episodeNumber,
                    preferredLanguage = request.preferredLanguage, title = request.title
                ), maxSources = 12)
                val sources = SourceSelectionPolicy.rank(resolution.sources, request.preferredLanguage, request.preferredQuality).take(12)
                if (sources.isNotEmpty()) cache.put(key, sources)
                SourceDiscoveryResult(sources, resolution.attemptedProviders, resolution.successfulProviders, System.currentTimeMillis() - started)
            }.also { inFlight[key] = it }
        }
        return try { deferred.await() } finally { mutex.withLock { if (inFlight[key] === deferred) inFlight.remove(key) } }
    }

    private fun requestKey(request: SourceDiscoveryRequest): String = listOf(
        request.mediaType.name, request.provider.lowercase(), request.providerId,
        request.seasonNumber ?: 0, request.episodeNumber ?: 0,
        request.preferredLanguage.orEmpty().lowercase(), request.preferredQuality ?: 0,
        request.title.orEmpty().lowercase()
    ).joinToString(":")
}
