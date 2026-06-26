package com.dstwrtv.app.ui.channels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.ui.components.DSTWRTheme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BouquetsGridView(
    channels: List<Channel>,
    bouquetsList: List<String>,
    onSelectBouquet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (bouquetsList.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = DSTWRTheme.PrimaryRed)
                Spacer(modifier = Modifier.height(10.dp))
                Text("جاري معالجة وتصنيف القنوات...", color = DSTWRTheme.TextMuted, fontSize = 12.sp)
            }
        }
    } else {
        // Pre-calculate channel counts by category to prevent O(N * M) scans during scroll/render
        val channelCounts = remember(channels) {
            channels.groupBy { it.category }.mapValues { it.value.size }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = modifier
        ) {
            items(bouquetsList, key = { it }) { bouquetName ->
                val gradient = remember(bouquetName) { getBouquetGradient(bouquetName) }
                val icon = remember(bouquetName) { getBouquetIcon(bouquetName) }
                val channelCount = remember(bouquetName, channelCounts) {
                    channelCounts[bouquetName] ?: 0
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(125.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(gradient)
                        .clickable { onSelectBouquet(bouquetName) }
                        .border(1.2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    // Decorative Icon Background
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                            .alpha(0.12f),
                        tint = Color.White
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("$channelCount قناة", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cleanBouquetName(bouquetName),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

fun getBouquetIcon(bouquetName: String): ImageVector {
    return when {
        bouquetName.contains("Sports") || bouquetName.contains("الرياضية") || bouquetName.contains("beIN") -> Icons.Rounded.SportsSoccer
        bouquetName.contains("News") || bouquetName.contains("الأخبار") || bouquetName.contains("اخبار") -> Icons.Rounded.Newspaper
        bouquetName.contains("Kids") || bouquetName.contains("الأطفال") || bouquetName.contains("كرتون") -> Icons.Rounded.ChildCare
        bouquetName.contains("Movie") || bouquetName.contains("أفلام") || bouquetName.contains("سينما") -> Icons.Rounded.Movie
        bouquetName.contains("Music") || bouquetName.contains("موسيقى") -> Icons.Rounded.MusicNote
        bouquetName.contains("Islamic") || bouquetName.contains("قرآن") || bouquetName.contains("اسلام") -> Icons.Rounded.Mosque
        bouquetName.contains("OSN") || bouquetName.contains("MBC") || bouquetName.contains("شاهد") -> Icons.Rounded.Star
        bouquetName.contains("المضافة") || bouquetName.contains("Added") -> Icons.Rounded.AddCircle
        else -> Icons.Rounded.Tv
    }
}

fun cleanBouquetName(name: String): String {
    return name.replace("باقة قنوات ", "")
        .replace("باقة القنوات ", "")
        .replace("قنوات ", "")
        .replace("باقة ", "")
        .replace("الال", "ال")
        .trim()
}

fun getBouquetGradient(bouquetName: String): Brush {
    return when {
        bouquetName.contains("beIN") -> Brush.verticalGradient(listOf(Color(0xFF8A1538), Color(0xFF4A0B1D)))
        bouquetName.contains("SSC") -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
        bouquetName.contains("OSN") -> Brush.verticalGradient(listOf(DSTWRTheme.PrimaryRed, Color(0xFF881337)))
        bouquetName.contains("MBC") -> Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF172554)))
        bouquetName.contains("نتفليكس") || bouquetName.contains("Netflix") -> Brush.verticalGradient(listOf(Color(0xFF312E81), Color(0xFF1E1B4B)))
        bouquetName.contains("روتانا") -> Brush.verticalGradient(listOf(Color(0xFF047857), Color(0xFF065F46)))
        bouquetName.contains("الأطفال") || bouquetName.contains("kids") -> Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFF92400E)))
        bouquetName.contains("الأخبار") || bouquetName.contains("news") -> Brush.verticalGradient(listOf(Color(0xFF0369A1), Color(0xFF075985)))
        bouquetName.contains("المضافة") -> Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF312E81)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1F2937), Color(0xFF111827)))
    }
}
