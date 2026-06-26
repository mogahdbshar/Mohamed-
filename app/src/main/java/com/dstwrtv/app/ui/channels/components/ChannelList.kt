package com.dstwrtv.app.ui.channels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.components.ChannelVerticalRow
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun ChannelList(
    channels: List<Channel>,
    selectedChannel: Channel?,
    favorites: List<Channel>,
    onChannelSelect: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Int = 110
) {
    val favoriteUrls = remember(favorites) {
        favorites.map { it.url }.toSet()
    }

    if (channels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DSTWRTheme.SurfaceDark.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = DSTWRTheme.BorderSoft.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(32.dp)
            ) {
                // Glowing Icon container
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.6f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "لم يتم العثور على قنوات",
                        tint = DSTWRTheme.PrimaryRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "عذراً، لم نجد قنوات مطابقة",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "تأكد من إملاء الكلمات بشكل صحيح، أو جرب البحث بكلمات أبسط، أو تصفح القنوات عبر اختيار باقة أخرى.",
                    color = DSTWRTheme.TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = bottomPadding.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
        ) {
            items(channels, key = { it.url }) { channel ->
                val isFav = favoriteUrls.contains(channel.url)
                ChannelVerticalRow(
                    channel = channel,
                    isFavorite = isFav,
                    isActive = selectedChannel?.url == channel.url,
                    onSelect = { onChannelSelect(channel) },
                    onToggleFavorite = { onToggleFavorite(channel) }
                )
            }
        }
    }
}
