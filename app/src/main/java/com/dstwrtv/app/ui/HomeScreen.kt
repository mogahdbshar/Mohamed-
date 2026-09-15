package com.dstwrtv.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dstwrtv.app.ui.components.*
import com.dstwrtv.app.ui.home.HomeView
import com.dstwrtv.app.ui.channels.ChannelsView
import com.dstwrtv.app.ui.channels.BouquetsView
import com.dstwrtv.app.ui.favorites.FavoritesView
import com.dstwrtv.app.ui.settings.SettingsView
import com.dstwrtv.app.ui.player.components.ActiveChannelPlayerSection
import com.dstwrtv.app.viewmodel.MainViewModel
import com.dstwrtv.app.streaming.ui.StreamingView
import com.dstwrtv.app.streaming.ui.StreamingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: MainViewModel, isInPipMode: Boolean = false) {
    val context = LocalContext.current
    val remoteConfigManager = remember { viewModel.remoteConfigManager }
    val sharedPrefs = remember { context.getSharedPreferences("dstwr_prefs", android.content.Context.MODE_PRIVATE) }
    var ambientGlowEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("ambient_glow_enabled", true)) }
    var activeThemeId by remember { mutableStateOf(sharedPrefs.getString("selected_theme_id", "crimson_gold") ?: "crimson_gold") }
    val channels by viewModel.filteredChannels.collectAsState(); val favorites by viewModel.favoriteChannels.collectAsState(); val selectedChannel by viewModel.selectedChannel.collectAsState(); val isLoading by viewModel.isLoading.collectAsState(); val syncError by viewModel.syncError.collectAsState(); val searchQuery by viewModel.searchQuery.collectAsState(); val configUpdated by viewModel.configUpdated.collectAsState()
    val streamingViewModel: StreamingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var currentTab by remember { mutableStateOf("home") }; var showSplash by remember { mutableStateOf(true) }; var showOnboarding by remember(remoteConfigManager.showOnboardingAlways) { mutableStateOf(!sharedPrefs.getBoolean("is_onboarded_v2_new", false) || remoteConfigManager.showOnboardingAlways) }; var isFullscreen by remember { mutableStateOf(false) }; var vodUrl by remember { mutableStateOf("") }; var vodTitle by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }; val coroutineScope = rememberCoroutineScope(); var activeBouquetDetail by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(activeThemeId, selectedChannel) { applyThemeStyle(activeThemeId); if (activeThemeId == "dynamic_chameleon") { DSTWRTheme.PrimaryRed = getChannelAmbientColor(selectedChannel); DSTWRTheme.AccentAmber = Color(0xFFFAFAFA); DSTWRTheme.PureBlack = Color(0xFF030305); DSTWRTheme.SurfaceDark = Color(0x1AFFFFFF) } }
    LaunchedEffect(selectedChannel) { if (selectedChannel != null) { activeBouquetDetail = selectedChannel?.category; currentTab = "bouquets" } }
    LaunchedEffect(Unit) { delay(2400); showSplash = false; if (!com.dstwrtv.app.core.util.NetworkUtils.isInternetAvailable(context)) snackbarHostState.showSnackbar("لا يوجد اتصال بالإنترنت. يتم تصفح البث من الذاكرة المحلية والاحتياطية", duration = SnackbarDuration.Short) }

    if (showSplash) SplashView() else RemoteControlProtectionOverlay(config = remoteConfigManager, configUpdated = configUpdated) {
        if (showOnboarding) OnboardingView(viewModel = viewModel, onComplete = { showOnboarding = false; coroutineScope.launch { snackbarHostState.showSnackbar("تمت مزامنة وتفعيل الباقات بنجاح!") } }) else {
            Box(Modifier.fillMaxSize().background(DSTWRTheme.PureBlack)) {
                Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(DSTWRTheme.PrimaryRed.copy(alpha = .08f), Color.Transparent), center = androidx.compose.ui.geometry.Offset(0f, 0f), radius = 1800f)))
                val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow"); val pulseAlpha by infiniteTransition.animateFloat(if (selectedChannel != null || vodUrl.isNotBlank()) .12f else .03f, if (selectedChannel != null || vodUrl.isNotBlank()) .28f else .08f, infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse"); val currentGlowColor = if (ambientGlowEnabled) getChannelAmbientColor(selectedChannel) else Color(0xFF321A4B)
                Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(if (ambientGlowEnabled) currentGlowColor.copy(alpha = pulseAlpha) else Color(0x1F321A4B), if (ambientGlowEnabled && selectedChannel != null) currentGlowColor.copy(alpha = pulseAlpha * .3f) else Color(0x0602010A), Color.Transparent), radius = 2800f)))
                Column(Modifier.fillMaxSize()) {
                    if (!isFullscreen && !isInPipMode) DSTWRHeader(onActionClick = { currentTab = "settings"; activeBouquetDetail = null })
                    if (vodUrl.isNotBlank()) {
                        Column(if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
                            VideoPlayer(url = vodUrl, channelName = vodTitle, isFullscreen = isFullscreen, onFullscreenToggle = { isFullscreen = !isFullscreen }, onClose = { vodUrl = ""; vodTitle = ""; isFullscreen = false }, modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f), isInPipMode = isInPipMode)
                            if (!isFullscreen && !isInPipMode) Text(vodTitle, color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                        }
                    }
                    selectedChannel?.let { activeCh -> if (vodUrl.isBlank()) ActiveChannelPlayerSection(activeCh = activeCh, isFullscreen = isFullscreen, onFullscreenToggle = { isFullscreen = it }, onClose = { viewModel.selectChannel(null); isFullscreen = false }, isFavorite = favorites.any { it.url == activeCh.url }, onToggleFavorite = { viewModel.toggleFavorite(activeCh) }, isInPipMode = isInPipMode) }
                    if (!isFullscreen && !isInPipMode) Box(Modifier.weight(1f)) {
                        when (currentTab) {
                            "home" -> HomeView(channels = channels, selectedChannel = selectedChannel, isLoading = isLoading, syncError = syncError, searchQuery = searchQuery, onSearchChange = viewModel::setSearchQuery, onChannelSelect = viewModel::selectChannel, onToggleFavorite = viewModel::toggleFavorite, favorites = favorites, onSwitchTab = { tab, bouquet -> currentTab = tab; activeBouquetDetail = bouquet })
                            "streaming" -> StreamingView(streamingViewModel, onOpenPlayer = { url, title -> viewModel.selectChannel(null); vodUrl = url; vodTitle = title; currentTab = "streaming" })
                            "channels" -> ChannelsView(channels = channels, selectedChannel = selectedChannel, onChannelSelect = viewModel::selectChannel, onToggleFavorite = viewModel::toggleFavorite, favorites = favorites, searchQuery = searchQuery, onSearchChange = viewModel::setSearchQuery)
                            "bouquets" -> BouquetsView(channels = channels, selectedChannel = selectedChannel, onChannelSelect = viewModel::selectChannel, onToggleFavorite = viewModel::toggleFavorite, favorites = favorites, activeBouquetDetail = activeBouquetDetail, onSelectBouquet = { activeBouquetDetail = it }, onBackToGrid = { activeBouquetDetail = null })
                            "favorites" -> FavoritesView(favorites = favorites, selectedChannel = selectedChannel, onChannelSelect = viewModel::selectChannel, onToggleFavorite = viewModel::toggleFavorite)
                            "settings" -> SettingsView(onRefreshList = { url, onCompleted -> viewModel.selectChannel(null); viewModel.syncFromNetwork(url, bypassCache = true, onResult = onCompleted) }, isLoading = isLoading, favoritesCount = favorites.size, totalChannelsCount = channels.size, ambientGlowEnabled = ambientGlowEnabled, onAmbientGlowChange = { ambientGlowEnabled = it; sharedPrefs.edit().putBoolean("ambient_glow_enabled", it).apply() }, activeThemeId = activeThemeId, onThemeChange = { activeThemeId = it; sharedPrefs.edit().putString("selected_theme_id", it).apply() }, onShowNotification = { msg -> coroutineScope.launch { snackbarHostState.showSnackbar(msg) } })
                        }
                    }
                }
                if (!isFullscreen && !isInPipMode) Box(Modifier.align(Alignment.BottomCenter)) { DSTWRBottomNavigation(currentTab = currentTab, onTabSelected = { currentTab = it; activeBouquetDetail = null }) }
                Box(Modifier.fillMaxWidth().statusBarsPadding().padding(top = 16.dp).align(Alignment.TopCenter)) { PremiumSnackbarHost(hostState = snackbarHostState) }
            }
        }
    }
}
