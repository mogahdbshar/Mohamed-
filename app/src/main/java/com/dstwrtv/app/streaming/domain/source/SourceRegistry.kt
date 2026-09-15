package com.dstwrtv.app.streaming.domain.source

class SourceRegistry(
    providers: List<SourceProvider> = emptyList()
) {
    private val entries = LinkedHashMap<String, SourceProvider>()

    init {
        providers.forEach(::register)
    }

    fun register(provider: SourceProvider) {
        require(provider.id.isNotBlank()) { "Source provider id must not be blank" }
        entries[provider.id] = provider
    }

    fun remove(id: String) {
        entries.remove(id)
    }

    fun get(id: String): SourceProvider? = entries[id]

    fun all(): List<SourceProvider> = entries.values.toList()

    fun ordered(): List<SourceProvider> = entries.values.sortedByDescending { it.priority }
}
