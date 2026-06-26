package com.dstwrtv.app.ui.player.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.VideoPlayer
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun ActiveChannelPlayerSection(
    activeCh: Channel,
    isFullscreen: Boolean,
    onFullscreenToggle: (Boolean) -> Unit,
    onClose: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    isInPipMode: Boolean = false
) {
    Column(modifier = if (isFullscreen || isInPipMode) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
        Box(
            modifier = if (isFullscreen || isInPipMode) Modifier.fillMaxSize() else Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            VideoPlayer(
                url = activeCh.url,
                channelName = activeCh.name,
                isFullscreen = isFullscreen,
                onFullscreenToggle = { onFullscreenToggle(!isFullscreen) },
                onClose = onClose,
                modifier = if (isFullscreen || isInPipMode) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                isInPipMode = isInPipMode
            )
        }

        if (!isFullscreen && !isInPipMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(DSTWRTheme.SurfaceDark, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(6.dp).background(DSTWRTheme.PrimaryRed, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "البث النشط المباشر",
                            color = DSTWRTheme.PrimaryRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeCh.name,
                            color = DSTWRTheme.TextMain,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "المفضلة",
                            tint = if (isFavorite) DSTWRTheme.AccentAmber else DSTWRTheme.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "إغلاق",
                            tint = DSTWRTheme.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
