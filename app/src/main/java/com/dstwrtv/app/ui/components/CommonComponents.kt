package com.dstwrtv.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.model.Channel
import coil.compose.AsyncImage

// ═══════════════════════════════════════════════
// DSTWR TV — Beautiful Premium Dark Design System
// ═══════════════════════════════════════════════
object DasturTheme {
    var PureBlack by mutableStateOf(Color(0xFF030305))      // Absolute luxury deep black
    var SurfaceDark by mutableStateOf(Color(0x1AFFFFFF))    // Semi-translucent glass (approx 10% white)
    var SecondaryDark by mutableStateOf(Color(0x0EFFFFFF))  // Sub-layer glass (approx 5% white)
    var BorderSoft by mutableStateOf(Color(0x2BFFFFFF))     // Elegant glass edge refraction (17% white)
    var PrimaryRed by mutableStateOf(Color(0xFFE50914))     // Iconic Cinematic Red
    var AccentAmber by mutableStateOf(Color(0xFFFFD700))     // Classic 24K Gold
    val TextMain = Color(0xFFFAFAFA)                        // Crisp Modern White
    val TextMuted = Color(0xFF9CA3AF)                       // Sophisticated Slate Muted
}

fun applyThemeStyle(themeId: String) {
    when (themeId) {
        "noir_diamond" -> {
            DasturTheme.PureBlack = Color(0xFF010103)
            DasturTheme.PrimaryRed = Color(0xFFE50914)
            DasturTheme.AccentAmber = Color(0xFFFFD700)
            DasturTheme.SurfaceDark = Color(0x1F22222E)
            DasturTheme.SecondaryDark = Color(0x0EFFFFFF)
            DasturTheme.BorderSoft = Color(0x3DFFFFFF)
        }
        "indigo_sapphire" -> {
            DasturTheme.PureBlack = Color(0xFF050814)
            DasturTheme.PrimaryRed = Color(0xFF6366F1)
            DasturTheme.AccentAmber = Color(0xFF60E5FF)
            DasturTheme.SurfaceDark = Color(0x1DFFFFFF)
            DasturTheme.SecondaryDark = Color(0x12FFFFFF)
            DasturTheme.BorderSoft = Color(0x35FFFFFF)
        }
        "midnight_velvet" -> {
            DasturTheme.PureBlack = Color(0xFF0A0208)
            DasturTheme.PrimaryRed = Color(0xFFF43F5E)
            DasturTheme.AccentAmber = Color(0xFFFB923C)
            DasturTheme.SurfaceDark = Color(0x22FFFFFF)
            DasturTheme.SecondaryDark = Color(0x15FFFFFF)
            DasturTheme.BorderSoft = Color(0x3AFFFFFF)
        }
        "emerald_onyx" -> {
            DasturTheme.PureBlack = Color(0xFF020604)
            DasturTheme.PrimaryRed = Color(0xFF10B981)
            DasturTheme.AccentAmber = Color(0xFFD9F99D)
            DasturTheme.SurfaceDark = Color(0x19FFFFFF)
            DasturTheme.SecondaryDark = Color(0x0DFFFFFF)
            DasturTheme.BorderSoft = Color(0x2AFFFFFF)
        }
        "carbon_platinum" -> {
            DasturTheme.PureBlack = Color(0xFF0B0B0D)
            DasturTheme.PrimaryRed = Color(0xFFFACC15)
            DasturTheme.AccentAmber = Color(0xFFE2E8F0)
            DasturTheme.SurfaceDark = Color(0x1AFFFFFF)
            DasturTheme.SecondaryDark = Color(0x0CFFFFFF)
            DasturTheme.BorderSoft = Color(0x2CFFFFFF)
        }
    }
}

fun getChannelAmbientColor(channel: com.dstwrtv.app.model.Channel?): Color {
    if (channel == null) return Color(0xFF673AB7)
    val cat = channel.category.lowercase()
    val name = channel.name.lowercase()
    
    return when {
        cat.contains("news") || cat.contains("أخبار") || cat.contains("اخبار") || name.contains("الجزيرة") || name.contains("العربية") || name.contains("news") -> Color(0xFF03A9F4)
        cat.contains("sport") || cat.contains("رياضة") || cat.contains("الرياضية") || name.contains("bein") || name.contains("الكأس") -> Color(0xFF00E676)
        cat.contains("islam") || cat.contains("قرأن") || cat.contains("قرآن") || cat.contains("قران") || cat.contains("دعوة") || name.contains("المجد") || name.contains("سنة") -> Color(0xFFFFD54F)
        cat.contains("kids") || cat.contains("أطفال") || cat.contains("atfal") || name.contains("طيور") || name.contains("براعم") -> Color(0xFFF06292)
        cat.contains("cinema") || cat.contains("أفلام") || cat.contains("سينما") || cat.contains("دراما") || name.contains("mbc") || name.contains("روتانا") -> Color(0xFFE50914)
        else -> {
            val hash = channel.name.hashCode()
            val colors = listOf(Color(0xFFE50914), Color(0xFF9C27B0), Color(0xFF03A9F4), Color(0xFF00E676), Color(0xFFFFB703), Color(0xFFE040FB))
            colors[Math.abs(hash) % colors.size]
        }
    }
}

@Composable
fun DstwrLogo(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.233f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.dstwrtv.app.R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.dstwrtv.app.R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().scale(1.3f),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
    }
}

@Composable
fun DasturSearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val focusColor = if (isFocused) DasturTheme.PrimaryRed else Color.Transparent
    
    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("ابحث عن القنوات، المسلسلات، أو الباقات...", color = DasturTheme.TextMuted, fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(54.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                1.2.dp,
                if (isFocused) Brush.linearGradient(listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber)) else Brush.linearGradient(listOf(Color(0x2BFFFFFF), Color(0x08FFFFFF))),
                RoundedCornerShape(16.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DasturTheme.PureBlack,
            unfocusedContainerColor = DasturTheme.SurfaceDark.copy(alpha = 0.12f),
            disabledContainerColor = DasturTheme.SurfaceDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DasturTheme.TextMain,
            unfocusedTextColor = DasturTheme.TextMain,
            cursorColor = DasturTheme.PrimaryRed
        ),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = "بحث", tint = if (isFocused) DasturTheme.PrimaryRed else DasturTheme.TextMuted, modifier = Modifier.size(20.dp))
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "مسح", tint = DasturTheme.TextMuted, modifier = Modifier.size(18.dp))
                }
            }
        }
    )
}

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

    Box(
        modifier = Modifier
            .width(110.dp)
            .height(134.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = if (isActive) {
                        listOf(DasturTheme.PrimaryRed.copy(alpha = 0.2f), DasturTheme.SecondaryDark.copy(alpha = 0.85f))
                    } else {
                        listOf(Color(0x22FFFFFF), Color(0x05FFFFFF))
                    },
                    radius = 250f
                )
            )
            .clickable {
                coroutineScope.launch {
                    scale.animateTo(0.92f, animationSpec = tween(100))
                    scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                }
                onSelect()
            }
            .border(
                border = if (isActive) {
                    BorderStroke(2.dp, DasturTheme.PrimaryRed)
                } else {
                    BorderStroke(1.dp, DasturTheme.BorderSoft.copy(alpha = 0.6f))
                },
                shape = RoundedCornerShape(16.dp)
            )
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
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = channel.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
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
                            color = if (isActive) DasturTheme.PrimaryRed else DasturTheme.TextMuted,
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
                        tint = if (isFavorite) DasturTheme.AccentAmber else Color.White.copy(alpha = 0.4f),
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
                    color = DasturTheme.TextMain,
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isActive) {
                        listOf(DasturTheme.SecondaryDark, DasturTheme.PrimaryRed.copy(alpha = 0.15f))
                    } else {
                        listOf(Color(0x22FFFFFF), Color(0x05FFFFFF))
                    }
                )
            )
            .clickable(onClick = onSelect)
            .border(
                border = if (isActive) {
                    BorderStroke(1.2.dp, Brush.linearGradient(listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber)))
                } else {
                    BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x05FFFFFF))))
                },
                shape = RoundedCornerShape(16.dp)
            )
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
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = channel.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
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
                            color = if (isActive) DasturTheme.PrimaryRed else DasturTheme.TextMuted,
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
                    color = if (isActive) Color.White else DasturTheme.TextMain,
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
                            color = DasturTheme.TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(DasturTheme.PrimaryRed.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
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
                        tint = if (isFavorite) DasturTheme.AccentAmber else Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isActive) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "قيد التشغيل",
                        tint = DasturTheme.PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
