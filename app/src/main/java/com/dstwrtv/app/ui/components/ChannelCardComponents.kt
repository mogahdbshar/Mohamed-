package com.dstwrtv.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.dstwrtv.app.model.Channel
import kotlinx.coroutines.launch

@Composable
fun ChannelCardCompact(
    channel: Channel,
    isFavorite: Boolean,
    isActive: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var isFocused by remember { mutableStateOf(false) }
    val isHighlighted = isActive || isFocused

    Box(
        modifier = Modifier
            .width(110.dp)
            .height(134.dp)
            .scale(if (isFocused) 1.05f else scale.value)
            .onFocusChanged { isFocused = it.isFocused }
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = if (isHighlighted) {
                        listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.25f), DSTWRTheme.SecondaryDark.copy(alpha = 0.85f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.03f))
                    },
                    radius = 250f
                )
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isHighlighted) listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.8f), DSTWRTheme.AccentAmber.copy(alpha = 0.5f))
                             else listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.02f))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable {
                coroutineScope.launch {
                    scale.animateTo(0.92f, animationSpec = tween(100))
                    scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                }
                onSelect()
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(channel.logo)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = channel.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Fit,
                        error = androidx.compose.ui.graphics.painter.ColorPainter(Color.Transparent),
                        fallback = androidx.compose.ui.graphics.painter.ColorPainter(Color.Transparent)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF20202F), Color(0xFF0B0B14))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (channel.name.length >= 2) channel.name.take(2).uppercase() else "TV",
                            color = if (isActive) DSTWRTheme.PrimaryRed else DSTWRTheme.TextMuted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) DSTWRTheme.AccentAmber else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(12.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.62f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("LIVE", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0x7F07070F))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.name,
                    color = DSTWRTheme.TextMain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ChannelVerticalRow(
    channel: Channel,
    isFavorite: Boolean,
    isActive: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var isFocused by remember { mutableStateOf(false) }
    val isHighlighted = isActive || isFocused

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .scale(if (isFocused) 1.02f else scale.value)
            .onFocusChanged { isFocused = it.isFocused }
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isHighlighted) {
                        listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.2f), DSTWRTheme.SecondaryDark.copy(alpha = 0.8f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.02f))
                    }
                )
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isHighlighted) listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.8f), DSTWRTheme.AccentAmber.copy(alpha = 0.5f))
                             else listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.02f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                coroutineScope.launch {
                    scale.animateTo(0.97f, animationSpec = tween(80))
                    scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                }
                onSelect()
            }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x35000000)),
                contentAlignment = Alignment.Center
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(channel.logo)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = channel.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Fit,
                        error = androidx.compose.ui.graphics.painter.ColorPainter(Color.Transparent),
                        fallback = androidx.compose.ui.graphics.painter.ColorPainter(Color.Transparent)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF20202F), Color(0xFF0B0B14))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (channel.name.length >= 2) channel.name.take(2).uppercase() else "TV",
                            color = if (isActive) DSTWRTheme.PrimaryRed else DSTWRTheme.TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = channel.name,
                    color = if (isActive) Color.White else DSTWRTheme.TextMain,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0x1F2A2A3D), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.category,
                            color = DSTWRTheme.TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(DSTWRTheme.PrimaryRed.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Text(
                            text = "LIVE",
                            color = Color(0xFF10B981),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "التفضيل",
                        tint = if (isFavorite) DSTWRTheme.AccentAmber else Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isActive) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "قيد التشغيل",
                        tint = DSTWRTheme.PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
