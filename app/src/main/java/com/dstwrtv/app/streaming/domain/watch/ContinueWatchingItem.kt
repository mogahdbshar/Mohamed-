package com.dstwrtv.app.streaming.domain.watch

import com.dstwrtv.app.streaming.domain.model.MediaType

data class ContinueWatchingItem(
    val contentKey: String,
    val mediaType: MediaType,
    val title: String,
    val posterUrl: String? = null,
    val positionMs: Long,
    val durationMs: Long,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val updatedAtEpochMs: Long
)
