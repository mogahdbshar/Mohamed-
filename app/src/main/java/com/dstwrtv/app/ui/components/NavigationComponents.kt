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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DSTWRHeader(onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(DSTWRTheme.SurfaceDark)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DstwrLogo(size = 28.dp)
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
                        color = DSTWRTheme.PrimaryRed,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
        var isFocused by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .size(46.dp)
                .scale(if (isFocused) 1.15f else 1.0f)
                .onFocusChanged { isFocused = it.isFocused }
                .clip(CircleShape)
                .background(if (isFocused) DSTWRTheme.PrimaryRed.copy(alpha = 0.2f) else DSTWRTheme.SurfaceDark)
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    brush = Brush.linearGradient(
                        colors = if (isFocused) {
                            listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber)
                        } else {
                            listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        }
                    ),
                    shape = CircleShape
                )
                .clickable { onActionClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Settings, null, tint = if (isFocused) DSTWRTheme.AccentAmber else Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun DSTWRBottomNavigation(currentTab: String, onTabSelected: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val remoteConfigManager = remember { (context.applicationContext as com.dstwrtv.app.DstwrApplication).remoteConfigManager }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(
                    color = DSTWRTheme.SurfaceDark, // Translucent glass effect
                    shape = RoundedCornerShape(34.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DSTWRTheme.BorderSoft,
                            DSTWRTheme.BorderSoft.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(34.dp)
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hiddenTabsList = remember(remoteConfigManager.hiddenTabs) {
                    remoteConfigManager.hiddenTabs.split(",")
                        .map { it.trim().lowercase() }
                        .filter { it.isNotBlank() }
                }

                val tabs = listOf(
                    Triple("home", "الرئيسية", Icons.Rounded.Home),
                    Triple("channels", "القنوات", Icons.Rounded.PlayArrow),
                    Triple("bouquets", "الباقات", Icons.AutoMirrored.Rounded.List),
                    Triple("favorites", "المفضلة", Icons.Rounded.FavoriteBorder),
                    Triple("settings", "الإعدادات", Icons.Rounded.Settings)
                ).filter { (tabId, _, _) -> !hiddenTabsList.contains(tabId) }
                
                tabs.forEach { (tabId, label, icon) ->
                    val isActive = currentTab == tabId
                    var isFocused by remember { mutableStateOf(false) }
                    val isHighlighted = isActive || isFocused
                    
                    val scaleFactor by animateFloatAsState(
                        targetValue = if (isFocused) 1.25f else (if (isActive) 1.15f else 1.0f),
                        animationSpec = spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                        ),
                        label = "tabScale"
                    )

                    val tintColor by animateColorAsState(
                        targetValue = if (isHighlighted) DSTWRTheme.PrimaryRed else DSTWRTheme.TextMuted.copy(alpha = 0.7f),
                        animationSpec = spring(stiffness = androidx.compose.animation.core.Spring.StiffnessLow),
                        label = "tabTint"
                    )

                    val glowColor by animateColorAsState(
                        targetValue = if (isHighlighted) DSTWRTheme.PrimaryRed.copy(alpha = 0.15f) else Color.Transparent,
                        animationSpec = spring(stiffness = androidx.compose.animation.core.Spring.StiffnessLow),
                        label = "tabGlow"
                    )

                    Box(
                        modifier = Modifier
                            .scale(scaleFactor)
                            .onFocusChanged { isFocused = it.isFocused }
                            .clip(RoundedCornerShape(18.dp))
                            .background(glowColor)
                            .border(
                                width = 1.dp,
                                color = if (isHighlighted) DSTWRTheme.PrimaryRed.copy(alpha = 0.35f) else Color.Transparent,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable(
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                indication = androidx.compose.foundation.LocalIndication.current
                            ) { 
                                onTabSelected(tabId) 
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (tabId == "favorites" && isActive) Icons.Rounded.Favorite else icon,
                                label,
                                tint = tintColor,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            // Smoothly animate text label appearance
                            AnimatedVisibility(
                                visible = isActive,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        label,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    // Premium active indicator dot
                                    Box(
                                        modifier = Modifier
                                            .size(width = 12.dp, height = 3.dp)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber)
                                                ),
                                                shape = RoundedCornerShape(1.5.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
