package com.dstwrtv.app.ui.channels

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.channels.components.CategoryFilterRow
import com.dstwrtv.app.ui.channels.components.ChannelList
import com.dstwrtv.app.ui.channels.components.cleanBouquetName
import com.dstwrtv.app.ui.components.DasturTheme
import com.dstwrtv.app.ui.components.DasturSearchBar

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
                Pair(it, cleanBouquetName(it))
            }
        
        staticFilters.addAll(dynamicGroups)
        staticFilters
    }

    val currentFiltered = remember(channels, activeFilterId, searchQuery) {
        val categoryFiltered = if (activeFilterId == "all") {
            channels
        } else {
            channels.filter { ch -> ch.category == activeFilterId }
        }

        if (searchQuery.isBlank()) {
            categoryFiltered
        } else {
            val norm = com.dstwrtv.app.util.ArabicUtils.normalize(searchQuery)
            val terms = norm.split(" ").filter { it.isNotBlank() }
            
            categoryFiltered.filter { ch ->
                val targetName = com.dstwrtv.app.util.ArabicUtils.normalize(ch.name)
                val targetCat = com.dstwrtv.app.util.ArabicUtils.normalize(ch.category)
                terms.all { targetName.contains(it) || targetCat.contains(it) }
            }
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

        CategoryFilterRow(
            filters = filters,
            activeFilterId = activeFilterId,
            onFilterSelect = { activeFilterId = it }
        )

        DasturSearchBar(searchQuery = searchQuery, onSearchChange = onSearchChange)

        Spacer(modifier = Modifier.height(10.dp))

        ChannelList(
            channels = currentFiltered,
            selectedChannel = selectedChannel,
            favorites = favorites,
            onChannelSelect = onChannelSelect,
            onToggleFavorite = onToggleFavorite,
            modifier = Modifier.weight(1f)
        )
    }
}
