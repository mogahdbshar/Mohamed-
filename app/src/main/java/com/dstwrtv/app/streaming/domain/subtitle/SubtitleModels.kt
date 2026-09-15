package com.dstwrtv.app.streaming.domain.subtitle

data class SubtitleTrack(
    val id: String,
    val language: String,
    val label: String,
    val url: String,
    val isDefault: Boolean = false,
    val isForced: Boolean = false
)

data class AudioTrack(
    val id: String,
    val language: String,
    val label: String,
    val isDefault: Boolean = false
)

data class TrackSelection(
    val preferredSubtitleLanguage: String? = null,
    val preferredAudioLanguage: String? = null,
    val subtitlesEnabled: Boolean = true
)
