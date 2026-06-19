package com.example.ui

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isBuffering by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    // Custom data source with matching User-Agent & Referer for smooth bypassed authentication
    val httpDataSourceFactory = remember {
        DefaultHttpDataSource.Factory()
            .setUserAgent("IPTVSmarters/1.0.0")
            .setDefaultRequestProperties(mapOf("Referer" to "http://12k-service.org/"))
    }

    // High performance load controller designed strictly to minimize background network usage
    val loadControl = remember {
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                5000,   // Min buffer: starts fast
                15000,  // Max buffer: avoids excessive internet waste
                1500,   // Buffer for playback: immediate starts
                2000    // Buffer for play after rebuffering
            )
            .build()
    }

    val exoPlayer = remember {
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    // Handle play state and state updates reactively
    LaunchedEffect(url) {
        if (url.isNotBlank()) {
            isBuffering = true
            isError = false
            
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    // Attach ExoPlayer event trackers
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                isError = false
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                isError = true
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering) {
            CircularProgressIndicator(color = Color(0xFFFF4500))
        }

        if (isError) {
            androidx.compose.material3.Text(
                text = "❌ فشل تحميل البث المباشر. يرجى المحاولة لاحقاً",
                color = Color.White,
                fontSize = 12.sp,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.7f), shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            )
        }
    }
}
