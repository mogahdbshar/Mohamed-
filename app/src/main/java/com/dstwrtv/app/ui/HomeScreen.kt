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
import com.dstwrtv.app.ui.player.components.ActiveChannelPlayerSection
import com.dstwrtv.app.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: MainViewModel, isInPipMode: Boolean = false) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("dstwr_prefs", android.content.Context.MODE_PRIVATE) }
    var ambientGlowEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("ambient_glow_enabled", true)) }
    var activeThemeId by remember { mutableStateOf(sharedPrefs.getString("selected_theme_id", "crimson_gold") ?: "crimson_gold") }

    val channels by viewModel.filteredChannels.collectAsState()
    val favorites by viewModel.favoriteChannels.collectAsState()
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val syncError by viewModel.syncError.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(activeThemeId, selectedChannel) {
        applyThemeStyle(activeThemeId)
        if (activeThemeId == "dynamic_chameleon") {
            val dynamicColor = getChannelAmbientColor(selectedChannel)
            DSTWRTheme.PrimaryRed = dynamicColor
            DSTWRTheme.AccentAmber = Color(0xFFFAFAFA)
            DSTWRTheme.PureBlack = Color(0xFF030305)
            DSTWRTheme.SurfaceDark = Color(0x1AFFFFFF)
        }
    }

    var currentTab by remember { mutableStateOf("home") }
    var showSplash by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        if (!com.dstwrtv.app.core.util.NetworkUtils.isInternetAvailable(context)) {
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
                .background(DSTWRTheme.PureBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(DSTWRTheme.PrimaryRed.copy(alpha = 0.08f), Color.Transparent),
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

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = if (isFullscreen || isInPipMode) 0.dp else 0.dp,
                            bottom = 0.dp
                        )
                ) {
                    if (!isFullscreen && !isInPipMode) {
                        DSTWRHeader(onActionClick = { 
                            currentTab = "settings" 
                            activeBouquetDetail = null
                        })
                    }

                    selectedChannel?.let { activeCh ->
                        val isFav = favorites.any { it.url == activeCh.url }
                        ActiveChannelPlayerSection(
                            activeCh = activeCh,
                            isFullscreen = isFullscreen,
                            onFullscreenToggle = { isFullscreen = it },
                            onClose = {
                                viewModel.selectChannel(null)
                                isFullscreen = false
                            },
                            isFavorite = isFav,
                            onToggleFavorite = { viewModel.toggleFavorite(activeCh) },
                            isInPipMode = isInPipMode
                        )
                    }

                    if (!isFullscreen && !isInPipMode) {
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
                
                if (!isFullscreen && !isInPipMode) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        DSTWRBottomNavigation(currentTab = currentTab, onTabSelected = { currentTab = it; activeBouquetDetail = null })
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 16.dp).align(Alignment.TopCenter)) {
                PremiumSnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}
