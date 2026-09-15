package com.dstwrtv.app.streaming.domain.source

/** Central policy used before a source is handed to the player. */
data class SourcePolicy(
    val maxSources: Int = 8,
    val maxProvidersInParallel: Int = 4,
    val maxAttemptsPerPlayback: Int = 5,
    val allowHttpDirect: Boolean = true,
    val allowEmbeddedHttps: Boolean = true,
    val preferDirect: Boolean = true,
    val preferAdaptiveFormats: Boolean = true,
    val preferredLanguages: List<String> = emptyList()
)

object SourcePolicies {
    val balanced = SourcePolicy()
}
