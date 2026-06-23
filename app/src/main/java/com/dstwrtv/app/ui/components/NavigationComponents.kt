package com.dstwrtv.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DasturHeader(onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.horizontalGradient(listOf(DasturTheme.SurfaceDark, Color.Transparent)))
                .border(
                    BorderStroke(1.dp, Brush.linearGradient(listOf(DasturTheme.BorderSoft, Color.Transparent))),
                    RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DstwrLogo(size = 30.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "DSTWR",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        "TV",
                        color = DasturTheme.PrimaryRed,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(DasturTheme.SurfaceDark)
                .border(BorderStroke(1.2.dp, DasturTheme.BorderSoft), CircleShape)
                .clickable { onActionClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Settings, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DasturBottomNavigation(currentTab: String, onTabSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xE60D0D14), Color(0xFB05050A))),
                    RoundedCornerShape(22.dp)
                )
                .border(1.2.dp, DasturTheme.BorderSoft, RoundedCornerShape(22.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    Triple("home", "الرئيسية", Icons.Rounded.Home),
                    Triple("channels", "القنوات", Icons.Rounded.PlayArrow),
                    Triple("bouquets", "الباقات", Icons.Rounded.List),
                    Triple("favorites", "المفضلة", Icons.Rounded.FavoriteBorder),
                    Triple("settings", "الإعدادات", Icons.Rounded.Settings)
                )
                tabs.forEach { (tabId, label, icon) ->
                    val isActive = currentTab == tabId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isActive) DasturTheme.PrimaryRed.copy(alpha = 0.16f) else Color.Transparent)
                            .border(
                                BorderStroke(1.dp, if (isActive) DasturTheme.PrimaryRed.copy(alpha = 0.35f) else Color.Transparent),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onTabSelected(tabId) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (tabId == "favorites" && isActive) Icons.Rounded.Favorite else icon,
                                label,
                                tint = if (isActive) DasturTheme.PrimaryRed else DasturTheme.TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    label,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
