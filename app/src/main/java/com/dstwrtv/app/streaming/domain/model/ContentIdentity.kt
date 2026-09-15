package com.dstwrtv.app.streaming.domain.model

/** Stable cross-provider identity for a movie, series, episode, or channel. */
data class ContentIdentity(
    val type: MediaType,
    val provider: String,
    val providerId: String
) {
    val key: String
        get() = "${type.name.lowercase()}:$provider:$providerId"
}

data class EpisodeIdentity(
    val show: ContentIdentity,
    val seasonNumber: Int,
    val episodeNumber: Int
) {
    val key: String
        get() = "${show.key}:s$seasonNumber:e$episodeNumber"
}
