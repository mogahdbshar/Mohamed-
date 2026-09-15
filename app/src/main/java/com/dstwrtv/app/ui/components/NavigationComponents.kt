package com.dstwrtv.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DSTWRHeader(onActionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(30.dp)).background(DSTWRTheme.SurfaceDark).border(
                1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = .2f), Color.White.copy(alpha = .05f))), RoundedCornerShape(30.dp)
            ).padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DstwrLogo(size = 28.dp); Spacer(Modifier.width(10.dp))
                    Text("DSTWR", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black, letterSpacing = .8.sp)
                    Text("TV", color = DSTWRTheme.PrimaryRed, fontSize = 17.sp, fontWeight = FontWeight.Black, letterSpacing = .8.sp)
                }
            }
        }
        var focused by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.size(46.dp).scale(if (focused) 1.15f else 1f).onFocusChanged { focused = it.isFocused }
                .clip(CircleShape).background(if (focused) DSTWRTheme.PrimaryRed.copy(alpha = .2f) else DSTWRTheme.SurfaceDark)
                .border(if (focused) 2.dp else 1.dp, Brush.linearGradient(if (focused) listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber) else listOf(Color.White.copy(alpha = .2f), Color.White.copy(alpha = .05f))), CircleShape)
                .clickable(onClick = onActionClick), contentAlignment = Alignment.Center
        ) { Icon(Icons.Rounded.Settings, null, tint = if (focused) DSTWRTheme.AccentAmber else Color.White, modifier = Modifier.size(22.dp)) }
    }
}

@Composable
fun DSTWRBottomNavigation(currentTab: String, onTabSelected: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val remoteConfigManager = remember { (context.applicationContext as com.dstwrtv.app.DstwrApplication).remoteConfigManager }
    Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Box(
            Modifier.fillMaxWidth().height(70.dp).background(DSTWRTheme.SurfaceDark, RoundedCornerShape(35.dp)).border(
                1.dp, Brush.verticalGradient(listOf(DSTWRTheme.BorderSoft, DSTWRTheme.BorderSoft.copy(alpha = .2f))), RoundedCornerShape(35.dp)
            ).padding(horizontal = 6.dp), contentAlignment = Alignment.Center
        ) {
            val hidden = remember(remoteConfigManager.hiddenTabs) { remoteConfigManager.hiddenTabs.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() } }
            val tabs = listOf(
                Triple("home", "الرئيسية", Icons.Rounded.Home),
                Triple("streaming", "المشاهدة", Icons.Rounded.Movie),
                Triple("channels", "القنوات", Icons.Rounded.PlayArrow),
                Triple("bouquets", "الباقات", Icons.AutoMirrored.Rounded.List),
                Triple("favorites", "المفضلة", Icons.Rounded.FavoriteBorder),
                Triple("settings", "الإعدادات", Icons.Rounded.Settings)
            ).filter { !hidden.contains(it.first) }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                tabs.forEach { (id, label, icon) ->
                    val active = currentTab == id
                    var focused by remember { mutableStateOf(false) }
                    val highlighted = active || focused
                    val scale by animateFloatAsState(if (focused) 1.2f else if (active) 1.1f else 1f, label = "tabScale")
                    val tint by animateColorAsState(if (highlighted) DSTWRTheme.PrimaryRed else DSTWRTheme.TextMuted.copy(alpha = .7f), label = "tabTint")
                    Box(
                        Modifier.scale(scale).onFocusChanged { focused = it.isFocused }.clip(RoundedCornerShape(16.dp))
                            .background(if (highlighted) DSTWRTheme.PrimaryRed.copy(alpha = .12f) else Color.Transparent)
                            .clickable { onTabSelected(id) }.padding(horizontal = 8.dp, vertical = 7.dp), contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(if (id == "favorites" && active) Icons.Rounded.Favorite else icon, label, tint = tint, modifier = Modifier.size(23.dp))
                            AnimatedVisibility(active, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(Modifier.height(2.dp)); Text(label, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                    Spacer(Modifier.height(3.dp)); Box(Modifier.size(width = 12.dp, height = 3.dp).background(Brush.horizontalGradient(listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber)), RoundedCornerShape(2.dp)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
