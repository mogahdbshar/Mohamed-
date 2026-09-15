package com.dstwrtv.app.streaming.domain.subtitle

class SubtitleSelector {
    fun choose(
        tracks: List<SubtitleTrack>,
        selection: TrackSelection
    ): SubtitleTrack? {
        if (!selection.subtitlesEnabled) return null
        val preferred = selection.preferredSubtitleLanguage?.trim()?.lowercase()
        if (!preferred.isNullOrBlank()) {
            tracks.firstOrNull { it.language.lowercase() == preferred }?.let { return it }
            tracks.firstOrNull { it.language.lowercase().startsWith(preferred) }?.let { return it }
        }
        return tracks.firstOrNull { it.isDefault } ?: tracks.firstOrNull()
    }

    fun chooseAudio(
        tracks: List<AudioTrack>,
        selection: TrackSelection
    ): AudioTrack? {
        val preferred = selection.preferredAudioLanguage?.trim()?.lowercase()
        if (!preferred.isNullOrBlank()) {
            tracks.firstOrNull { it.language.lowercase() == preferred }?.let { return it }
            tracks.firstOrNull { it.language.lowercase().startsWith(preferred) }?.let { return it }
        }
        return tracks.firstOrNull { it.isDefault } ?: tracks.firstOrNull()
    }
}
