package com.dstwrtv.app.ui.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.channels.components.BouquetsGridView
import com.dstwrtv.app.ui.channels.components.ChannelList
import com.dstwrtv.app.ui.components.DSTWRTheme

import com.dstwrtv.app.ui.components.DSTWRSearchBar

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
    var query by androidx.compose.runtime.mutableStateOf("")
    
    val bouquetsList = remember(channels, query) {
        val priorityList = listOf(
            "beIN Sports العربية", "SSC الرياضية", "أبوظبي الرياضية", "الكأس الرياضية",
            "شاهد", "OSN الترفيهية", "MBC", "روتانا", "أفلام ومسلسلات مختارة",
            "الأخبار والسياسة", "الوثائقية", "الأطفال والكرتون", "العربية العامة",
            "الأحداث الرياضية المباشرة", "المضافة", "العالمية"
        )
        val allBouquets = channels.map { it.category }
            .distinct()
            .filter { it.isNotBlank() }
        
        val filtered = if (query.isBlank()) {
            allBouquets
        } else {
            val norm = com.dstwrtv.app.core.util.ArabicUtils.normalize(query)
            val terms = norm.split(" ").filter { it.isNotBlank() }
            allBouquets.filter { bouquet ->
                val target = com.dstwrtv.app.core.util.ArabicUtils.normalize(bouquet)
                terms.all { target.contains(it) }
            }
        }

        filtered.sortedWith { a, b ->
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
                        .background(DSTWRTheme.SurfaceDark, CircleShape)
                        .border(1.dp, DSTWRTheme.BorderSoft, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(activeBouquetDetail, color = DSTWRTheme.TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("${bouquetChannels.size} قناة متاحة", color = DSTWRTheme.TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ChannelList(
                channels = bouquetChannels,
                selectedChannel = selectedChannel,
                favorites = favorites,
                onChannelSelect = onChannelSelect,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text("باقات البث التلفزيوني الموحد", color = DSTWRTheme.TextMain, fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text("اختر تصنيف الباقة المفضلة للانتقال الفوري لقنواتها", color = DSTWRTheme.TextMuted, fontSize = 11.sp)
            }

            DSTWRSearchBar(searchQuery = query, onSearchChange = { query = it })

            Spacer(modifier = Modifier.height(14.dp))

            BouquetsGridView(
                channels = channels,
                bouquetsList = bouquetsList,
                onSelectBouquet = onSelectBouquet,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
