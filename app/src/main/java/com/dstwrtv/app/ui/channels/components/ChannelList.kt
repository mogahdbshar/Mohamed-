package com.dstwrtv.app.ui.channels.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.components.ChannelVerticalRow
import com.dstwrtv.app.ui.components.DasturTheme

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
    if (channels.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("لا توجد قنوات توافق الفلترة أو معايير البحث", color = DasturTheme.TextMuted, fontSize = 12.sp)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = bottomPadding.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
        ) {
            items(channels) { channel ->
                val isFav = favorites.any { it.url == channel.url }
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
