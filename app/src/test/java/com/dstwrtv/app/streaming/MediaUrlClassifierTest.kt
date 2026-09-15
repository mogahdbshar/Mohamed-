package com.dstwrtv.app.streaming

import com.dstwrtv.app.streaming.data.remote.MediaUrlClassifier
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaUrlClassifierTest {
    @Test fun detectsHlsByExtension() = assertEquals(
        MediaUrlClassifier.Type.HLS,
        MediaUrlClassifier.classify("https://example.test/video.m3u8?token=x")
    )

    @Test fun detectsDashByContentType() = assertEquals(
        MediaUrlClassifier.Type.DASH,
        MediaUrlClassifier.classify("https://example.test/video", "application/dash+xml")
    )

    @Test fun detectsProgressiveVideo() = assertEquals(
        MediaUrlClassifier.Type.PROGRESSIVE,
        MediaUrlClassifier.classify("https://example.test/video.mp4")
    )
}
