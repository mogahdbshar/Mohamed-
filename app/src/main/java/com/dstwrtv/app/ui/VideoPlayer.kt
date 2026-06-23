package com.dstwrtv.app.ui

import com.dstwrtv.app.ui.player.components.*
import com.dstwrtv.app.ui.player.VideoPlayerState
import com.dstwrtv.app.ui.player.rememberVideoPlayerState
import com.dstwrtv.app.ui.components.DasturTheme
import com.dstwrtv.app.util.findActivity
import com.dstwrtv.app.util.toggleFullscreen
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput

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
    val state = rememberVideoPlayerState(url = url)

    // Auto-hide controls effect
    LaunchedEffect(state.showControls) {
        if (state.showControls) {
            delay(3500)
            state.showControls = false
        }
    }

    // Handle Orientation and system bars manually
    LaunchedEffect(isFullscreen) {
        context.findActivity()?.toggleFullscreen(isFullscreen)
    }

    Box(
        modifier = if (isFullscreen) {
            modifier.background(Color.Black)
        } else {
            modifier
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color.Black)))
                .clip(RoundedCornerShape(20.dp))
                .border(BorderStroke(1.2.dp, DasturTheme.BorderSoft), RoundedCornerShape(20.dp))
        }.pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragStart = { offset ->
                    val isLeftSide = offset.x < size.width / 2
                    state.showGesture(if (isLeftSide) "brightness" else "volume")
                },
                onDragEnd = {
                    state.hideGestureAfterDelay()
                },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    if (state.gestureType == "volume") {
                        state.updateVolume(dragAmount)
                    } else if (state.gestureType == "brightness") {
                        state.updateBrightness(dragAmount)
                    }
                }
            )
        }.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            state.showControls = !state.showControls
        },
        contentAlignment = Alignment.Center
    ) {
        if (state.player != null) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = state.player
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        keepScreenOn = true
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { view ->
                    view.player = state.player
                    view.resizeMode = state.resizeMode
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

        PlayerControlsOverlay(
            visible = state.showControls,
            channelName = channelName,
            videoResolution = state.videoResolution,
            isPlaying = state.isPlaying,
            isMuted = state.isMuted,
            isFullscreen = isFullscreen,
            onClose = onClose,
            onPlayPause = { state.togglePlayPause() },
            onMuteToggle = { state.toggleMute() },
            onRefresh = { state.refresh() },
            onFullscreenToggle = onFullscreenToggle,
            onResizeToggle = { state.toggleResizeMode() }
        )

        GestureOverlay(
            visible = state.showGestureOverlay,
            gestureType = state.gestureType,
            gestureValue = state.gestureValue
        )

        if (state.isBuffering && !state.isError) {
            LoadingOverlay()
        }

        if (state.isError) {
            ErrorOverlay(
                errorMessage = state.errorMessage,
                isRetrying = state.isRetrying,
                onRetry = { state.refresh() }
            )
        }
    }
}

