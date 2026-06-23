package com.dstwrtv.app.ui.player.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun PlayerControlsOverlay(
    visible: Boolean,
    channelName: String,
    videoResolution: String,
    isPlaying: Boolean,
    isMuted: Boolean,
    isFullscreen: Boolean,
    onClose: () -> Unit,
    onPlayPause: () -> Unit,
    onMuteToggle: () -> Unit,
    onRefresh: () -> Unit,
    onFullscreenToggle: () -> Unit,
    onResizeToggle: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        ) {
            // Top Header
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

            // Center Play/Pause
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .clickable { onPlayPause() },
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

            // Bottom Bar
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
                    IconButton(
                        onClick = onMuteToggle,
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

                    IconButton(
                        onClick = onRefresh,
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

                    IconButton(
                        onClick = onResizeToggle,
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
}
