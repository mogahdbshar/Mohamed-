package com.dstwrtv.app.streaming.domain.model

/** Canonical identity used to merge metadata from independent providers. */
data class MediaIdentity(
    val mediaType: MediaType,
    val provider: String,
    val providerId: String,
    val externalIds: Map<String, String> = emptyMap()
) {
    val stableKey: String
        get() = "$mediaType:${provider.lowercase()}:$providerId"
}
