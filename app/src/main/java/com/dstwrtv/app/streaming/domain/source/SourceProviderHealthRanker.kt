package com.dstwrtv.app.streaming.domain.source

/** Keeps stable, successful providers ahead of repeatedly failing ones. */
object SourceProviderHealthRanker {
    fun rank(
        providers: List<SourceProvider>,
        health: List<SourceProviderHealth>
    ): List<SourceProvider> {
        val byId = health.associateBy { it.providerId }
        return providers.sortedWith(
            compareByDescending<SourceProvider> { byId[it.id]?.successRate ?: 0.5 }
                .thenBy { byId[it.id]?.averageLatencyMs ?: Long.MAX_VALUE }
                .thenByDescending { it.priority }
        )
    }
}
