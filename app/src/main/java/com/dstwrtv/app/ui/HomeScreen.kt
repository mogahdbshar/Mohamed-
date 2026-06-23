package com.dstwrtv.app.ui

import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.*
import com.dstwrtv.app.ui.home.HomeView
import com.dstwrtv.app.ui.channels.ChannelsView
import com.dstwrtv.app.ui.channels.BouquetsView
import com.dstwrtv.app.ui.favorites.FavoritesView
import com.dstwrtv.app.ui.settings.SettingsView
import com.dstwrtv.app.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("dstwr_prefs", android.content.Context.MODE_PRIVATE) }
    var ambientGlowEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("ambient_glow_enabled", true)) }
    var activeThemeId by remember { mutableStateOf(sharedPrefs.getString("selected_theme_id", "crimson_neon") ?: "crimson_neon") }

    LaunchedEffect(activeThemeId) {
        applyThemeStyle(activeThemeId)
    }

    var currentTab by remember { mutableStateOf("home") }
    var showSplash by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val channels by viewModel.filteredChannels.collectAsState()
    val favorites by viewModel.favoriteChannels.collectAsState()
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val syncError by viewModel.syncError.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var activeBouquetDetail by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedChannel) {
        if (selectedChannel != null) {
            activeBouquetDetail = selectedChannel?.category
            currentTab = "bouquets"
        }
    }

    LaunchedEffect(Unit) {
        delay(2400)
        showSplash = false
        if (!com.dstwrtv.app.util.NetworkUtils.isInternetAvailable(context)) {
            snackbarHostState.showSnackbar(
                message = "لا يوجد اتصال بالإنترنت. يتم تصفح البث من الذاكرة المحلية والاحتياطية",
                duration = SnackbarDuration.Short
            )
        }
    }

    if (showSplash) {
        SplashView()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DasturTheme.PureBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(DasturTheme.PrimaryRed.copy(alpha = 0.08f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(0f, 0f),
                            radius = 1800f
                        )
                    )
            )

            val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = if (selectedChannel != null) 0.12f else 0.03f,
                targetValue = if (selectedChannel != null) 0.28f else 0.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            val currentGlowColor = if (ambientGlowEnabled) getChannelAmbientColor(selectedChannel) else Color(0xFF321A4B)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (ambientGlowEnabled) currentGlowColor.copy(alpha = pulseAlpha) else Color(0x1F321A4B),
                                if (ambientGlowEnabled && selectedChannel != null) currentGlowColor.copy(alpha = pulseAlpha * 0.3f) else Color(0x0602010A),
                                Color.Transparent
                            ),
                            radius = 2800f
                        )
                    )
            )

            Scaffold(
                containerColor = Color.Transparent,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = if (isFullscreen) 0.dp else innerPadding.calculateTopPadding())
                ) {
                    if (!isFullscreen) {
                        DasturHeader(onActionClick = { 
                            currentTab = "settings" 
                            activeBouquetDetail = null
                        })
                    }

                    selectedChannel?.let { activeCh ->
                        Column(modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
                            Box(
                                modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                VideoPlayer(
                                    url = activeCh.url,
                                    channelName = activeCh.name,
                                    isFullscreen = isFullscreen,
                                    onFullscreenToggle = { isFullscreen = !isFullscreen },
                                    onClose = {
                                        viewModel.selectChannel(null)
                                        isFullscreen = false
                                    },
                                    modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                                )
                            }

                            if (!isFullscreen) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                        .background(DasturTheme.SurfaceDark, shape = RoundedCornerShape(16.dp))
                                        .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(16.dp))
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(modifier = Modifier.size(6.dp).background(DasturTheme.PrimaryRed, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("البث النشط المباشر", color = DasturTheme.PrimaryRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(activeCh.name, color = DasturTheme.TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val isFav = favorites.any { it.url == activeCh.url }
                                        IconButton(onClick = { viewModel.toggleFavorite(activeCh) }, modifier = Modifier.size(36.dp)) {
                                            Icon(if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null, tint = if (isFav) DasturTheme.AccentAmber else DasturTheme.TextMuted, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(onClick = { viewModel.selectChannel(null) }, modifier = Modifier.size(36.dp)) {
                                            Icon(Icons.Rounded.Close, null, tint = DasturTheme.TextMuted, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isFullscreen) {
                        Box(modifier = Modifier.weight(1f)) {
                            when (currentTab) {
                                "home" -> HomeView(
                                    channels = channels, selectedChannel = selectedChannel, isLoading = isLoading, syncError = syncError, 
                                    searchQuery = searchQuery, onSearchChange = viewModel::setSearchQuery, onChannelSelect = viewModel::selectChannel, 
                                    onToggleFavorite = viewModel::toggleFavorite, favorites = favorites, onSwitchTab = { tab, bouquet -> currentTab = tab; activeBouquetDetail = bouquet }
                                )
                                "channels" -> ChannelsView(
                                    channels = channels, selectedChannel = selectedChannel, onChannelSelect = viewModel::selectChannel, 
                                    onToggleFavorite = viewModel::toggleFavorite, favorites = favorites, searchQuery = searchQuery, onSearchChange = viewModel::setSearchQuery
                                )
                                "bouquets" -> BouquetsView(
                                    channels = channels, selectedChannel = selectedChannel, onChannelSelect = viewModel::selectChannel, 
                                    onToggleFavorite = viewModel::toggleFavorite, favorites = favorites, activeBouquetDetail = activeBouquetDetail, 
                                    onSelectBouquet = { activeBouquetDetail = it }, onBackToGrid = { activeBouquetDetail = null }
                                )
                                "favorites" -> FavoritesView(favorites = favorites, selectedChannel = selectedChannel, onChannelSelect = viewModel::selectChannel, onToggleFavorite = viewModel::toggleFavorite)
                                "settings" -> SettingsView(
                                    onRefreshList = { url, onCompleted -> viewModel.selectChannel(null); viewModel.syncFromNetwork(url, onResult = onCompleted) }, 
                                    isLoading = isLoading, favoritesCount = favorites.size, totalChannelsCount = channels.size, ambientGlowEnabled = ambientGlowEnabled, 
                                    onAmbientGlowChange = { ambientGlowEnabled = it; sharedPrefs.edit().putBoolean("ambient_glow_enabled", it).apply() }, 
                                    activeThemeId = activeThemeId, onThemeChange = { activeThemeId = it; sharedPrefs.edit().putString("selected_theme_id", it).apply() }, 
                                    onShowNotification = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } }
                                )
                            }
                        }
                    }
                }
            }

            if (!isFullscreen) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                    DasturBottomNavigation(currentTab = currentTab, onTabSelected = { currentTab = it; activeBouquetDetail = null })
                }
            }

            Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 16.dp).align(Alignment.TopCenter)) {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    val isFail = data.visuals.message.contains("فشل") || data.visuals.message.contains("خطأ") || data.visuals.message.contains("error") || data.visuals.message.contains("مشكلة")
                    val isWarn = data.visuals.message.contains("لا يوجد") || data.visuals.message.contains("تعذر") || data.visuals.message.contains("تأكد")
                    val isSuccess = data.visuals.message.contains("بنجاح") || data.visuals.message.contains("تم")
                    val accentCol = when { isFail -> Color(0xFFEF4444); isWarn -> DasturTheme.PrimaryRed; isSuccess -> Color(0xFF10B981); else -> DasturTheme.AccentAmber }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(listOf(Color(0xFA07070A), Color(0xEE12121A))),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.2.dp,
                                color = accentCol.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(34.dp).background(accentCol.copy(alpha = 0.12f), CircleShape).border(1.dp, accentCol.copy(alpha = 0.35f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(when { isFail -> Icons.Rounded.Close; isWarn -> Icons.Rounded.Warning; isSuccess -> Icons.Rounded.Check; else -> Icons.Rounded.Info }, null, tint = accentCol, modifier = Modifier.size(16.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(when { isFail -> "تنبيه خطأ النظام"; isWarn -> "إشعار وتنبيه"; isSuccess -> "تمت العملية بنجاح"; else -> "تنبيه من DSTWR TV" }, color = accentCol, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(data.visuals.message, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
