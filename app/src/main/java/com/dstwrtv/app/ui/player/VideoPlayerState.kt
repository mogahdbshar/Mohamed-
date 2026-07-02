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
import com.dstwrtv.app.core.util.findActivity
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

@OptIn(UnstableApi::class)
class VideoPlayerState(
    context: Context,
    val coroutineScope: CoroutineScope
) {
    private var contextRef = WeakReference(context)
    val context: Context get() = contextRef.get() ?: error("Context has been garbage collected")

    fun updateContext(newContext: Context) {
        if (contextRef.get() != newContext) {
            contextRef = WeakReference(newContext)
        }
    }

    var url by mutableStateOf("")
        private set

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
    private var networkJob: Job? = null

    init {
        initBrightness()
        observeNetwork()
    }

    private fun observeNetwork() {
        networkJob?.cancel()
        networkJob = coroutineScope.launch {
            com.dstwrtv.app.core.util.NetworkUtils.isOnline.collect { isOnline ->
                if (isOnline && isError && url.isNotBlank()) {
                    retryCount = 0
                    refresh()
                }
            }
        }
    }

    private fun initBrightness() {
        val activity = context.findActivity()
        activity?.window?.attributes?.let {
            brightnessLevel = if (it.screenBrightness < 0) 0.5f else it.screenBrightness
        }
    }

    fun updateUrl(newUrl: String) {
        if (this.url == newUrl) return
        this.url = newUrl
        
        isBuffering = true
        isError = false
        isRetrying = false
        retryCount = 0
        retryJob?.cancel()
        stopJob?.cancel()
        
        if (player == null) {
            initPlayer()
        } else {
            loadUrl()
        }
    }

    private fun initPlayer() {
        val buildExoPlayer: (Context) -> ExoPlayer = { ctx ->
            // Use the original context directly to prevent AppOps attribution errors and audio/video issues
            val mediaContext = ctx

            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("DSTWRTV/2.1.0/Android")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setDefaultRequestProperties(mapOf(
                    "Referer" to "http://12k-service.org/",
                    "User-Agent" to "DSTWRTV/2.1.0/Android"
                ))
            
            val mediaSourceFactory = DefaultMediaSourceFactory(mediaContext)
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

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build()

            ExoPlayer.Builder(mediaContext)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .setAudioAttributes(audioAttributes, true)
                .setWakeMode(C.WAKE_MODE_NETWORK)
                .build().apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        }

        player = try {
            // CRITICAL FIX: Use the original Activity Context directly to correctly bind to window/surface.
            // Do NOT use applicationContext or createAttributionContext, which throws AppOps errors and fails to display video.
            buildExoPlayer(context)
        } catch (e: Exception) {
            null
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
        if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
            try {
                player?.seekToDefaultPosition()
                player?.prepare()
                player?.play()
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val isRecoverable = when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
            PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE,
            PlaybackException.ERROR_CODE_IO_UNSPECIFIED,
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED,
            PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED -> true
            else -> true // Retry almost anything to keep IPTV running!
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
        if (!com.dstwrtv.app.core.util.NetworkUtils.isInternetAvailable(context)) {
            isError = true
            isBuffering = false
            errorMessage = "لا يوجد اتصال بالإنترنت. يرجى التحقق من الشبكة وإعادة المحاولة."
            return
        }
        try {
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .apply {
                    val urlLower = url.lowercase()
                    if (urlLower.contains(".m3u8") || urlLower.contains("m3u8")) {
                        setMimeType(MimeTypes.APPLICATION_M3U8)
                    } else if (urlLower.contains(".mpd") || urlLower.contains("mpd")) {
                        setMimeType(MimeTypes.APPLICATION_MPD)
                    } else if (urlLower.contains(".ts")) {
                        setMimeType(MimeTypes.VIDEO_MP2T)
                    }
                }
                .build()
            
            player?.let {
                it.stop()
                it.clearMediaItems()
                it.setMediaItem(mediaItem)
                it.prepare()
                it.playWhenReady = true
                isPlaying = true
            }
        } catch (e: Exception) {
            isError = true
            errorMessage = "عذراً، رابط البث غير صالح."
        }
    }

    fun play() {
        player?.let {
            if (!it.isPlaying) {
                if (it.playbackState == Player.STATE_IDLE || isError) {
                    loadUrl()
                }
                it.play()
                isPlaying = true
            }
        }
    }

    fun pause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            }
        }
    }

    fun seekForward() {
        player?.let {
            it.seekTo(it.currentPosition + 15000)
        }
    }

    fun seekBackward() {
        player?.let {
            it.seekTo((it.currentPosition - 15000).coerceAtLeast(0))
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                pause()
            } else {
                play()
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
        loadUrl()
    }

    fun toggleResizeMode() {
        resizeMode = when (resizeMode) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
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
        networkJob?.cancel()
    }
}

@Composable
fun rememberVideoPlayerState(
    url: String,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): VideoPlayerState {
    val state = remember {
        VideoPlayerState(context, coroutineScope)
    }
    
    LaunchedEffect(url) {
        state.updateUrl(url)
    }
    
    DisposableEffect(state) {
        onDispose {
            state.release()
        }
    }
    
    return state
}
