package com.dstwrtv.app.ui.player

import android.content.Context
import android.os.Build
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.dstwrtv.app.util.findActivity
import kotlinx.coroutines.*

@OptIn(UnstableApi::class)
class VideoPlayerState(
    val context: Context,
    val coroutineScope: CoroutineScope,
    val url: String
) {
    var isBuffering by mutableStateOf(true)
    var isError by mutableStateOf(false)
    var errorMessage by mutableStateOf("تعذر تحميل البث المباشر حالياً")
    var isRetrying by mutableStateOf(false)
    var isPlaying by mutableStateOf(true)
    var isMuted by mutableStateOf(false)
    var showControls by mutableStateOf(false)
    var videoResolution by mutableStateOf("تلقائي")
    var resizeMode by mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT)
    var volumeLevel by mutableFloatStateOf(1f)
    var brightnessLevel by mutableFloatStateOf(0.5f)
    var gestureType by mutableStateOf<String?>(null)
    var gestureValue by mutableFloatStateOf(0f)
    var showGestureOverlay by mutableStateOf(false)
    
    private var retryCount = 0
    private val maxRetries = 5
    private var retryJob: Job? = null

    internal var player: ExoPlayer? by mutableStateOf(null)
    private var stopJob: Job? = null
    private var gestureHideJob: Job? = null

    init {
        initBrightness()
        initPlayer()
    }

    private fun initBrightness() {
        val activity = context.findActivity()
        activity?.window?.attributes?.let {
            brightnessLevel = if (it.screenBrightness < 0) 0.5f else it.screenBrightness
        }
    }

    private fun initPlayer() {
        val buildExoPlayer: (Context) -> ExoPlayer = { ctx ->
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("DasturTV/2.1.0/Android")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setDefaultRequestProperties(mapOf(
                    "Referer" to "http://12k-service.org/",
                    "User-Agent" to "DasturTV/2.1.0/Android"
                ))
            
            val mediaSourceFactory = DefaultMediaSourceFactory(ctx)
                .setDataSourceFactory(httpDataSourceFactory)

            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    20_000, // Min buffer to start
                    90_000, // Max buffer (increased for better stability)
                    3_000,  // Buffer for playback start
                    6_000   // Buffer for re-playback after buffer empty
                )
                .setBackBuffer(30_000, true) // Enable back buffer for smoother seeks/rewinds
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()

            ExoPlayer.Builder(ctx)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .build().apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        }

        player = try {
            val playerContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.applicationContext.createAttributionContext("media")
            } else {
                context
            }
            buildExoPlayer(playerContext)
        } catch (e: Exception) {
            try { buildExoPlayer(context) } catch (inner: Exception) { null }
        }

        setupPlayerListener()
        loadUrl()
    }

    private fun setupPlayerListener() {
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_READY) {
                    isError = false
                    isRetrying = false
                    retryCount = 0
                    retryJob?.cancel()
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
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
                        delay(60_000L) // Wait longer before releasing resources
                        if (player?.playbackState != Player.STATE_IDLE && !isPlaying) {
                            player?.stop()
                            player?.clearMediaItems()
                        }
                    }
                } else {
                    stopJob?.cancel()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                handleRetry(error)
            }
        })
    }

    private fun handleRetry(error: PlaybackException) {
        val isRecoverable = when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
            PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE -> true
            else -> false
        }

        if (isRecoverable && retryCount < maxRetries) {
            isRetrying = true
            isError = true
            errorMessage = "جاري محاولة الاتصال تلقائياً (${retryCount + 1}/$maxRetries)..."
            
            retryJob?.cancel()
            retryJob = coroutineScope.launch {
                delay(3000L * (retryCount + 1)) // Exponential backoff
                retryCount++
                refresh()
            }
        } else {
            isError = true
            isRetrying = false
            errorMessage = when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "عذراً، البث مقطوع من المصدر حالياً."
                PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> "صيغة البث غير مدعومة على هذا الجهاز."
                else -> "فشل تشغيل القناة، يرجى التحقق من اتصال الإنترنت."
            }
        }
    }

    private fun loadUrl() {
        if (url.isBlank()) return
        try {
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .apply {
                    val urlLower = url.lowercase()
                    if (urlLower.contains(".m3u8") || urlLower.contains("m3u8") || urlLower.contains(".ts")) {
                        setMimeType(MimeTypes.APPLICATION_M3U8)
                    } else if (urlLower.contains(".mpd") || urlLower.contains("mpd")) {
                        setMimeType(MimeTypes.APPLICATION_MPD)
                    }
                }
                .build()
            
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            isError = true
            errorMessage = "عذراً، رابط البث غير صالح."
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            } else {
                if (it.playbackState == Player.STATE_IDLE || isError) {
                    loadUrl()
                }
                it.play()
                isPlaying = true
            }
        }
    }

    fun toggleMute() {
        isMuted = !isMuted
        player?.volume = if (isMuted) 0f else volumeLevel
    }

    fun refresh() {
        isError = false
        isBuffering = true
        player?.stop()
        loadUrl()
    }

    fun toggleResizeMode() {
        resizeMode = when (resizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
    }

    fun updateVolume(dragAmount: Float) {
        val sensitivity = 0.002f
        volumeLevel = (volumeLevel - dragAmount * sensitivity).coerceIn(0f, 1f)
        player?.volume = volumeLevel
        isMuted = volumeLevel == 0f
        gestureValue = volumeLevel
    }

    fun updateBrightness(dragAmount: Float) {
        val sensitivity = 0.002f
        brightnessLevel = (brightnessLevel - dragAmount * sensitivity).coerceIn(0f, 1f)
        val activity = context.findActivity()
        activity?.let {
            val lp = it.window.attributes
            lp.screenBrightness = brightnessLevel
            it.window.attributes = lp
        }
        gestureValue = brightnessLevel
    }

    fun showGesture(type: String) {
        gestureType = type
        showGestureOverlay = true
        gestureHideJob?.cancel()
    }

    fun hideGestureAfterDelay() {
        gestureHideJob = coroutineScope.launch {
            delay(1000)
            showGestureOverlay = false
            gestureType = null
        }
    }

    fun release() {
        player?.release()
        player = null
        stopJob?.cancel()
        gestureHideJob?.cancel()
    }
}

@Composable
fun rememberVideoPlayerState(
    url: String,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): VideoPlayerState {
    val state = remember(url) {
        VideoPlayerState(context, coroutineScope, url)
    }
    
    DisposableEffect(state) {
        onDispose {
            state.release()
        }
    }
    
    return state
}
