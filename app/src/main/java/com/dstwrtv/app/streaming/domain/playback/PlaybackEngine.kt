package com.dstwrtv.app.streaming.domain.playback

import com.dstwrtv.app.streaming.domain.model.PlaybackRequest

/** Pluggable player contract. The existing Media3 player remains the primary implementation. */
interface PlaybackEngine {
    val id: String
    fun canPlay(request: PlaybackRequest): Boolean
}
