package com.dstwrtv.app.streaming.domain.source

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/**
 * Resolves playback candidates without relaying video bytes through the app.
 * The provider supplier keeps the engine synchronized with the live registry.
 */
class SourceEngine(
    private val providersSupplier: () -> List<SourceProvider>,
    private val healthStore: SourceHealthStore = InMemorySourceHealthStore(),
    private val maxConcurrentProviders: Int = 4
) {
    constructor(
        providers: List<SourceProvider>,
        healthStore: SourceHealthStore = InMemorySourceHealthStore(),
        maxConcurrentProviders: Int = 4
    ) : this({ providers }, healthStore, maxConcurrentProviders)

    private val mutex = Mutex()
    private val inFlight = mutableMapOf<String, Deferred<SourceResolution>>()
    private val semaphore = Semaphore(maxConcurrentProviders.coerceAtLeast(1))

    suspend fun resolve(request: SourceRequest, maxSources: Int = 6): SourceResolution {
        val key = "${request.mediaType}:${request.contentKey}:${request.seasonNumber}:${request.episodeNumber}:${request.preferredLanguage}"
        val deferred = mutex.withLock {
            inFlight[key] ?: coroutineScope {
                async(Dispatchers.IO) {
                    try {
                        resolveInternal(request, maxSources)
                    } finally {
                        mutex.withLock { inFlight.remove(key) }
                    }
                }.also { inFlight[key] = it }
            }
        }
        return deferred.await()
    }

    private suspend fun resolveInternal(request: SourceRequest, maxSources: Int): SourceResolution =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val candidates = SourceProviderHealthRanker.rank(
                providersSupplier()
                    .filter { it.id.isNotBlank() }
                    .filterNot { healthStore.isTemporarilyUnavailable(it.id, now) },
                healthStore.snapshot()
            )
            val resolved = coroutineScope {
                candidates.map { provider ->
                    async {
                        semaphore.withPermit {
                            val started = System.nanoTime()
                            runCatching { provider.resolve(request) }
                                .onSuccess {
                                    healthStore.recordSuccess(
                                        provider.id,
                                        (System.nanoTime() - started) / 1_000_000L
                                    )
                                }
                                .onFailure { healthStore.recordFailure(provider.id) }
                                .getOrDefault(emptyList())
                                .filter(::isSafeDirectCandidate)
                                .map { provider.priority to it }
                        }
                    }
                }.awaitAll()
            }
            val sources = resolved.flatten()
                .sortedWith(
                    compareByDescending<Pair<Int, PlaybackSource>> { it.first }
                        .thenByDescending { it.second.quality ?: 0 }
                )
                .map { it.second }
                .distinctBy { normalizeUrl(it.url) }
                .take(maxSources.coerceAtLeast(1))
            SourceResolution(request, sources)
        }

    private fun isSafeDirectCandidate(source: PlaybackSource): Boolean {
        if (source.url.isBlank() || source.requiresProxy) return false
        if (source.isEmbedded) return source.url.startsWith("https://")
        return source.url.startsWith("https://") || source.url.startsWith("http://")
    }

    private fun normalizeUrl(url: String): String = url.trim().removeSuffix("/")

    fun health(): List<SourceProviderHealth> = healthStore.snapshot()
}
