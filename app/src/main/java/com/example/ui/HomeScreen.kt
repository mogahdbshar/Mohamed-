package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
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
    var PureBlack by mutableStateOf(Color(0xFF07070A))      // Deep luxurious background
    var SurfaceDark by mutableStateOf(Color(0x17FFFFFF))    // Glass card background (approx 9% white)
    var SecondaryDark by mutableStateOf(Color(0x0CFFFFFF))  // Glass secondary container (approx 5% white)
    var BorderSoft by mutableStateOf(Color(0x26FFFFFF))     // Soft glass reflection border (15% white)
    var PrimaryRed by mutableStateOf(Color(0xFFE50914))     // Cinematic premium red
    var AccentAmber by mutableStateOf(Color(0xFFD4AF37))    // Luxury Gold
    val TextMain = Color(0xFFFAFAFA)                        // Crisp White
    val TextMuted = Color(0xFF8E93A3)                       // Elegant muted text
}

fun applyThemeStyle(themeId: String) {
    when (themeId) {
        "crimson_neon" -> {
            DasturTheme.PureBlack = Color(0xFF060609)
            DasturTheme.PrimaryRed = Color(0xFFE50914)      // Cinematic premium red
            DasturTheme.AccentAmber = Color(0xFFFFB703)     // Luxury Gold
            DasturTheme.SurfaceDark = Color(0x17FFFFFF)     // Glass card background
            DasturTheme.SecondaryDark = Color(0x0CFFFFFF)   // Glass secondary container
            DasturTheme.BorderSoft = Color(0x26FFFFFF)      // Soft glass reflection border
        }
        "calm_slate" -> {
            DasturTheme.PureBlack = Color(0xFF090D13)       // Comfort midnight slate grey
            DasturTheme.PrimaryRed = Color(0xFF00B4D8)      // Sky Blue / Ocean active
            DasturTheme.AccentAmber = Color(0xFF90E0EF)     // Cyan / Eye comfort tint
            DasturTheme.SurfaceDark = Color(0x1BFFFFFF)     // High-translucency slate glass
            DasturTheme.SecondaryDark = Color(0x10FFFFFF)
            DasturTheme.BorderSoft = Color(0x2EFFFFFF)
        }
        "royal_gold" -> {
            DasturTheme.PureBlack = Color(0xFF040610)       // Deep luxurious Islamic royal indigo
            DasturTheme.PrimaryRed = Color(0xFFFFD54F)      // Luxury Royal Gold
            DasturTheme.AccentAmber = Color(0xFF00E676)     // Vibrant emerald green accent
            DasturTheme.SurfaceDark = Color(0x1EFFFFFF)     // Rich glass background
            DasturTheme.SecondaryDark = Color(0x11FFFFFF)
            DasturTheme.BorderSoft = Color(0x32FFFFFF)
        }
        "active_emerald" -> {
            DasturTheme.PureBlack = Color(0xFF030804)       // Cyber active sports dark green
            DasturTheme.PrimaryRed = Color(0xFF00E676)      // Vivid neon emerald
            DasturTheme.AccentAmber = Color(0xFFAEEA00)     // Energetic neon lime
            DasturTheme.SurfaceDark = Color(0x16FFFFFF)
            DasturTheme.SecondaryDark = Color(0x0BFFFFFF)
            DasturTheme.BorderSoft = Color(0x25FFFFFF)
        }
    }
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
fun rememberSafeLogoPainter(): Painter? {
    val context = LocalContext.current
    return remember(context) {
        try {
            val drawable = context.resources.getDrawable(com.example.R.drawable.img_app_icon_1782010376813, context.theme)
            if (drawable is BitmapDrawable) {
                BitmapPainter(drawable.bitmap.asImageBitmap())
            } else {
                null
            }
        } catch (e1: Throwable) {
            try {
                val drawable = context.resources.getDrawable(com.example.R.drawable.dstwr_logo_asset_1781909924808, context.theme)
                if (drawable is BitmapDrawable) {
                    BitmapPainter(drawable.bitmap.asImageBitmap())
                } else {
                    null
                }
            } catch (e2: Throwable) {
                null
            }
        }
    }
}

fun getChannelAmbientColor(channel: com.example.model.Channel?): Color {
    if (channel == null) return Color(0xFF673AB7) // Indigo-violet base
    val cat = channel.category.lowercase()
    val name = channel.name.lowercase()
    
    return when {
        cat.contains("news") || cat.contains("أخبار") || cat.contains("اخبار") || name.contains("الجزيرة") || name.contains("العربية") || name.contains("news") -> Color(0xFF03A9F4) // Cyan / news blue
        cat.contains("sport") || cat.contains("رياضة") || cat.contains("الرياضية") || name.contains("bein") || name.contains("الكأس") -> Color(0xFF00E676) // Sports green
        cat.contains("islam") || cat.contains("قرأن") || cat.contains("قرآن") || cat.contains("قران") || cat.contains("دعوة") || name.contains("المجد") || name.contains("سنة") -> Color(0xFFFFD54F) // Islamic gold
        cat.contains("kids") || cat.contains("أطفال") || cat.contains("atfal") || name.contains("طيور") || name.contains("براعم") -> Color(0xFFF06292) // Playful pink
        cat.contains("cinema") || cat.contains("أفلام") || cat.contains("سينما") || cat.contains("دراما") || name.contains("mbc") || name.contains("روتانا") -> Color(0xFFE50914) // Film red
        else -> {
            val hash = channel.name.hashCode()
            val colors = listOf(
                Color(0xFFE50914), // Red
                Color(0xFF9C27B0), // Purple
                Color(0xFF03A9F4), // Light Blue
                Color(0xFF00E676), // Green
                Color(0xFFFFB703), // Orange/Amber
                Color(0xFFE040FB)  // Magenta
            )
            colors[Math.abs(hash) % colors.size]
        }
    }
}

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

    // Control individual Bouquet click detail page
    var activeBouquetDetail by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(2400) // 2.4 seconds for faster entry
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0F0B1E),
                            DasturTheme.PureBlack
                        ),
                        radius = 2200f
                    )
                )
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = if (selectedChannel != null) 0.16f else 0.05f,
                targetValue = if (selectedChannel != null) 0.32f else 0.10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            val currentGlowColor = if (ambientGlowEnabled) {
                getChannelAmbientColor(selectedChannel)
            } else {
                Color(0xFF321A4B) // Subtle static fallback
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
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
                        .padding(
                            top = if (isFullscreen) 0.dp else innerPadding.calculateTopPadding(),
                            bottom = 0.dp
                        )
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
                    androidx.activity.compose.BackHandler(enabled = isFullscreen || selectedChannel != null) {
                        if (isFullscreen) {
                            isFullscreen = false
                        } else {
                            viewModel.selectChannel(null)
                        }
                    }
                    Column(modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
                        val playerGlowColor = getChannelAmbientColor(selectedChannel)

                        Box(
                            modifier = if (isFullscreen) {
                                Modifier.fillMaxSize()
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                if (ambientGlowEnabled) playerGlowColor.copy(alpha = 0.28f) else Color.Transparent,
                                                Color.Transparent
                                            ),
                                            radius = 500f
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                            },
                            contentAlignment = Alignment.Center
                        ) {
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
                        }

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
                                            imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
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
                                            imageVector = Icons.Rounded.Close,
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
                            onRefreshList = { url, onCompleted ->
                                viewModel.syncFromNetwork(url, onResult = onCompleted)
                            },
                            isLoading = isLoading,
                            favoritesCount = favorites.size,
                            totalChannelsCount = channels.size,
                            ambientGlowEnabled = ambientGlowEnabled,
                            onAmbientGlowChange = { enabled ->
                                ambientGlowEnabled = enabled
                                sharedPrefs.edit().putBoolean("ambient_glow_enabled", enabled).apply()
                            },
                            activeThemeId = activeThemeId,
                            onThemeChange = { theme ->
                                activeThemeId = theme
                                sharedPrefs.edit().putString("selected_theme_id", theme).apply()
                            },
                            onShowNotification = { msg ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (!isFullscreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    DasturBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { 
                            currentTab = it 
                            activeBouquetDetail = null // reset detail view as user navigates tabs
                        }
                    )
                }
            }

            // ═══════════════════════════════════════════════════════════════════════════════════════
            // Custom Glassmorphic Top Notification Banner (Dynamic Theme and Status Compatibility)
            // ═══════════════════════════════════════════════════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter)
            ) {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    val isFail = data.visuals.message.contains("فشل") || 
                                 data.visuals.message.contains("خطأ") || 
                                 data.visuals.message.contains("error") || 
                                 data.visuals.message.contains("مشكلة")
                    
                    val isWarn = data.visuals.message.contains("لا يوجد") || 
                                 data.visuals.message.contains("تعذر") || 
                                 data.visuals.message.contains("تأكد")
                                 
                    val isSuccess = data.visuals.message.contains("بنجاح") || 
                                    data.visuals.message.contains("تم")
                    
                    val accentCol = when {
                        isFail -> Color(0xFFEF4444) // Bright Warning Red
                        isWarn -> DasturTheme.PrimaryRed // Theme-Active Accent (Primary Red, Sky Blue, Neon Emerald, Gold)
                        isSuccess -> Color(0xFF10B981) // Action Success Emerald Green
                        else -> DasturTheme.AccentAmber // Secondary Warm Amber Accent Code
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFA07070A), // Extremely crisp opaque glassmorphic slate background for contrast
                                        Color(0xEE12121A)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.2.dp,
                                color = accentCol.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Premium Dual-Circle Glowing Logo Icon Shape
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(accentCol.copy(alpha = 0.12f), CircleShape)
                                    .border(1.dp, accentCol.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isFail -> Icons.Rounded.Close
                                        isWarn -> Icons.Rounded.Warning
                                        isSuccess -> Icons.Rounded.Check
                                        else -> Icons.Rounded.Info
                                    },
                                    contentDescription = "Notification Icon",
                                    tint = accentCol,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when {
                                        isFail -> "تنبيه خطأ النظام"
                                        isWarn -> "إشعار وتنبيه"
                                        isSuccess -> "تمت العملية بنجاح"
                                        else -> "تنبيه من DSTWR TV"
                                    },
                                    color = accentCol,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = data.visuals.message,
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 16.sp
                                )
                            }
                        }
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
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1B0F2A), // Subtle violet/purple glow in center
                        DasturTheme.PureBlack // Fade to pure black
                    ),
                    radius = 2000f
                )
            ),
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
            DstwrLogo(size = 92.dp)

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = splashPhase >= 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
fun DstwrLogo(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.233f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        // Scale the foreground slightly so it has a vibrant, full presence but still matches the icon perfectly
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().scale(1.3f), // Scale up inside the clip safely
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
    }
}

@Composable
fun DasturHeader(currentTab: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & name wrapped in a compact glass capsule that dynamically fits content
        Surface(
            color = Color(0x3B12121E), // Subtle premium translucent base
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0x3DFFFFFF), Color(0x06FFFFFF))))
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(Color(0x15FFFFFF), Color(0x02FFFFFF))))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DstwrLogo(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DSTWR",
                        color = DasturTheme.TextMain,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "TV",
                        color = DasturTheme.PrimaryRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Settings button wrapped in a separate matching glass circle
        IconButton(
            onClick = onActionClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0x3B12121E))
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.linearGradient(listOf(Color(0x3DFFFFFF), Color(0x06FFFFFF)))
                    ),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "الإعدادات",
                tint = Color.White,
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

    // Group and map local channels to show dynamically based on available categories
    val dynamicGroups = remember(channels) {
        channels.groupBy { it.category }
            .filterKeys { it.isNotBlank() }
            .map { (category, list) -> Pair(category, list) }
            .sortedByDescending { it.second.size }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp)
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
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
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
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = DasturTheme.AccentAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("الخدمة البريميوم المتكاملة", color = DasturTheme.TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("استمتع بأفلامك بجودة ultra وفي أي وقت بدون تقطيع", color = DasturTheme.TextMuted, fontSize = 10.sp)
                    }
                    Icon(Icons.Rounded.Close, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(14.dp))
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
            dynamicGroups.forEach { (bouquetName, groupChannels) ->
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
                                    text = bouquetName.replace("باقة قنوات ", "").replace("باقة ", ""),
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
                            items(groupChannels.take(10)) { channel ->
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

@Composable
fun DasturSearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("البحث في القنوات والباقات...", color = DasturTheme.TextMuted, fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp)
            .border(
                1.dp,
                Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x08FFFFFF))),
                RoundedCornerShape(16.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DasturTheme.SecondaryDark,
            unfocusedContainerColor = DasturTheme.SurfaceDark,
            disabledContainerColor = DasturTheme.SurfaceDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DasturTheme.TextMain,
            unfocusedTextColor = DasturTheme.TextMain
        ),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = "بحث", tint = DasturTheme.PrimaryRed, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(Icons.Rounded.Clear, contentDescription = "مسح", tint = DasturTheme.TextMuted, modifier = Modifier.size(18.dp))
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
            .clickable(onClick = onSelect)
            .border(
                border = if (isActive) {
                    BorderStroke(1.5.dp, Brush.linearGradient(listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber)))
                } else {
                    BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x05FFFFFF))))
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
            // Channel Logo on the Right (Arabic RTL Layout is natural here)
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
    
    val filters = remember(channels) {
        val staticFilters = mutableListOf(Pair("all", "الكل"))
        val dynamicGroups = channels.map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .map { Pair(it, it.replace("باقة ", "").replace("قنوات ", "")) }
        
        staticFilters.addAll(dynamicGroups)
        staticFilters
    }

    val currentFiltered = remember(channels, activeFilterId) {
        if (activeFilterId == "all") {
            channels
        } else {
            channels.filter { ch -> ch.category == activeFilterId }
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
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
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
    // Dynamically derive all bouquets and packages directly from actual active channels
    val bouquetsList = remember(channels) {
        val priorityList = listOf(
            "باقة قنوات beIN Sports العربية",
            "باقة قنوات SSC الرياضية",
            "باقة قنوات أبوظبي الرياضية",
            "باقة قنوات الكأس الرياضية",
            "باقة VIP شاهد",
            "باقة قنوات OSN الترفيهية",
            "باقة قنوات MBC الكاملة",
            "باقة قنوات روتانا",
            "باقة أفلام ومسلسلات نتفليكس",
            "باقة الأخبار والبرامج السياسية",
            "باقة القنوات الوثائقية",
            "باقة قنوات الأطفال والكرتون",
            "باقة القنوات العربية العامة",
            "باقة الأحداث الرياضية والبوكسينج",
            "باقة القنوات المضافة",
            "باقة القنوات العالمية",
            "باقة القنوات العالمية الأخرى"
        )
        channels.map { it.category }
            .distinct()
            .filter { it.isNotBlank() }
            .sortedWith { a, b ->
                val indexA = priorityList.indexOf(a)
                val indexB = priorityList.indexOf(b)
                when {
                    indexA != -1 && indexB != -1 -> indexA.compareTo(indexB)
                    indexA != -1 -> -1
                    indexB != -1 -> 1
                    else -> a.compareTo(b)
                }
            }
    }

    if (activeBouquetDetail != null) {
        // Detailed Bouquet Item List View
        val bouquetChannels = remember(channels, activeBouquetDetail) {
            channels.filter { it.category == activeBouquetDetail }
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
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع", tint = Color.White, modifier = Modifier.size(16.dp))
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
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
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

            if (bouquetsList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DasturTheme.PrimaryRed)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("جاري معالجة وتصنيف الباقات الرياضية والسينمائية...", color = DasturTheme.TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bouquetsList) { bouquetName ->
                        val gradient = when {
                            bouquetName.contains("beIN", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF8A1538), Color(0xFF4A0B1D)))
                            bouquetName.contains("SSC", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
                            bouquetName.contains("OSN", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(DasturTheme.PrimaryRed, Color(0xFF881337)))
                            bouquetName.contains("MBC", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF1E3A8A), Color(0xFF172554)))
                            bouquetName.contains("نتفليكس", ignoreCase = true) || bouquetName.contains("Netflix", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF312E81), Color(0xFF1E1B4B)))
                            bouquetName.contains("روتانا", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF047857), Color(0xFF065F46)))
                            bouquetName.contains("الأطفال", ignoreCase = true) || bouquetName.contains("kids", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFFF59E0B), Color(0xFF92400E)))
                            bouquetName.contains("الأخبار", ignoreCase = true) || bouquetName.contains("news", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF0369A1), Color(0xFF075985)))
                            bouquetName.contains("المضافة", ignoreCase = true) || bouquetName.contains("المخصصة", ignoreCase = true) -> Brush.verticalGradient(colors = listOf(Color(0xFF4F46E5), Color(0xFF312E81)))
                            else -> Brush.verticalGradient(colors = listOf(Color(0xFF1F2937), Color(0xFF111827)))
                        }

                        val channelCount = remember(channels, bouquetName) {
                            channels.filter { it.category == bouquetName }.size
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
                                    text = bouquetName.replace("باقة قنوات ", "").replace("باقة ", ""),
                                    color = Color.White,
                                    fontSize = 13.sp,
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
                            imageVector = Icons.Rounded.FavoriteBorder,
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
                contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 110.dp),
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
private fun GlassGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                color = DasturTheme.TextMain,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = DasturTheme.TextMuted,
                    fontSize = 9.5.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DasturTheme.SurfaceDark,
                            DasturTheme.SurfaceDark.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(1.2.dp, DasturTheme.BorderSoft, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun PremiumSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null,
    iconColor: Color = DasturTheme.PrimaryRed
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(iconColor.copy(alpha = 0.12f), CircleShape)
                        .border(1.dp, iconColor.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    color = DasturTheme.TextMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = DasturTheme.TextMuted,
                    fontSize = 9.5.sp,
                    lineHeight = 13.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DasturTheme.PrimaryRed,
                uncheckedThumbColor = DasturTheme.TextMuted,
                uncheckedTrackColor = DasturTheme.SecondaryDark,
                uncheckedBorderColor = DasturTheme.BorderSoft
            )
        )
    }
}

@Composable
private fun PremiumRadioButtonRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (selected) Color.White else DasturTheme.TextMain,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = DasturTheme.TextMuted,
                fontSize = 9.5.sp,
                lineHeight = 13.sp
            )
        }
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = DasturTheme.PrimaryRed,
                unselectedColor = DasturTheme.TextMuted
            )
        )
    }
}

@Composable
private fun PremiumMenuOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, DasturTheme.BorderSoft)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DasturTheme.SurfaceDark,
                            DasturTheme.SurfaceDark.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(iconColor.copy(alpha = 0.12f), CircleShape)
                            .border(1.dp, iconColor.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            color = DasturTheme.TextMain,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            color = DasturTheme.TextMuted,
                            fontSize = 9.5.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
                if (trailingContent != null) {
                    trailingContent()
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = DasturTheme.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsView(
    onRefreshList: (String, (Result<Int>) -> Unit) -> Unit,
    isLoading: Boolean,
    favoritesCount: Int,
    totalChannelsCount: Int,
    ambientGlowEnabled: Boolean,
    onAmbientGlowChange: (Boolean) -> Unit,
    activeThemeId: String,
    onThemeChange: (String) -> Unit,
    onShowNotification: (String) -> Unit
) {
    val context = LocalContext.current
    var activeSubPage by remember { mutableStateOf<String?>(null) }
    
    // Custom settings options state
    var useHardwareAcceleration by remember { @Suppress("MutableStateOfWithMutableStateFlow") mutableStateOf(true) }
    var forceWidescreen by remember { @Suppress("MutableStateOfWithMutableStateFlow") mutableStateOf(false) }
    
    // Retrieve persistent custom URL securely, defaulting to empty
    val sharedPrefs = remember { context.getSharedPreferences("dstwr_prefs", android.content.Context.MODE_PRIVATE) }
    var customM3uUrl by remember { mutableStateOf(sharedPrefs.getString("custom_m3u_url", "") ?: "") }
    
    // Multi Source state
    var sourceMode by remember { mutableStateOf(sharedPrefs.getString("source_mode", "merged") ?: "merged") }
    var showDevPackage by remember { mutableStateOf(sharedPrefs.getBoolean("show_dev_package", true)) }

    // State indicators for loading feedback and messages
    var isSaving by remember { mutableStateOf(false) }
    var syncStatusMessage by remember { mutableStateOf<String?>(null) }
    var syncIsSuccess by remember { mutableStateOf<Boolean?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).imePadding().verticalScroll(rememberScrollState())) {
        if (activeSubPage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        activeSubPage = null 
                        syncStatusMessage = null
                        syncIsSuccess = null
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(DasturTheme.SurfaceDark, CircleShape)
                        .border(1.dp, DasturTheme.BorderSoft, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = when(activeSubPage) {
                        "server" -> "إعدادات السيرفر والمصدر"
                        "sources" -> "إدارة مصادر القنوات"
                        "quality" -> "خيارات العرض والسينما"
                        "privacy" -> "سياسة الخصوصية والأمان"
                        "about" -> "عن تطبيق DSTWR TV"
                        else -> "تفاصيل الإعدادات"
                    },
                    color = DasturTheme.TextMain,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when(activeSubPage) {
                "server" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassGroup(
                            title = "نوع الاتصال ومصدر القنوات",
                            subtitle = "اختر صيغة رابط ملف القنوات (M3U) أو اشتراكك عبر الأبي ميت لـ Xtream"
                        ) {
                            var inputMode by remember { mutableStateOf("m3u") }
                            var xtreamHost by remember { mutableStateOf("") }
                            var xtreamUser by remember { mutableStateOf("") }
                            var xtreamPass by remember { mutableStateOf("") }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DasturTheme.SecondaryDark, RoundedCornerShape(10.dp))
                                        .border(1.2.dp, if (inputMode == "m3u") DasturTheme.PrimaryRed else Color.Transparent, RoundedCornerShape(10.dp))
                                        .clickable { inputMode = "m3u" }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("رابط M3U", color = if (inputMode == "m3u") Color.White else DasturTheme.TextMain, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(DasturTheme.SecondaryDark, RoundedCornerShape(10.dp))
                                        .border(1.2.dp, if (inputMode == "xtream") DasturTheme.PrimaryRed else Color.Transparent, RoundedCornerShape(10.dp))
                                        .clickable { inputMode = "xtream" }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Xtream Codes", color = if (inputMode == "xtream") Color.White else DasturTheme.TextMain, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            HorizontalDivider(color = DasturTheme.BorderSoft.copy(alpha = 0.5f), thickness = 1.dp)

                            if (inputMode == "m3u") {
                                Text("رابط ملف القنوات (M3U / M3U8)", color = DasturTheme.TextMain, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("انسخ والصق رابط قنواتك المخصصة مباشرة تحت لدمجها تلقائياً بالباقات.", color = DasturTheme.TextMuted, fontSize = 9.5.sp)
                                
                                TextField(
                                    value = customM3uUrl,
                                    onValueChange = { customM3uUrl = it },
                                    placeholder = { Text("أدخل رابط m3u الخاص بك هنا...", color = DasturTheme.TextMuted, fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.2.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp)),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = DasturTheme.SecondaryDark,
                                        unfocusedContainerColor = DasturTheme.SecondaryDark,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSaving
                                )
                            } else {
                                Text("بيانات حساب Xtream API", color = DasturTheme.TextMain, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text("أدخل خادم (Host) واسم مستخدم ورمز المرور لاشتراكك المباشر.", color = DasturTheme.TextMuted, fontSize = 9.5.sp)
                                
                                TextField(
                                    value = xtreamHost,
                                    onValueChange = { xtreamHost = it },
                                    placeholder = { Text("مثال: http://iptv-server.com:8080", color = DasturTheme.TextMuted, fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.2.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp)),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = DasturTheme.SecondaryDark, unfocusedContainerColor = DasturTheme.SecondaryDark, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSaving
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TextField(
                                        value = xtreamUser, 
                                        onValueChange = { xtreamUser = it }, 
                                        placeholder = { Text("اسم المستخدم", color = DasturTheme.TextMuted, fontSize = 11.5.sp) }, 
                                        singleLine = true, 
                                        modifier = Modifier.weight(1f).border(1.2.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp)), 
                                        colors = TextFieldDefaults.colors(focusedContainerColor = DasturTheme.SecondaryDark, unfocusedContainerColor = DasturTheme.SecondaryDark, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White), 
                                        shape = RoundedCornerShape(12.dp), 
                                        enabled = !isSaving
                                    )
                                    TextField(
                                        value = xtreamPass, 
                                        onValueChange = { xtreamPass = it }, 
                                        placeholder = { Text("كلمة المرور", color = DasturTheme.TextMuted, fontSize = 11.5.sp) }, 
                                        singleLine = true, 
                                        modifier = Modifier.weight(1f).border(1.2.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp)), 
                                        colors = TextFieldDefaults.colors(focusedContainerColor = DasturTheme.SecondaryDark, unfocusedContainerColor = DasturTheme.SecondaryDark, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White), 
                                        shape = RoundedCornerShape(12.dp), 
                                        enabled = !isSaving
                                    )
                                }
                            }
                            
                            if (syncStatusMessage != null) {
                                val (bgCol, borderCol, textCol) = when (syncIsSuccess) {
                                    true -> Triple(Color(0x1F10B981), Color(0xFF10B981), Color(0xFF34D399))
                                    false -> Triple(Color(0x1FEF4444), Color(0xFFEF4444), Color(0xFFF87171))
                                    null -> Triple(DasturTheme.PrimaryRed.copy(alpha = 0.12f), DasturTheme.PrimaryRed, DasturTheme.PrimaryRed)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bgCol)
                                        .border(1.dp, borderCol.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (syncIsSuccess == null) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = DasturTheme.PrimaryRed
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (syncIsSuccess == true) Icons.Rounded.Check else Icons.Rounded.Warning,
                                                contentDescription = "حالة",
                                                tint = textCol,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = syncStatusMessage ?: "",
                                            color = textCol,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { 
                                        if (inputMode == "xtream") {
                                            if (xtreamHost.isBlank() || xtreamUser.isBlank() || xtreamPass.isBlank()) {
                                                syncIsSuccess = false
                                                syncStatusMessage = "يرجى ملء جميع حقول حساب Xtream (الرابط، المستخدم، كلمة المرور)"
                                                return@Button
                                            }
                                            if (!xtreamHost.startsWith("http://") && !xtreamHost.startsWith("https://")) {
                                                syncIsSuccess = false
                                                syncStatusMessage = "رابط الهوست غير صالح. يجب أن يبدأ بـ http:// أو https://"
                                                return@Button
                                            }
                                            
                                            val cleanHost = xtreamHost.trim().removeSuffix("/")
                                            customM3uUrl = "$cleanHost/get.php?username=${xtreamUser.trim()}&password=${xtreamPass.trim()}&type=m3u_plus&output=ts"
                                        } else {
                                            if (customM3uUrl.isNotBlank() && !customM3uUrl.startsWith("http://") && !customM3uUrl.startsWith("https://")) {
                                                syncIsSuccess = false
                                                syncStatusMessage = "الرابط غير صالح. يرجى إدخال رابط M3U صحيح يبدأ بـ http:// أو https://"
                                                return@Button
                                            }
                                        }
                                        
                                        isSaving = true
                                        syncIsSuccess = null
                                        syncStatusMessage = "جاري التحقق وجلب قنواتك المشتركة لدمجها..."
                                        onRefreshList(customM3uUrl) { result ->
                                            isSaving = false
                                            result.onSuccess { count ->
                                                syncIsSuccess = true
                                                syncStatusMessage = "تم جلب القنوات بنجاح! تم تحميل $count قناة وإضافتها بنجاح."
                                            }.onFailure { err ->
                                                syncIsSuccess = false
                                                syncStatusMessage = "خطأ في الاتصال: ${err.message}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.PrimaryRed),
                                    modifier = Modifier.weight(1.2f).height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSaving
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("جاري الجلب...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("حفظ ومزامنة المصدر", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                if (customM3uUrl.isNotBlank()) {
                                    Button(
                                        onClick = {
                                            isSaving = true
                                            syncIsSuccess = null
                                            syncStatusMessage = "جاري استعادة ملف البث الافتراضي المدمج..."
                                            customM3uUrl = ""
                                            onRefreshList("") { result ->
                                                isSaving = false
                                                result.onSuccess {
                                                    syncIsSuccess = true
                                                    syncStatusMessage = "تمت استعادة مصادر البث وتحديث القنوات الافتراضية بنجاح."
                                                }.onFailure { err ->
                                                    syncIsSuccess = false
                                                    syncStatusMessage = "خطأ: ${err.message}"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.SecondaryDark),
                                        modifier = Modifier.weight(0.8f).height(46.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                                        enabled = !isSaving
                                    ) {
                                        Text("إستعادة الافتراضي", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                "quality" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassGroup(
                            title = "خصائص العرض والمشغل الذكي",
                            subtitle = "خيارات الإضاءة التفاعلية وفك الترميز وحسابات الأبعاد"
                        ) {
                            PremiumSwitchRow(
                                title = "وضع السينما والإضاءة المحيطة",
                                subtitle = "تفعيل هالة توهج لينة خلف مشغل الفيديو للتخفيف من إجهاد العين",
                                checked = ambientGlowEnabled,
                                onCheckedChange = onAmbientGlowChange,
                                icon = Icons.Rounded.Star,
                                iconColor = DasturTheme.AccentAmber
                            )
                            HorizontalDivider(color = DasturTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
                            PremiumSwitchRow(
                                title = "فك ترميز الهاردوير (تسريع الأجهزة)",
                                subtitle = "استخدام المعالج الرسومي للجهاز لتقليل استهلاك البطارية والسخونة",
                                checked = useHardwareAcceleration,
                                onCheckedChange = { useHardwareAcceleration = it },
                                icon = Icons.Rounded.Build,
                                iconColor = DasturTheme.PrimaryRed
                            )
                            HorizontalDivider(color = DasturTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
                            PremiumSwitchRow(
                                title = "فرض ملء الشاشة بنسبة 16:9",
                                subtitle = "مكافحة ظهور الحواف السوداء تلقائياً عبر مطابقة شاشة البث السينمائي",
                                checked = forceWidescreen,
                                onCheckedChange = { forceWidescreen = it },
                                icon = Icons.Rounded.PlayArrow,
                                iconColor = Color(0xFF3B82F6)
                            )
                        }

                        GlassGroup(
                            title = "طابع فخم متكامل (ثيمات المنصة)",
                            subtitle = "قم بتفعيل الطابع الذي يروق لك، وسيتحول كامل نظام الألوان والأضواء والخلفيات بنسبة ١٠٠٪ لتكتمل الفخامة."
                        ) {
                            val isOriginalActive = activeThemeId == "crimson_neon"
                            
                            // 1. Original Classic Cinematic Crimson Theme
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isOriginalActive) DasturTheme.SecondaryDark else Color.Transparent,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .border(
                                        width = if (isOriginalActive) 1.5.dp else 1.dp,
                                        color = if (isOriginalActive) DasturTheme.PrimaryRed else DasturTheme.BorderSoft.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onThemeChange("crimson_neon") }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .background(Color(0xFF060609), RoundedCornerShape(10.dp))
                                        .border(1.dp, DasturTheme.BorderSoft.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                ) {
                                    Box(modifier = Modifier.size(14.dp).background(Color(0xFFE50914), CircleShape))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.size(14.dp).background(Color(0xFFFFB703), CircleShape))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "ثيم التطبيق الأصلي (الافتراضي)",
                                            color = if (isOriginalActive) DasturTheme.PrimaryRed else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isOriginalActive) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(DasturTheme.PrimaryRed.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                                    .border(1.dp, DasturTheme.PrimaryRed, RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("نشط حالياً", color = DasturTheme.PrimaryRed, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "الطابع الكلاسيكي السينمائي الفاخر باللون القرمزي والذهب البراق.",
                                        color = DasturTheme.TextMuted,
                                        fontSize = 9.5.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }

                            val extraThemes = listOf(
                                Triple("calm_slate", "الرمادي الكوزمي الهادئ", "طيف مريح للعين مطعم بدرجات رمادي هادئة وبخ البث السماوي والأزرق"),
                                Triple("royal_gold", "الملكي الذهبي الأندلسي", "رونق أندلسي مخملي يمزج الذهب مع زرقة الليل والأخضر الزمردي اللامع"),
                                Triple("active_emerald", "الأخضر السيبيري الرياضي", "طاقة رنانة وتوهجات خضراء وفسفورية مصممة خصيصاً للمباريات وجماهير الرياضة")
                            )

                            extraThemes.forEach { (tid, tname, tdesc) ->
                                val isSelected = activeThemeId == tid
                                val (colorPri, colorAcc, colorBg) = when(tid) {
                                    "calm_slate" -> Triple(Color(0xFF00B4D8), Color(0xFF90E0EF), Color(0xFF090D13))
                                    "royal_gold" -> Triple(Color(0xFFFFD54F), Color(0xFF00E676), Color(0xFF040610))
                                    "active_emerald" -> Triple(Color(0xFF00E676), Color(0xFFAEEA00), Color(0xFF030804))
                                    else -> Triple(Color(0xFFE50914), Color(0xFFFFB703), Color(0xFF060609))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isSelected) DasturTheme.SecondaryDark else Color.Transparent,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) DasturTheme.PrimaryRed else DasturTheme.BorderSoft.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { 
                                            onThemeChange(if (isSelected) "crimson_neon" else tid) 
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .background(colorBg, RoundedCornerShape(10.dp))
                                            .border(1.dp, DasturTheme.BorderSoft.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        Box(modifier = Modifier.size(14.dp).background(colorPri, CircleShape))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(modifier = Modifier.size(14.dp).background(colorAcc, CircleShape))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = tname,
                                                color = if (isSelected) DasturTheme.PrimaryRed else Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(DasturTheme.PrimaryRed.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                                        .border(1.dp, DasturTheme.PrimaryRed, RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("نشط حالياً", color = DasturTheme.PrimaryRed, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = tdesc,
                                            color = DasturTheme.TextMuted,
                                            fontSize = 9.5.sp,
                                            lineHeight = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = isSelected,
                                        onCheckedChange = { isChecked ->
                                            onThemeChange(if (isChecked) tid else "crimson_neon")
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = DasturTheme.PrimaryRed,
                                            uncheckedThumbColor = DasturTheme.TextMuted,
                                            uncheckedTrackColor = DasturTheme.SecondaryDark,
                                            uncheckedBorderColor = DasturTheme.BorderSoft
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                "privacy" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassGroup(
                            title = "دليل والضمانات الأمنية الكاملة",
                            subtitle = "أمان وسرية المستخدم هي ركيزة التشغيل لمنصة البث"
                        ) {
                            Text(
                                text = "نحن في DSTWR TV نولي أهمية قصوى لخصوصيتك. جميع عمليات معالجة وحفظ البيانات والاشتراكات والمفضلة تتم محلياً ومباشرة في جهازك، ولا نقوم برفع أو نسخ أي بيانات تتعلق بسجل مشاهدتك وقنواتك المفضلة إلى أي خوادم خارجية.\n\nإن نظام تصفية العائلات والحاجز الأخلاق مفعل تلقائياً ونشط بالكامل ولا يمكن تعطيله لضمان بيئة عائلية آمنة في جميع الأوقات.\n\nكلما تشاهد القنوات أو تحدث روابط m3u، فالمزامنة تتم بشكل مجهول ومدرع لحماية خصوصيتك وعنوان بروتوكول الإنترنت بالكامل عن المتلصصين.",
                                color = DasturTheme.TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                "sources" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassGroup(
                            title = "خيارات العرض وطريقة الدمج",
                            subtitle = "حدد الطريقة المفضلة لعرض وتصفية قنوات البث ومطابقتها"
                        ) {
                            val modes = listOf(
                                "merged" to Triple("Merged Mode", "قنوات المطور الافتراضية مع قنواتك المضافة معاً بنظام مدمج سلس", "merged"),
                                "user_only" to Triple("User Only", "قصر البث على قنواتك وملفك المرفوع فقط (إخفاء الافتراضي)", "user_only"),
                                "dev_only" to Triple("Developer Only", "مشاهدة قنوات المطور الافتراضية المدمجة في النظام فقط لحمايتك", "dev_only")
                            )

                            modes.forEachIndexed { idx, (key, info) ->
                                val (mTitle, mSub, mKey) = info
                                PremiumRadioButtonRow(
                                    title = mTitle,
                                    subtitle = mSub,
                                    selected = sourceMode == mKey,
                                    onSelect = {
                                        sourceMode = mKey
                                        sharedPrefs.edit().putString("source_mode", mKey).apply()
                                        onRefreshList(customM3uUrl) {}
                                    }
                                )
                                if (idx < modes.size - 1) {
                                    HorizontalDivider(color = DasturTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
                                }
                            }
                        }

                        GlassGroup(
                            title = "عرض المطور السريع للباقات",
                            subtitle = "إظهار أو حجب الباقات ومحطات المطور من اللوائح بنقرة واحدة"
                        ) {
                            PremiumSwitchRow(
                                title = "تفعيل قنوات وباقات المطور الافتراضية",
                                subtitle = if (showDevPackage) "نشط = يظهر قنوات النظام" else "معطل = يحجب قنوات النظام كاملة ويقتصر على باقتك",
                                checked = showDevPackage,
                                onCheckedChange = { isChecked ->
                                    showDevPackage = isChecked
                                    sharedPrefs.edit().putBoolean("show_dev_package", isChecked).apply()
                                    onRefreshList(customM3uUrl) {}
                                },
                                icon = Icons.Rounded.FavoriteBorder,
                                iconColor = DasturTheme.PrimaryRed
                            )
                        }
                    }
                }
                "about" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassGroup(
                            title = "حقوق وعمل منصة DSTWR TV",
                            subtitle = "منبر إعلامي فريد عائلي فخم"
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إصدار التطبيق الفضي الألترا", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .background(DasturTheme.AccentAmber.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                        .border(1.dp, DasturTheme.AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("v2.5.0 Premium", color = DasturTheme.AccentAmber, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            HorizontalDivider(color = DasturTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
                            Text(
                                text = "تطبيق DSTWR TV هو المنصة الترفيهية الفاخرة والأولى لبث القنوات الرياضية والترفيهية والسينمائية المباشرة.\n\n" +
                                "مطور ومصمم بمرآة زجاجية زهرية تقدم تجربة سينمائية فريدة تلبي تطلعات الشغوفين بالبث الأسرع بجودة ممتازة وخالية من الإعلانات تماماً.\n\n" +
                                "جميع الحقوق محفوظة لصالح محمد الدستور ٢٠٢٦ م.",
                                color = DasturTheme.TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        } else {
            // ═══════════════════════════════════════════════════════════════════════════════════════
            // Premium VIP Membership Profile Card (Mohammed Al-Dastour!)
            // ═══════════════════════════════════════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.2.dp, DasturTheme.BorderSoft)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    DasturTheme.SecondaryDark.copy(alpha = 0.5f),
                                    DasturTheme.SurfaceDark.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Refined Glowing Verified Circular Frame
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .background(DasturTheme.PureBlack, CircleShape)
                                .border(
                                    border = BorderStroke(
                                        width = 2.dp, 
                                        brush = Brush.linearGradient(listOf(DasturTheme.PrimaryRed, DasturTheme.AccentAmber))
                                    ),
                                    shape = CircleShape
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DstwrLogo(size = 54.dp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Name Badge + Instagram verify Blue Circle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "محمد الدستور",
                                color = DasturTheme.TextMain,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Verified Account Badge",
                                tint = Color(0xFF3897F0), // Luxury Certified Instagram Cyan
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // 3. Mini golden role text framed with luxury brackets
                        Text(
                            text = "✦ مطور ومؤسس تطبيق DSTWR TV ✦",
                            color = DasturTheme.AccentAmber,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 4. Custom statistics pills that display live counts of the application!
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DasturTheme.SecondaryDark, RoundedCornerShape(12.dp))
                                    .border(1.dp, DasturTheme.BorderSoft.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$totalChannelsCount", 
                                        color = DasturTheme.PrimaryRed, 
                                        fontSize = 15.sp, 
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "قناة متوفرة", 
                                        color = DasturTheme.TextMuted, 
                                        fontSize = 8.5.sp, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(DasturTheme.SecondaryDark, RoundedCornerShape(12.dp))
                                    .border(1.dp, DasturTheme.BorderSoft.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$favoritesCount", 
                                        color = DasturTheme.AccentAmber, 
                                        fontSize = 15.sp, 
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "قناة مفضلة", 
                                        color = DasturTheme.TextMuted, 
                                        fontSize = 8.5.sp, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5. Action Buttons styled under extreme glass reflection details
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(0.95f)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ds.r6"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.SurfaceDark),
                                border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).height(40.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFE1306C), // Luxury Instagram pink
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("انستغرام المطور", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@dastur.tv"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.SurfaceDark),
                                border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f).height(40.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Email,
                                    contentDescription = null,
                                    tint = DasturTheme.PrimaryRed,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الدعم والبريد", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("قائمة الإعدادات والمصادر", color = DasturTheme.TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

            // 1. Sync Channels Card
            PremiumMenuOptionCard(
                title = "مزامنة وتنزيل القنوات المباشرة",
                subtitle = "جلب وتحديث القنوات العامة المدمجة في خوادم البث",
                icon = Icons.Rounded.Refresh,
                iconColor = DasturTheme.PrimaryRed,
                onClick = {
                    onRefreshList(customM3uUrl) { result ->
                        result.onSuccess { count ->
                            onShowNotification(
                                if (count > 0) "تم الجلب والمزامنة بنجاح! ($count قناة)"
                                else "تمت مزامنة قنوات النظام بنجاح!"
                            )
                        }.onFailure { err ->
                            onShowNotification("فشل المزامنة: ${err.message}")
                        }
                    }
                },
                trailingContent = if (isLoading) {
                    { CircularProgressIndicator(color = DasturTheme.PrimaryRed, modifier = Modifier.size(16.dp), strokeWidth = 2.dp) }
                } else null
            )

            // 2. Source Management setup
            PremiumMenuOptionCard(
                title = "إدارة تقسيم مصادر القنوات",
                subtitle = "أختر طريقة عرض القنوات وتصفية باقات المطور وقناتك",
                icon = Icons.AutoMirrored.Rounded.List,
                iconColor = DasturTheme.PrimaryRed,
                onClick = { activeSubPage = "sources" }
            )

            // 3. Custom M3U / IPTV connection
            PremiumMenuOptionCard(
                title = "إضافة خادم IPTV أو ملف M3U خارجي",
                subtitle = "أدمج باقات اشتراكاتك الخاصة مباشرة في مشغل DSTWR",
                icon = Icons.Rounded.Build,
                iconColor = DasturTheme.AccentAmber,
                onClick = { activeSubPage = "server" }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("سينما وعرض وخصوصية المنصة", color = DasturTheme.TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))

            // 4. Cinema visualizer and decoder
            PremiumMenuOptionCard(
                title = "خيارات العرض والسينما الفخمة",
                subtitle = "التحكم في فك الترميز، الإضاءة المحيطية التفاعلية والأبعاد",
                icon = Icons.Rounded.PlayArrow,
                iconColor = Color(0xFF3B82F6),
                onClick = { activeSubPage = "quality" }
            )

            // 5. Privacy Policy
            PremiumMenuOptionCard(
                title = "مدرع الأمن وسياسة الخصوصية",
                subtitle = "اتفاقية الحماية الكاملة للمشاهد والبيانات المحلية",
                icon = Icons.Rounded.Lock,
                iconColor = Color(0xFF10B981),
                onClick = { activeSubPage = "privacy" }
            )

            // 6. About App developer specifications
            PremiumMenuOptionCard(
                title = "عن تطبيق DSTWR TV الفخم",
                subtitle = "شروط تشغيل النظام والملكية الفكرية للأشخاص المحترفين",
                icon = Icons.Rounded.Info,
                iconColor = DasturTheme.PrimaryRed,
                onClick = { activeSubPage = "about" }
            )

            // 7. Family Protection Mode (Static Indicator Card)
            PremiumMenuOptionCard(
                title = "نظام الحاجز العائلي وتصفية الأمن للأسرة",
                subtitle = "جميع المحتويات مراقبة لبيئة عائلية آمنة خالية من المحتوى الهابط",
                icon = Icons.Rounded.Star,
                iconColor = Color(0xFF10B981),
                onClick = {}, // Active block static details
                trailingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Active Guard Protection",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(115.dp))
    }
}

@Composable
fun DasturBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0x730A0515), // Elegant dark glass base with deep premium violet tint (45% opacity)
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x52FFFFFF), // Shiny top bevel reflection
                        Color(0x06FFFFFF)  // Fades down to keep bottom border perfectly clean & glassmorphic
                    )
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x1EFFFFFF), // Glass gloss sheen overlay
                                Color(0x02FFFFFF)
                            )
                        )
                    )
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    Triple("home", "الرئيسية", Icons.Rounded.Home),
                    Triple("channels", "القنوات", Icons.Rounded.PlayArrow),
                    Triple("bouquets", "الباقات", Icons.Rounded.Menu),
                    Triple("favorites", "المفضلة", Icons.Rounded.FavoriteBorder),
                    Triple("settings", "الإعدادات", Icons.Rounded.Settings)
                )

                tabs.forEach { (tabId, label, icon) ->
                    val isActive = currentTab == tabId
                    val activeIcon = if (tabId == "favorites" && isActive) Icons.Rounded.Favorite else icon
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isActive) DasturTheme.PrimaryRed.copy(alpha = 0.16f) else Color.Transparent)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isActive) DasturTheme.PrimaryRed.copy(alpha = 0.35f) else Color.Transparent
                                ),
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
                                imageVector = activeIcon,
                                contentDescription = label,
                                tint = if (isActive) DasturTheme.PrimaryRed else DasturTheme.TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
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
