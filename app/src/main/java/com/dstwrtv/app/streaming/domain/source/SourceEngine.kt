package com.dstwrtv.app.streaming.domain.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class SourceEngine(
    private val providers: List<SourceProvider>,
    private val healthStore: SourceHealthStore = InMemorySourceHealthStore()
) {
    suspend fun resolve(request: SourceRequest, maxSources: Int = 6): SourceResolution = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val candidates = providers
            .filterNot { it.id.isBlank() }
            .filterNot { healthStore.isTemporarilyUnavailable(it.id, now) }
            .sortedByDescending { it.priority }

        val resolved = coroutineScope {
            candidates.map { provider ->
                async {
                    val started = System.nanoTime()
                    runCatching { provider.resolve(request) }
                        .onSuccess { healthStore.recordSuccess(provider.id, (System.nanoTime() - started) / 1_000_000L) }
                        .onFailure { healthStore.recordFailure(provider.id) }
                        .getOrDefault(emptyList())
                        .filter { it.url.isNotBlank() && !it.requiresProxy }
                        .map { provider.priority to it }
                }
            }.awaitAll()
        }

        val sources = resolved
            .flatten()
            .sortedWith(
                compareByDescending<Pair<Int, PlaybackSource>> { it.first }
                    .thenByDescending { it.second.quality ?: 0 }
            )
            .map { it.second }
            .distinctBy { it.url }
            .take(maxSources)

        SourceResolution(request = request, sources = sources)
    }

    fun health(): List<SourceProviderHealth> = healthStore.snapshot()
}
