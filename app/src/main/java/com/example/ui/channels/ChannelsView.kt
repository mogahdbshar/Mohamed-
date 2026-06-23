package com.example.ui.channels

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Channel
import com.example.ui.components.DasturTheme
import com.example.ui.components.DasturSearchBar
import com.example.ui.components.ChannelVerticalRow

@Composable
fun ChannelsView(
    channels: List<Channel>,
    selectedChannel: Channel?,
    onChannelSelect: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    favorites: List<Channel>,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    var activeFilterId by remember { mutableStateOf("all") }
    
    val filters = remember(channels) {
        val staticFilters = mutableListOf(Pair("all", "الكل"))
        val dynamicGroups = channels.map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .map { 
                val cleanLabel = it
                    .replace("باقة قنوات ", "")
                    .replace("باقة القنوات ", "")
                    .replace("قنوات ", "")
                    .replace("باقة ", "")
                    .replace("الال", "ال")
                    .replace(" VIP", "")
                    .replace("VIP ", "")
                    .replace("VIP", "")
                    .trim()
                Pair(it, cleanLabel)
            }
        
        staticFilters.addAll(dynamicGroups)
        staticFilters
    }

    val currentFiltered = remember(channels, activeFilterId) {
        if (activeFilterId == "all") {
            channels
        } else {
            channels.filter { ch -> ch.category == activeFilterId }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("قنوات البث المباشر", color = DasturTheme.TextMain, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${currentFiltered.size} قناة متاحة", color = DasturTheme.TextMuted, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(filters) { (id, label) ->
                val isActive = activeFilterId == id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isActive) DasturTheme.PrimaryRed else DasturTheme.SurfaceDark)
                        .clickable { activeFilterId = id }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(
                            1.dp,
                            if (isActive) DasturTheme.PrimaryRed else DasturTheme.BorderSoft,
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        DasturSearchBar(searchQuery = searchQuery, onSearchChange = onSearchChange)

        Spacer(modifier = Modifier.height(10.dp))

        if (currentFiltered.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("لا توجد قنوات توافق الفلترة أو معايير البحث", color = DasturTheme.TextMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentFiltered) { channel ->
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
}

@Composable
fun BouquetsView(
    channels: List<Channel>,
    selectedChannel: Channel?,
    onChannelSelect: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    favorites: List<Channel>,
    activeBouquetDetail: String?,
    onSelectBouquet: (String) -> Unit,
    onBackToGrid: () -> Unit
) {
    val bouquetsList = remember(channels) {
        val priorityList = listOf(
            "beIN Sports العربية", "SSC الرياضية", "أبوظبي الرياضية", "الكأس الرياضية",
            "شاهد", "OSN الترفيهية", "MBC", "روتانا", "أفلام ومسلسلات مختارة",
            "الأخبار والسياسة", "الوثائقية", "الأطفال والكرتون", "العربية العامة",
            "الأحداث الرياضية المباشرة", "المضافة", "العالمية"
        )
        channels.map { it.category }
            .distinct()
            .filter { it.isNotBlank() }
            .sortedWith { a, b ->
                val indexA = priorityList.indexOfFirst { it.contains(a) || a.contains(it) }
                val indexB = priorityList.indexOfFirst { it.contains(b) || b.contains(it) }
                when {
                    indexA != -1 && indexB != -1 -> indexA.compareTo(indexB)
                    indexA != -1 -> -1
                    indexB != -1 -> 1
                    else -> a.compareTo(b)
                }
            }
    }

    if (activeBouquetDetail != null) {
        val bouquetChannels = remember(channels, activeBouquetDetail) {
            channels.filter { it.category == activeBouquetDetail }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToGrid,
                    modifier = Modifier
                        .size(34.dp)
                        .background(DasturTheme.SurfaceDark, CircleShape)
                        .border(1.dp, DasturTheme.BorderSoft, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(activeBouquetDetail, color = DasturTheme.TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("${bouquetChannels.size} قناة متاحة", color = DasturTheme.TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (bouquetChannels.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("لا توجد قنوات مدرجة تحت هذه الباقة حالياً", color = DasturTheme.TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bouquetChannels) { channel ->
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
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text("باقات البث التلفزيوني الموحد", color = DasturTheme.TextMain, fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text("اختر تصنيف الباقة المفضلة للانتقال الفوري لقنواتها", color = DasturTheme.TextMuted, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (bouquetsList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DasturTheme.PrimaryRed)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("جاري معالجة وتصنيف القنوات...", color = DasturTheme.TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bouquetsList) { bouquetName ->
                        val gradient = when {
                            bouquetName.contains("beIN") -> Brush.verticalGradient(listOf(Color(0xFF8A1538), Color(0xFF4A0B1D)))
                            bouquetName.contains("SSC") -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
                            bouquetName.contains("OSN") -> Brush.verticalGradient(listOf(DasturTheme.PrimaryRed, Color(0xFF881337)))
                            bouquetName.contains("MBC") -> Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF172554)))
                            bouquetName.contains("نتفليكس") || bouquetName.contains("Netflix") -> Brush.verticalGradient(listOf(Color(0xFF312E81), Color(0xFF1E1B4B)))
                            bouquetName.contains("روتانا") -> Brush.verticalGradient(listOf(Color(0xFF047857), Color(0xFF065F46)))
                            bouquetName.contains("الأطفال") || bouquetName.contains("kids") -> Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFF92400E)))
                            bouquetName.contains("الأخبار") || bouquetName.contains("news") -> Brush.verticalGradient(listOf(Color(0xFF0369A1), Color(0xFF075985)))
                            bouquetName.contains("المضافة") -> Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF312E81)))
                            else -> Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827)))
                        }

                        val channelCount = remember(channels, bouquetName) {
                            channels.filter { it.category == bouquetName }.size
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(gradient)
                                .clickable { onSelectBouquet(bouquetName) }
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("$channelCount قناة", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = bouquetName.replace("باقة قنوات ", "").replace("باقة القنوات ", "").replace("قنوات ", "").replace("باقة ", "").replace("الال", "ال"),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
