package com.example.ui

import com.example.ui.components.DasturTheme
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.media3.common.Format
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.content.pm.ActivityInfo
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.media3.ui.AspectRatioFrameLayout
import android.provider.Settings
import kotlin.math.abs

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun CustomPauseIcon(tint: Color = Color.White) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(width = 4.dp, height = 16.dp).background(tint, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.size(width = 4.dp, height = 16.dp).background(tint, RoundedCornerShape(2.dp)))
    }
}

@Composable
fun CustomVolumeIcon(tint: Color = Color.White) {
    Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(16.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(2.0.dp.toPx(), 5.0.dp.toPx())
                lineTo(5.0.dp.toPx(), 5.0.dp.toPx())
                lineTo(9.0.dp.toPx(), 1.0.dp.toPx())
                lineTo(9.0.dp.toPx(), 15.0.dp.toPx())
                lineTo(5.0.dp.toPx(), 11.0.dp.toPx())
                lineTo(2.0.dp.toPx(), 11.0.dp.toPx())
                close()
            }
            drawPath(path, color = tint)
            
            drawArc(
                color = tint,
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx()),
                topLeft = androidx.compose.ui.geometry.Offset(3.0.dp.toPx(), 2.0.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(8.0.dp.toPx(), 12.0.dp.toPx())
            )
        }
    }
}

@Composable
fun CustomMuteIcon(tint: Color = Color.White) {
    Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(16.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(2.0.dp.toPx(), 5.0.dp.toPx())
                lineTo(5.0.dp.toPx(), 5.0.dp.toPx())
                lineTo(9.0.dp.toPx(), 1.0.dp.toPx())
                lineTo(9.0.dp.toPx(), 15.0.dp.toPx())
                lineTo(5.0.dp.toPx(), 11.0.dp.toPx())
                lineTo(2.0.dp.toPx(), 11.0.dp.toPx())
                close()
            }
            drawPath(path, color = tint.copy(alpha = 0.5f))
            
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(2.0.dp.toPx(), 2.0.dp.toPx()),
                end = androidx.compose.ui.geometry.Offset(14.0.dp.toPx(), 14.0.dp.toPx()),
                strokeWidth = 2.0.dp.toPx()
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    url: String, 
    channelName: String = "",
    isFullscreen: Boolean,
    onFullscreenToggle: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isBuffering by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("تعذر تحميل البث المباشر حالياً") }

    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }
    var videoResolution by remember { mutableStateOf("تلقائي") }
    var stopJob by remember { mutableStateOf<Job?>(null) }
    
    // New states for enhanced features
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var volumeLevel by remember { mutableFloatStateOf(1f) }
    var brightnessLevel by remember { mutableFloatStateOf(0.5f) }
    var gestureType by remember { mutableStateOf<String?>(null) } // "volume" or "brightness"
    var gestureValue by remember { mutableFloatStateOf(0f) }
    var showGestureOverlay by remember { mutableStateOf(false) }
    var gestureHideJob by remember { mutableStateOf<Job?>(null) }

    // Initialize brightness on startup if possible
    LaunchedEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.attributes?.let {
            brightnessLevel = if (it.screenBrightness < 0) 0.5f else it.screenBrightness
        }
    }

    // Use remember to keep the same player instance across recompositions
    val exoPlayer = remember(context) {
        val buildExoPlayer: (Context) -> ExoPlayer = { ctx ->
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("IPTVSmarters/1.0.0")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(8000) // Safe connection timeout to prevent hanging locks
                .setReadTimeoutMs(10000)   // Safe read timeout to protect emulator/app threads!
                .setDefaultRequestProperties(mapOf(
                    "Referer" to "http://12k-service.org/",
                    "User-Agent" to "IPTVSmarters/1.0.0"
                ))
            
            val mediaSourceFactory = DefaultMediaSourceFactory(ctx)
                .setDataSourceFactory(httpDataSourceFactory)

            // Smart Load Control to prevent network drain and bloat while keeping stream ultra-live
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    15000,   // Min buffer (15s) to avoid interruptions
                    50000,   // Max buffer (50s) to absorb network spikes
                    500,     // Buffer for playback to start very quickly (0.5s)
                    2000     // Buffer after rebuffer to recover smoothly
                )
                .build()

            ExoPlayer.Builder(ctx)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .build().apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        }

        try {
            val fallbackContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    context.createAttributionContext("media")
                } catch (ce: Exception) {
                    context
                }
            } else {
                context
            }
            buildExoPlayer(fallbackContext)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                buildExoPlayer(context)
            } catch (inner: Exception) {
                inner.printStackTrace()
                null
            }
        }
    }

    // Auto-hide controls effect
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3500)
            showControls = false
        }
    }

    // Sync isPlaying state with exoplayer updates
    DisposableEffect(url, exoPlayer) {
        val player = exoPlayer
        var listener: Player.Listener? = null
        
        if (player != null && url.isNotBlank()) {
            try {
                isBuffering = true
                isError = false
                isPlaying = true
                videoResolution = "تلقائي"

                listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        isBuffering = playbackState == Player.STATE_BUFFERING
                        isError = false
                    }

                    override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                        if (videoSize.height > 0) {
                            videoResolution = "${videoSize.height}p"
                            if (videoSize.height >= 1080) videoResolution += " FHD"
                            else if (videoSize.height >= 720) videoResolution += " HD"
                        }
                    }

                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                        if (!playing) {
                            stopJob?.cancel()
                            stopJob = coroutineScope.launch {
                                delay(30_000L) // Wait 30 seconds
                                if (player.playbackState != Player.STATE_IDLE) {
                                    player.stop()
                                    player.clearMediaItems()
                                }
                            }
                        } else {
                            stopJob?.cancel()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        isBuffering = false
                        isError = true
                        errorMessage = when (error.errorCode) {
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                                "الاتصال بالإنترنت غير مستقر. جاري محاولة إعادة الاتصال تلقائياً..."
                            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ->
                                "عذراً، البث مقطوع من المصدر حالياً. يرجى المحاولة لاحقاً."
                            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
                            PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED ->
                                "صيغة البث غير مدعومة على هذا الجهاز."
                            else -> "حدث خطأ غير متوقع أثناء تشغيل القناة. جاري المعالجة..."
                        }
                    }
                }
                player.addListener(listener)
                
                // Content-Type Adaptive Stream routing for IPTV
                val mediaItem = MediaItem.Builder()
                    .setUri(url)
                    .apply {
                        val urlLower = url.lowercase()
                        if (urlLower.contains(".m3u8") || urlLower.contains("m3u8")) {
                            setMimeType(MimeTypes.APPLICATION_M3U8)
                        } else if (urlLower.contains(".mpd") || urlLower.contains("mpd")) {
                            setMimeType(MimeTypes.APPLICATION_MPD)
                        }
                    }
                    .build()
                
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
            } catch (e: Exception) {
                e.printStackTrace()
                isBuffering = false
                isError = true
                errorMessage = "عذراً، حدث خطأ أثناء تشغيل البث المباشر. يرجى إعادة المحاولة."
            }
        }

        onDispose {
            if (player != null) {
                try {
                    player.stop()
                    player.clearMediaItems()
                    if (listener != null) {
                        player.removeListener(listener)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Release ExoPlayer on dynamic component lifecycle exit
    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                exoPlayer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Track dynamic muting status on exoplayer
    LaunchedEffect(isMuted, exoPlayer) {
        exoPlayer?.let {
            it.volume = if (isMuted) 0f else 1f
        }
    }

    // Handle Orientation manually
    LaunchedEffect(isFullscreen) {
        val activity = context.findActivity() ?: return@LaunchedEffect
        val window = activity.window
        val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        
        if (isFullscreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            insetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            insetsController.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(
        modifier = if (isFullscreen) {
            modifier
                .background(Color.Black)
        } else {
            modifier
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color.Black)))
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.2.dp, DasturTheme.BorderSoft), RoundedCornerShape(20.dp))
        }.pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragStart = { offset ->
                    val isLeftSide = offset.x < size.width / 2
                    gestureType = if (isLeftSide) "brightness" else "volume"
                    showGestureOverlay = true
                    gestureHideJob?.cancel()
                },
                onDragEnd = {
                    gestureHideJob = coroutineScope.launch {
                        delay(1000)
                        showGestureOverlay = false
                        gestureType = null
                    }
                },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    val sensitivity = 0.002f
                    if (gestureType == "volume") {
                        volumeLevel = (volumeLevel - dragAmount * sensitivity).coerceIn(0f, 1f)
                        exoPlayer?.volume = volumeLevel
                        isMuted = volumeLevel == 0f
                        gestureValue = volumeLevel
                    } else if (gestureType == "brightness") {
                        brightnessLevel = (brightnessLevel - dragAmount * sensitivity).coerceIn(0f, 1f)
                        val activity = context.findActivity()
                        activity?.let {
                            val lp = it.window.attributes
                            lp.screenBrightness = brightnessLevel
                            it.window.attributes = lp
                        }
                        gestureValue = brightnessLevel
                    }
                }
            )
        }.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            showControls = !showControls
        },
        contentAlignment = Alignment.Center
    ) {
        if (exoPlayer != null) {
            AndroidView(
                factory = { ctx ->
                    try {
                        PlayerView(ctx).apply {
                            this.player = exoPlayer
                            // Disable standard ExoPlayer black bar controllers to draw our stunning premium Jetpack Compose overlay
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            keepScreenOn = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        FrameLayout(ctx)
                    }
                },
                update = { view ->
                    if (view is PlayerView) {
                        view.resizeMode = resizeMode
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "مشغل الوسائط غير متوفر حالياً",
                color = Color.White,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Modern Sleek Compose Control Layer Overlays!
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                // Top Overlay Header: Live Badge & Glow Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (channelName.isNotBlank()) {
                            Text(
                                text = channelName,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                        
                        // Modern LIVE Pulse badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFF4500).copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "بث مباشر",
                            color = Color.White,
                            fontSize = 10.sp,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Quality Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = videoResolution,
                            color = Color(0xFFFFD100),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .border(1.dp, Color(0xFFFFD100).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Central Interactive Play/Pause glow button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(54.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable {
                            if (isPlaying) {
                                exoPlayer?.pause()
                            } else {
                                if (exoPlayer?.playbackState == Player.STATE_IDLE) {
                                    // re-prepare if it was stopped after 30s
                                    val mediaItem = MediaItem.Builder().setUri(url).build()
                                    exoPlayer?.setMediaItem(mediaItem)
                                    exoPlayer?.prepare()
                                }
                                exoPlayer?.play()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        CustomPauseIcon(tint = Color.White)
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Bottom Control Layout: Mute, Refresh, and Settings Quick Toggles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom Volume controller using vector graphics
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            if (isMuted) {
                                CustomMuteIcon(tint = Color.White)
                            } else {
                                CustomVolumeIcon(tint = Color.White)
                            }
                        }

                        // Stream Refresh Button (re-buffers to stay ultra-live without sync lag)
                        IconButton(
                            onClick = {
                                exoPlayer?.let {
                                    it.stop()
                                    it.prepare()
                                    it.play()
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Re-buffer",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        // Fullscreen Toggle Button
                        IconButton(
                            onClick = onFullscreenToggle,
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Aspect Ratio Toggle
                        IconButton(
                            onClick = {
                                resizeMode = when (resizeMode) {
                                    AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                    AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                    else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AspectRatio,
                                contentDescription = "Aspect Ratio",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = "معدل الإطارات: 60 إطاراً/ثانية | جودة تلقائية",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        
        // Gesture Overlay Indicators
        AnimatedVisibility(
            visible = showGestureOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = if (gestureType == "brightness") Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(45.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                        .padding(vertical = 16.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = if (gestureType == "brightness") Icons.Rounded.WbSunny else {
                            if (gestureValue == 0f) Icons.Rounded.VolumeOff else if (gestureValue < 0.5f) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(120.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(gestureValue)
                                .background(DasturTheme.PrimaryRed, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }

        if (isBuffering && !isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF4500),
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "جاري تهيئة البث السريع...",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color(0xFFFF4500),
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage,
                        color = Color.White,
                        fontSize = 11.sp,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 14.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            exoPlayer?.let {
                                it.stop()
                                it.prepare()
                                it.play()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4500)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("إعادة المحاولة", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
