package com.dstwrtv.app.streaming.data.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import com.dstwrtv.app.streaming.data.remote.MediaUrlClassifier
import com.dstwrtv.app.streaming.domain.model.PlaybackRequest

object MediaItemFactory {
    fun create(request: PlaybackRequest): MediaItem {
        val uri = Uri.parse(request.source.url)
        val mediaType = when (MediaUrlClassifier.classify(request.source.url)) {
            MediaUrlClassifier.Type.HLS -> MimeTypes.APPLICATION_M3U8
            MediaUrlClassifier.Type.DASH -> MimeTypes.APPLICATION_MPD
            MediaUrlClassifier.Type.PROGRESSIVE,
            MediaUrlClassifier.Type.UNKNOWN -> null
        }
        val builder = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(request.title)
                    .build()
            )
        mediaType?.let(builder::setMimeType)
        return builder.build()
    }
}
