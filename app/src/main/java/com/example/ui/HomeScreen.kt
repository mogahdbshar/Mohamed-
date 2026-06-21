package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Channel
import com.example.viewmodel.MainViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════
// DSTWR TV — Beautiful Premium Dark Design System
// Colors mapped exactly from user specifications
// ═══════════════════════════════════════════════
object DasturTheme {
    val PureBlack = Color(0xFF050511)      // Colors.bg
    val SurfaceDark = Color(0xFF12121D)    // Colors.card
    val SecondaryDark = Color(0xFF1C1C28)  // Colors.secondary
    val BorderSoft = Color(0xFF2A2A3D)     // Colors.border
    val PrimaryRed = Color(0xFFE11D48)     // Colors.primary
    val AccentAmber = Color(0xFFF59E0B)    // Colors.accent
    val AccentGold = Color(0xFFD4AF37)     // Colors.gold
    val TextMain = Color(0xFFF8F9FA)       // Colors.text
    val TextMuted = Color(0xFF8D93A5)      // Colors.textMuted
}

data class NewsItem(
    val id: Int,
    val title: String,
    val source: String,
    val time: String,
    val cat: String
)

val NEWS_ITEMS = listOf(
    NewsItem(1, "انطلاق دوري أبطال أوروبا بمفاجآت كبيرة ومستويات غير مسبوقة", "beIN Sports", "منذ 15 دقيقة", "رياضة"),
    NewsItem(2, "قمة الذكاء الاصطناعي لعام 2026 تنعقد تحت شعار الابتكار المستدام", "DSTWR Tech", "منذ ساعة", "تكنولوجيا"),
    NewsItem(3, "تحديثات مرتقبة على خوادم البث المباشر لتوفير جودة 8K الموفرة للبيانات", "إدارة التطبيق", "منذ ساعتين", "تقني"),
    NewsItem(4, "الريال يستعد لمواجهة مصيرية لحسم اللقب المحلي الليلة", "Sky News", "منذ 3 ساعات", "رياضة"),
    NewsItem(5, "إطلاق باقة أفلام جديدة بجودة UHD حصرياً لمشتركي الفئة الفضية والذهبية", "DSTWR Cinema", "منذ 5 ساعات", "ترفيه")
)

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
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

    // Control individual Bouquet click detail page
    var activeBouquetDetail by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(3200) // 3.2 seconds matching React splash phase timeouts
        showSplash = false
        if (!com.example.util.NetworkUtils.isInternetAvailable(context)) {
            snackbarHostState.showSnackbar(
                message = "لا يوجد اتصال بالإنترنت. يتم تصفح البث من الذاكرة المحلية والاحتياطية",
                duration = SnackbarDuration.Short
            )
        }
    }

    if (showSplash) {
        SplashView()
    } else {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(14.dp)),
                        containerColor = DasturTheme.SurfaceDark,
                        contentColor = DasturTheme.TextMain,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = data.visuals.message, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            bottomBar = {
                if (!isFullscreen) {
                    DasturBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { 
                            currentTab = it 
                            activeBouquetDetail = null // reset detail view as user navigates tabs
                        }
                    )
                }
            },
            containerColor = DasturTheme.PureBlack,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DasturTheme.PureBlack)
                    .padding(innerPadding)
            ) {
                // Header (Not shown in Fullscreen mode)
                if (!isFullscreen) {
                    DasturHeader(
                        currentTab = currentTab,
                        onActionClick = { 
                            currentTab = "settings" 
                            activeBouquetDetail = null
                        }
                    )
                }

                // Global Interactive Live Player component
                if (selectedChannel != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        VideoPlayer(
                            url = selectedChannel!!.url,
                            isFullscreen = isFullscreen,
                            onFullscreenToggle = { isFullscreen = !isFullscreen },
                            onClose = {
                                viewModel.selectChannel(null)
                                isFullscreen = false
                            },
                            modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                        )

                        if (!isFullscreen) {
                            // Active playback metadata display
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(DasturTheme.PrimaryRed, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "البث النشط المباشر",
                                            color = DasturTheme.PrimaryRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = selectedChannel!!.name,
                                            color = DasturTheme.TextMain,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val isFav = favorites.any { it.url == selectedChannel!!.url }
                                    IconButton(
                                        onClick = { viewModel.toggleFavorite(selectedChannel!!) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Тفضيل",
                                            tint = if (isFav) DasturTheme.AccentAmber else DasturTheme.TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel.selectChannel(null) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "إغلاق",
                                            tint = DasturTheme.TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (!isFullscreen) {
                    when (currentTab) {
                        "home" -> HomeView(
                            channels = channels,
                            selectedChannel = selectedChannel,
                            isLoading = isLoading,
                            syncError = syncError,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onChannelSelect = { channel ->
                                if (!com.example.util.NetworkUtils.isInternetAvailable(context)) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("لا يوجد اتصال بالإنترنت. قد يفشل تحميل البث المباشر")
                                    }
                                }
                                viewModel.selectChannel(channel)
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            favorites = favorites,
                            onSwitchTab = { tab, bouquetName ->
                                currentTab = tab
                                activeBouquetDetail = bouquetName
                            }
                        )
                        "channels" -> ChannelsView(
                            channels = channels,
                            selectedChannel = selectedChannel,
                            onChannelSelect = { viewModel.selectChannel(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            favorites = favorites,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) }
                        )
                        "bouquets" -> BouquetsView(
                            channels = channels,
                            selectedChannel = selectedChannel,
                            onChannelSelect = { viewModel.selectChannel(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            favorites = favorites,
                            activeBouquetDetail = activeBouquetDetail,
                            onSelectBouquet = { activeBouquetDetail = it },
                            onBackToGrid = { activeBouquetDetail = null }
                        )
                        "favorites" -> FavoritesView(
                            favorites = favorites,
                            selectedChannel = selectedChannel,
                            onChannelSelect = { viewModel.selectChannel(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) }
                        )
                        "settings" -> SettingsView(
                            onRefreshList = { viewModel.syncFromNetwork() },
                            isLoading = isLoading,
                            favoritesCount = favorites.size,
                            totalChannelsCount = channels.size
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SplashView() {
    var splashPhase by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(400)
        splashPhase = 1
        delay(800)
        splashPhase = 2
        delay(1000)
        splashPhase = 3
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DasturTheme.PureBlack),
        contentAlignment = Alignment.Center
    ) {
        // High fidelity background elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(DasturTheme.PrimaryRed.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Elegant brand indicator logo container
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(DasturTheme.SurfaceDark)
                    .border(1.5.dp, DasturTheme.PrimaryRed, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = com.example.R.drawable.dstwr_logo_asset_1781909924808,
                    contentDescription = "Mohammed Al-Dastour Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = splashPhase >= 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DSTWR",
                            color = DasturTheme.TextMain,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TV",
                            color = DasturTheme.PrimaryRed,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "منصة البث المباشر المتكاملة",
                        color = DasturTheme.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Smooth linear loading progress emulation
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
            ) {
                val progressWidth = when (splashPhase) {
                    0 -> 0.1f
                    1 -> 0.4f
                    2 -> 0.8f
                    else -> 1f
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressWidth)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber)
                            ),
                            RoundedCornerShape(2.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (splashPhase) {
                    0 -> "جاري تهيئة النظام..."
                    1 -> "جاري مزامنة وترتيب قنوات البث..."
                    else -> "مستقر وجاهز للتشغيل..."
                },
                color = DasturTheme.TextMuted.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun DasturHeader(currentTab: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DasturTheme.PureBlack)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(DasturTheme.SecondaryDark, CircleShape)
                    .border(1.dp, DasturTheme.PrimaryRed, CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = com.example.R.drawable.dstwr_logo_asset_1781909924808,
                    contentDescription = "DSTWR TV Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DSTWR",
                color = DasturTheme.TextMain,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "TV",
                color = DasturTheme.PrimaryRed,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        IconButton(
            onClick = onActionClick,
            modifier = Modifier
                .size(34.dp)
                .background(DasturTheme.SurfaceDark, CircleShape)
                .border(1.dp, DasturTheme.BorderSoft, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "الإعدادات",
                tint = DasturTheme.TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

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
    var showPrompt by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 24.dp)
    ) {
        // Featured Cinema Banner (Hero display)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0F0B24), Color(0xFF050511))
                        )
                    )
            ) {
                // Background artistic aura
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(DasturTheme.PrimaryRed.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(DasturTheme.AccentAmber, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "البث الرياضي الأول",
                                color = DasturTheme.AccentAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(DasturTheme.PrimaryRed, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("HD LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Column {
                        Text(
                            text = "beIN SPORTS HD 1",
                            color = DasturTheme.TextMain,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "مباشر الآن: دوري أبطال أوروبا ومنافسات كروية نارية",
                            color = DasturTheme.TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val featured = channels.firstOrNull { it.name.contains("beIN", ignoreCase = true) }
                                    ?: channels.firstOrNull()
                                if (featured != null) {
                                    onChannelSelect(featured)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.PrimaryRed),
                            contentPadding = PaddingValues(start = 14.dp, top = 4.dp, end = 14.dp, bottom = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("شاهد البث الرياضي", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مستقر وسلس", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Search Input Bar
        item {
            Box(modifier = Modifier.padding(vertical = 4.dp)) {
                DasturSearchBar(searchQuery = searchQuery, onSearchChange = onSearchChange)
            }
        }

        // Active notification promotion strip
        if (showPrompt) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(DasturTheme.SurfaceDark, RoundedCornerShape(10.dp))
                        .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .clickable { showPrompt = false },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = DasturTheme.AccentAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("الخدمة البريميوم المتكاملة", color = DasturTheme.TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("استمتع بأفلامك بجودة ultra وفي أي وقت بدون تقطيع", color = DasturTheme.TextMuted, fontSize = 10.sp)
                    }
                    Icon(Icons.Default.Close, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(14.dp))
                }
            }
        }

        if (isLoading && channels.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DasturTheme.PrimaryRed)
                }
            }
        } else {
            // Group and map local channels to show the 3 key Home Bouquets (beIN Sports, OSN Network, MBC Group)
            val bouquetsOnHome = listOf(
                Pair("باقة قنوات beIN Sports العربية", "bein"),
                Pair("باقة قنوات OSN الترفيهية", "osn"),
                Pair("باقة قنوات MBC الكاملة", "mbc")
            )

            bouquetsOnHome.forEach { (bouquetName, bouquetId) ->
                val groupChannels = channels.filter { it.category.contains(bouquetId, ignoreCase = true) || it.category == bouquetName }
                if (groupChannels.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(vertical = 12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(14.dp)
                                            .background(DasturTheme.PrimaryRed, RoundedCornerShape(1.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = bouquetName,
                                        color = DasturTheme.TextMain,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "الكل",
                                    color = DasturTheme.PrimaryRed,
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
                                items(groupChannels.take(5)) { channel ->
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
    }
}

@Composable
fun DasturSearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("البحث عن باقات القنوات وسيرفر البث المباشر...", color = DasturTheme.TextMuted, fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp)
            .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DasturTheme.SurfaceDark,
            unfocusedContainerColor = DasturTheme.SurfaceDark,
            disabledContainerColor = DasturTheme.SurfaceDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DasturTheme.TextMain,
            unfocusedTextColor = DasturTheme.TextMain
        ),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "بحث", tint = DasturTheme.TextMuted, modifier = Modifier.size(16.dp))
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "مسح", tint = DasturTheme.TextMuted, modifier = Modifier.size(14.dp))
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
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(134.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isActive) {
                        listOf(DasturTheme.SecondaryDark, DasturTheme.SecondaryDark.copy(alpha = 0.85f))
                    } else {
                        listOf(Color(0x33FFFFFF).copy(alpha = 0.08f), Color(0x11000000).copy(alpha = 0.45f))
                    }
                )
            )
            .clickable(onClick = onSelect)
            .border(
                border = if (isActive) {
                    BorderStroke(1.5.dp, Brush.linearGradient(listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber)))
                } else {
                    BorderStroke(1.dp, Color(0x1F2A2A3D))
                },
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .background(Color(0x4007070F)),
                contentAlignment = Alignment.Center
            ) {
                // Async logo load
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

                // Interactive heart toggle
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isActive) {
                        listOf(DasturTheme.SecondaryDark, DasturTheme.SecondaryDark.copy(alpha = 0.75f))
                    } else {
                        listOf(Color(0x1FFFFFFF).copy(alpha = 0.05f), Color(0x3012121D).copy(alpha = 0.3f))
                    }
                )
            )
            .clickable(onClick = onSelect)
            .border(
                border = if (isActive) {
                    BorderStroke(1.2.dp, Brush.linearGradient(listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber)))
                } else {
                    BorderStroke(1.dp, Color(0x1F2A2A3D))
                },
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Logo on the Right (Arabic RTL Layout is natural here)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
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

            // Channel Details (Name, Category, Status Badge)
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
                    // Category Badge
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
                    
                    // Live Glow Badge
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

            // Controls on the Left (Favorite heart, Select indicator)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "التفضيل",
                        tint = if (isFavorite) DasturTheme.AccentAmber else Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isActive) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "قيد التشغيل",
                        tint = DasturTheme.PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

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
    val filters = listOf(
        Pair("all", "الكل"),
        Pair("bein", "beIN"),
        Pair("osn", "OSN"),
        Pair("mbc", "MBC"),
        Pair("movies", "أفلام"),
        Pair("kids", "أطفال"),
        Pair("news", "أخبار")
    )

    val currentFiltered = remember(channels, activeFilterId) {
        if (activeFilterId == "all") {
            channels
        } else {
            channels.filter { ch ->
                when (activeFilterId) {
                    "bein" -> ch.name.contains("beIN", ignoreCase = true) || ch.category.contains("beIN", ignoreCase = true)
                    "osn" -> ch.name.contains("OSN", ignoreCase = true) || ch.category.contains("OSN", ignoreCase = true)
                    "mbc" -> ch.name.contains("MBC", ignoreCase = true) || ch.category.contains("MBC", ignoreCase = true)
                    "movies" -> ch.category.contains("أفلام", ignoreCase = true) || ch.category.contains("Movies", ignoreCase = true) || ch.name.contains("Aflam", ignoreCase = true) || ch.name.contains("Cinema", ignoreCase = true)
                    "kids" -> ch.category.contains("أطفال", ignoreCase = true) || ch.category.contains("kids", ignoreCase = true) || ch.name.contains("Spacetoon", ignoreCase = true)
                    "news" -> ch.category.contains("أخبار", ignoreCase = true) || ch.category.contains("news", ignoreCase = true) || ch.name.contains("الجزيرة", ignoreCase = true) || ch.name.contains("العربية", ignoreCase = true)
                    else -> true
                }
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
                Text("القنوات البث المباشر", color = DasturTheme.TextMain, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${currentFiltered.size} قناة متوفرة", color = DasturTheme.TextMuted, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal tags filter line
        LazyRow(
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(filters) { (id, label) ->
                val isActive = activeFilterId == id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isActive) DasturTheme.PrimaryRed else DasturTheme.SurfaceDark)
                        .clickable { activeFilterId = id }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(
                            1.dp,
                            if (isActive) DasturTheme.PrimaryRed else DasturTheme.BorderSoft,
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        DasturSearchBar(searchQuery = searchQuery, onSearchChange = onSearchChange)

        Spacer(modifier = Modifier.height(10.dp))

        if (currentFiltered.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("لا توجد قنوات توافق الفلترة أو معايير البحث", color = DasturTheme.TextMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentFiltered) { channel ->
                    val isFav = favorites.any { it.url == channel.url }
                    ChannelVerticalRow(
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

@Composable
fun BouquetsView(
    channels: List<Channel>,
    selectedChannel: Channel?,
    onChannelSelect: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit,
    favorites: List<Channel>,
    activeBouquetDetail: String?,
    onSelectBouquet: (String) -> Unit,
    onBackToGrid: () -> Unit
) {
    val bouquetsList = listOf(
        Pair("باقة قنوات beIN Sports العربية", "bein"),
        Pair("باقة قنوات OSN الترفيهية", "osn"),
        Pair("باقة قنوات MBC الكاملة", "mbc"),
        Pair("باقة أفلام ومسلسلات نتفليكس", "movies"),
        Pair("باقة قنوات الأطفال والكرتون", "kids"),
        Pair("باقة الأخبار والبرامج السياسية", "news")
    )

    if (activeBouquetDetail != null) {
        // Detailed Bouquet Item List View
        val bouquetChannels = remember(channels, activeBouquetDetail) {
            channels.filter { it.category == activeBouquetDetail || it.category.contains(activeBouquetDetail.split(" ").getOrNull(1) ?: "XYZ", ignoreCase = true) }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToGrid,
                    modifier = Modifier
                        .size(34.dp)
                        .background(DasturTheme.SurfaceDark, CircleShape)
                        .border(1.dp, DasturTheme.BorderSoft, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(activeBouquetDetail, color = DasturTheme.TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("${bouquetChannels.size} قناة متاحة", color = DasturTheme.TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (bouquetChannels.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("لا توجد قنوات مدرجة تحت هذه الباقة حالياً", color = DasturTheme.TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bouquetChannels) { channel ->
                        val isFav = favorites.any { it.url == channel.url }
                        ChannelVerticalRow(
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
    } else {
        // Main Bouquets Grid View
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text("باقات البث التلفزيوني الموحد", color = DasturTheme.TextMain, fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text("اختر تصنيف الباقة المفضلة للانتقال الفوري لقنواتها", color = DasturTheme.TextMuted, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(bouquetsList) { (bouquetName, indexId) ->
                    val gradient = when (indexId) {
                        "bein" -> Brush.verticalGradient(colors = listOf(Color(0xFF8A1538), Color(0xFF4A0B1D)))
                        "osn" -> Brush.verticalGradient(colors = listOf(DasturTheme.PrimaryRed, Color(0xFF881337)))
                        "mbc" -> Brush.verticalGradient(colors = listOf(Color(0xFF1E3A8A), Color(0xFF172554)))
                        "movies" -> Brush.verticalGradient(colors = listOf(Color(0xFF312E81), Color(0xFF1E1B4B)))
                        "kids" -> Brush.verticalGradient(colors = listOf(Color(0xFFF59E0B), Color(0xFF92400E)))
                        else -> Brush.verticalGradient(colors = listOf(Color(0xFF475569), Color(0xFF1E293B)))
                    }

                    val channelCount = remember(channels) {
                        channels.filter { it.category == bouquetName || it.category.contains(indexId, ignoreCase = true) }.size
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(gradient)
                            .clickable { onSelectBouquet(bouquetName) }
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.28f), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("$channelCount قناة", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = bouquetName.replace("باقة قنوات ", ""),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesView(
    favorites: List<Channel>,
    selectedChannel: Channel?,
    onChannelSelect: (Channel) -> Unit,
    onToggleFavorite: (Channel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text("قنواتي المفضلة", color = DasturTheme.TextMain, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Text("قنوات البث التلفزيوني المفضلة لديك للوصول السريع إليها", color = DasturTheme.TextMuted, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(DasturTheme.PrimaryRed.copy(alpha = 0.08f), CircleShape)
                            .border(1.dp, DasturTheme.PrimaryRed.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "المفضلة فارغة",
                            tint = DasturTheme.PrimaryRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "قائمتك المفضلة فارغة حالياً",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تصفح القنوات والمس أيقونة القلب لإضافة أي قناة بث مفضلة وستظهر هنا فوراً.",
                        color = DasturTheme.TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(favorites) { channel ->
                    ChannelVerticalRow(
                        channel = channel,
                        isFavorite = true,
                        isActive = selectedChannel?.url == channel.url,
                        onSelect = { onChannelSelect(channel) },
                        onToggleFavorite = { onToggleFavorite(channel) }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsView(
    onRefreshList: () -> Unit,
    isLoading: Boolean,
    favoritesCount: Int,
    totalChannelsCount: Int
) {
    val context = LocalContext.current
    var activeSubPage by remember { mutableStateOf<String?>(null) }
    
    // Custom settings options state
    var useHardwareAcceleration by remember { mutableStateOf(true) }
    var ambientGlowEnabled by remember { mutableStateOf(true) }
    var forceWidescreen by remember { mutableStateOf(false) }
    var customM3uUrl by remember { mutableStateOf("https://raw.githubusercontent.com/.../system_config.dat") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        if (activeSubPage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { activeSubPage = null },
                    modifier = Modifier
                        .size(34.dp)
                        .background(DasturTheme.SurfaceDark, CircleShape)
                        .border(1.dp, DasturTheme.BorderSoft, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when(activeSubPage) {
                        "server" -> "إعدادات السيرفر والمصدر"
                        "quality" -> "خيارات العرض والسينما"
                        "privacy" -> "سياسة الخصوصية والأمان"
                        "about" -> "عن تطبيق DSTWR TV"
                        else -> "تفاصيل الإعدادات"
                    },
                    color = DasturTheme.TextMain,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when(activeSubPage) {
                "server" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DasturTheme.SurfaceDark)
                            .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("رابط ملف القنوات الخاص بك (M3U / M3U8)", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("يمكنك تخصيص مصدر قنوات البث لتضمين قائمتك المخصصة مباشرة في التطبيق.", color = DasturTheme.TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            TextField(
                                value = customM3uUrl,
                                onValueChange = { customM3uUrl = it },
                                placeholder = { Text("أدخل رابط m3u هنا...", color = DasturTheme.TextMuted, fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(8.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = DasturTheme.SecondaryDark,
                                    unfocusedContainerColor = DasturTheme.SecondaryDark,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Button(
                                onClick = { 
                                    activeSubPage = null
                                    onRefreshList()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.PrimaryRed),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("حفظ وتحديث القنوات والمصدر", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                "quality" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DasturTheme.SurfaceDark, RoundedCornerShape(12.dp))
                                .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("وضع السينما والإضاءة المحيطة", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("تفعيل هالة توهج لينة خلف مشغل الفيديو لتحسين الرؤية", color = DasturTheme.TextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = ambientGlowEnabled,
                                onCheckedChange = { ambientGlowEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = DasturTheme.PrimaryRed, checkedTrackColor = DasturTheme.PrimaryRed.copy(alpha = 0.4f))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DasturTheme.SurfaceDark, RoundedCornerShape(12.dp))
                                .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("فك ترميز الهاردوير (تسريع الأجهزة)", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("استخدام المعالج الرسومي للجهاز لتقليل استهلاك البطارية والسخونة", color = DasturTheme.TextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = useHardwareAcceleration,
                                onCheckedChange = { useHardwareAcceleration = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = DasturTheme.PrimaryRed, checkedTrackColor = DasturTheme.PrimaryRed.copy(alpha = 0.4f))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DasturTheme.SurfaceDark, RoundedCornerShape(12.dp))
                                .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("فرض ملء الشاشة بنسبة 16:9", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("تعديل أبعاد قنوات البث التلفزيوني تلقائياً لمنع ظهور البار الأسود", color = DasturTheme.TextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = forceWidescreen,
                                onCheckedChange = { forceWidescreen = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = DasturTheme.PrimaryRed, checkedTrackColor = DasturTheme.PrimaryRed.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
                "privacy" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DasturTheme.SurfaceDark)
                            .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("خصوصية وأمان تطبيق DSTWR TV", color = DasturTheme.TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "نحن في DSTWR TV نولي أهمية قصوى لخصوصيتك. جميع عمليات معالجة وحفظ البيانات والاشتراكات والمفضلة تتم محلياً ومباشرة في جهازك، ولا نقوم برفع أو نسخ أي بيانات تتعلق بسجل مشاهدتك وقنواتك المفضلة إلى أي خوادم خارجية.\n\nإن نظام تصفية العائلات والحاجز الأخلاق مفعل تلقائياً ونشط بالكامل ولا يمكن تعطيله لضمان بيئة عائلية آمنة في جميع الأوقات.",
                                color = DasturTheme.TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                "about" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DasturTheme.SurfaceDark)
                            .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("حول منصة DSTWR TV", color = DasturTheme.TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "تطبيق DSTWR TV هو المنصة الترفيهية الفاخرة والأولى لبث القنوات الرياضية والترفيهية والسينمائية المباشرة.\n\n" +
                                "مطور خصيصاً ليقدم تجربة سينمائية زجاجية متكاملة تليق بتطلعات المستخدم العربي.\n\n" +
                                "إصدار التطبيق الحالي: v2.5.0 Premium Ultra\n" +
                                "حقوق الملكية الفكرية والعمل محفوظة © محمد الدستور 2026.",
                                color = DasturTheme.TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Premium Profile Card (Mohammed Al-Dastour!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x22FFFFFF),
                                Color(0x05FFFFFF)
                            )
                        )
                    )
                    .border(
                        border = BorderStroke(1.2.dp, Brush.linearGradient(listOf(DasturTheme.PrimaryRed.copy(alpha = 0.5f), Color(0x1F2A2A3D)))),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(DasturTheme.PrimaryRed.copy(alpha = 0.15f), CircleShape)
                            .border(1.5.dp, DasturTheme.PrimaryRed, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Safe Async image load using local beautiful fallback
                        AsyncImage(
                            model = com.example.R.drawable.dstwr_logo_asset_1781909924808,
                            contentDescription = "Mohammed Al-Dastour Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "محمد الدستور",
                        color = DasturTheme.TextMain,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "مطور ومؤسس تطبيق DSTWR TV",
                        color = DasturTheme.AccentAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ds.r6"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.SecondaryDark),
                            border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(start = 14.dp, top = 4.dp, end = 14.dp, bottom = 4.dp)
                        ) {
                            Text("انستغرام", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@dastur.tv"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.SecondaryDark),
                            border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(start = 14.dp, top = 4.dp, end = 14.dp, bottom = 4.dp)
                        ) {
                            Text("الدعم الفني", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("قائمة الإعدادات وسيرفر البث", color = DasturTheme.TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Sync / Refresh card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(onClick = onRefreshList),
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = DasturTheme.PrimaryRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("مزامنة وتنزيل قنوات البث", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$totalChannelsCount قناة في الذاكرة الحالية", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    if (isLoading) {
                        CircularProgressIndicator(color = DasturTheme.PrimaryRed, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // 2. M3U Server connection setup
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { activeSubPage = "server" },
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = DasturTheme.AccentAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("إعدادات السيرفر والمصدر", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("تعديل روابط باقات تشغيل قنوات M3U يدوياً", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            // 3. Playback customizer options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { activeSubPage = "quality" },
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("خيارات العرض والسينما", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("مسرح الإضاءة ومحسّن تسريع الفيديو ومحدد الاقتصاص", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            // 4. Favorites count display card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = DasturTheme.AccentAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("قنواتي المفضلة", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("تم تسجيل $favoritesCount قناة مفلتة ومفضلة", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                }
            }

            // 5. Privacy Policy Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { activeSubPage = "privacy" },
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("سياسة الخصوصية والأمان", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("اتفاقية السرية والأمان وحفظ بيانات باقات قنواتك محلياً", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            // 6. About App Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { activeSubPage = "about" },
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = DasturTheme.PrimaryRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("عن تطبيق DSTWR TV", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("تاريخ الإصدار ومعلومات ملكية وتشغيل المنصة سينمائياً", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            // 7. Family Safety Mode status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = DasturTheme.SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DasturTheme.BorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("حاجز الحماية والتصفية النشط", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("مصفي المحتوى العام والصارم للعائلات مفعَّل بالكامل", color = DasturTheme.TextMuted, fontSize = 10.sp)
                        }
                    }
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun DasturBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        color = Color(0xC512121D),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.2.dp, Brush.linearGradient(listOf(Color(0x22FFFFFF), Color(0x06FFFFFF)))),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("home", "الرئيسية", Icons.Default.Home),
                Triple("channels", "القنوات", Icons.Default.PlayArrow),
                Triple("bouquets", "الباقات", Icons.Default.Menu),
                Triple("favorites", "المفضلة", Icons.Default.Favorite),
                Triple("settings", "الإعدادات", Icons.Default.Settings)
            )

            tabs.forEach { (tabId, label, icon) ->
                val isActive = currentTab == tabId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isActive) DasturTheme.PrimaryRed.copy(alpha = 0.12f) else Color.Transparent)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isActive) DasturTheme.PrimaryRed.copy(alpha = 0.25f) else Color.Transparent
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onTabSelected(tabId) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isActive) DasturTheme.PrimaryRed else DasturTheme.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            color = if (isActive) Color.White else DasturTheme.TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
