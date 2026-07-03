package com.dstwrtv.app.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.components.DSTWRTheme
import com.dstwrtv.app.ui.components.ChannelCardCompact

import com.dstwrtv.app.ui.components.DSTWRSearchBar

@Composable
fun HomeView(
    channels: List<Channel>,
    selectedChannel: Channel?,
    isLoading: Boolean,
    syncError: String?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onChannelSelect: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    favorites: List<Channel>,
    onSwitchTab: (String, String?) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val remoteConfigManager = remember { (context.applicationContext as com.dstwrtv.app.DstwrApplication).remoteConfigManager }

    val dynamicGroups = remember(channels) {
        channels.groupBy { it.category }.toList()
            .filter { it.first.lowercase() != "dev-hidden" }
            .sortedWith(compareByDescending<Pair<String, List<Channel>>> { 
                val name = it.first.lowercase()
                when {
                    name.contains("رياض") || name.contains("sport") -> 100
                    name.contains("عرب") && (name.contains("رياض") || name.contains("sport")) -> 110
                    name.contains("مسلسل") || name.contains("series") -> 80
                    name.contains("عرب") -> 70
                    else -> 0
                }
            }.thenByDescending { it.second.size })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 120.dp)
    ) {
        if (selectedChannel == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(DSTWRTheme.SurfaceDark, DSTWRTheme.PureBlack)
                            )
                        )
                        .border(BorderStroke(1.2.dp, DSTWRTheme.BorderSoft), RoundedCornerShape(26.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.2f), Color.Transparent),
                                    radius = 800f
                                )
                            )
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(DSTWRTheme.AccentAmber, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تطبيق DSTWR TV", color = DSTWRTheme.AccentAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مرحباً بك",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "استكشف محتوى البث المباشر المتميز وتصفح جميع القنوات المتاحة الآن.",
                            color = DSTWRTheme.TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { onSwitchTab("channels", null) },
                            colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.PrimaryRed),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ابدأ المشاهدة الآن", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        
        item {
            DSTWRSearchBar(searchQuery = searchQuery, onSearchChange = onSearchChange)
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (remoteConfigManager.enableAds && (remoteConfigManager.customAdDisplayLocation == "home" || remoteConfigManager.customAdDisplayLocation == "both")) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.25f), DSTWRTheme.SurfaceDark)
                            )
                        )
                        .border(BorderStroke(1.dp, DSTWRTheme.BorderSoft), RoundedCornerShape(16.dp))
                        .clickable {
                            try {
                                val urlToOpen = remoteConfigManager.customAdClickUrl.ifBlank { "https://t.me/your_telegram_channel" }
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(urlToOpen))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DSTWRTheme.AccentAmber)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("إعلان ممول", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "اشترك في الباقات المميزة أو زر موقع الشريك الرسمي لتجربة خالية من التقطيع وبجودة فائقة!",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = DSTWRTheme.AccentAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        if (dynamicGroups.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF111216))
                        .border(BorderStroke(1.dp, DSTWRTheme.BorderSoft), RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = DSTWRTheme.PrimaryRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (remoteConfigManager.hideAllChannels) "تنبيه من الإدارة" else "لا توجد قنوات متوفرة",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (remoteConfigManager.hideAllChannels) "تم إيقاف عرض القنوات مؤقتاً لأسباب تقنية أو صيانة مجدولة. يرجى الانتظار أو مراجعة الدعم الفني." else "لم يتم العثور على أي قنوات تناسب خيارات البحث أو الفلاتر الحالية.",
                            color = DSTWRTheme.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        items(dynamicGroups, key = { it.first }) { (bouquetName, groupChannels) ->
            if (groupChannels.isNotEmpty()) {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bouquetName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "الكل",
                            color = DSTWRTheme.PrimaryRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onSwitchTab("bouquets", bouquetName) }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = groupChannels.take(10),
                            key = { it.url }
                        ) { channel ->
                            val isFav = favorites.any { it.url == channel.url }
                            ChannelCardCompact(
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
    }
}
