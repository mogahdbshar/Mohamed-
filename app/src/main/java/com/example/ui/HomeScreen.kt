package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.*
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

// High fidelity styling configurations matching the client's premium dark identity
object DasturTheme {
    val PureBlack = Color(0xFF040405)
    val SurfaceDark = Color(0xFF0D0D10)
    val GlassSurface = Color(0x7319191B)
    val AccentOrange = Color(0xFFFF4500)
    val AccentOrangeGlow = Color(0x33FF4500)
    val TextMuted = Color(0xFF7A7A85)
    val BorderSoft = Color(0x0DFFFFFF)
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf("home") }
    var showSplash by remember { mutableStateOf(true) }

    val channels by viewModel.filteredChannels.collectAsState()
    val favorites by viewModel.favoriteChannels.collectAsState()
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val syncError by viewModel.syncError.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1800) // Instantly loads, displays logo, fades out black screen
        showSplash = false
    }

    if (showSplash) {
        SplashView()
    } else {
        // Base screen container with edge-to-edge support configuration
        Scaffold(
            bottomBar = {
                DasturBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            },
            containerColor = DasturTheme.PureBlack,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Safe Drawing Header
                DasturHeader(
                    onProfileClick = { currentTab = "settings" }
                )

                // Dynamic view based on the current bottom bar tab selection
                when (currentTab) {
                    "home" -> HomeView(
                        channels = channels,
                        selectedChannel = selectedChannel,
                        isLoading = isLoading,
                        syncError = syncError,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onChannelSelect = { viewModel.selectChannel(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        favorites = favorites
                    )
                    "favorites" -> FavoritesView(
                        favorites = favorites,
                        selectedChannel = selectedChannel,
                        onChannelSelect = {
                            viewModel.selectChannel(it)
                            currentTab = "home" // auto focus back to stream player
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                    "settings" -> SettingsView(
                        onRefreshList = { viewModel.syncFromNetwork() },
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}

@Composable
fun SplashView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040405)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Stylized glowing logo wrapper
            Box(
                modifier = Modifier
                    .size(114.dp)
                    .background(Color(0xFFFF4500).copy(alpha = 0.05f), CircleShape)
                    .border(2.dp, androidx.compose.ui.graphics.Brush.sweepGradient(
                        colors = listOf(Color(0xFFFF4500), Color(0xFFFFD100), Color(0xFFFF4500))
                    ), CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main app logo asset with safe fallback
                coil.compose.AsyncImage(
                    model = com.example.R.drawable.dstwr_logo_asset_1781909924808,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name in beautiful sleek display
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "DSTWR",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TV",
                    color = Color(0xFFFF4500),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "الجيل الجديد للبث التلفزيوني والرياضي",
                color = Color(0xFF7A7A85),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Modern smooth circular loader
            CircularProgressIndicator(
                color = Color(0xFFFF4500),
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun DasturHeader(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DasturTheme.PureBlack)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title in beautiful Arabic typeface
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "DSTWR",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "TV",
                color = DasturTheme.AccentOrange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Developer Profile Button
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(38.dp)
                .background(DasturTheme.GlassSurface, shape = CircleShape)
                .border(1.dp, DasturTheme.BorderSoft, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "ملف المطور",
                tint = Color.White
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
    favorites: List<Channel>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // High fidelity video player section
        if (selectedChannel != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                VideoPlayer(url = selectedChannel.url)
                
                // Active stream display match title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(DasturTheme.GlassSurface, shape = RoundedCornerShape(14.dp))
                        .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "البث المباشر المختار",
                            color = Color(0xFF00FF7F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedChannel.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Easy Favorite toggle directly on active stream
                    val isFav = favorites.any { it.url == selectedChannel.url }
                    IconButton(
                        onClick = { onToggleFavorite(selectedChannel) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "تفضيل",
                            tint = if (isFav) Color(0xFFFFD700) else Color.White
                        )
                    }
                }
            }
        } else {
            // Outstanding Cinematic Promo Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                // Background artistic gradient representing sports stadium aura
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E0B03),
                                    Color(0xFF07000B),
                                    Color(0xFF040405)
                                )
                            )
                        )
                )

                // Glowing circular ambient light
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-5).dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(Color(0xFFFF4500).copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
                )

                // High quality modern grid visual
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
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
                                    .size(8.dp)
                                    .background(Color(0xFFFFD100), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "منصة البث الذكي الموحد",
                                color = Color(0xFFFFD100),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Premium Tag
                        Text(
                            text = "بث عالي الدقة",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Column {
                        Text(
                            text = "بث مباشر مستقر وتلقائي للتفاعل الكلي",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "استمتع بمتابعة المحتوى الرياضي والترفيهي بجودة فائقة وبتأقلم تلقائي يضمن ثبات الاتصال وسلاسة العرض.",
                            color = Color(0xFF9E9EA5),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFF4500), RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "اختر باقة أو قناة الآن",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Active servers badge indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF00FF7F), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "جودة الخدمة مستمرة ومثالية",
                                color = Color(0xFF00FF7F),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Dynamic Filtering Bar
        DasturSearchBar(searchQuery = searchQuery, onSearchChange = onSearchChange)

        if (isLoading && channels.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DasturTheme.AccentOrange)
            }
        } else {
            // Beautiful semantic sorting ordering for packages to prioritize Arabic Sports & Entertainment
            val categoryOrder = listOf(
                "باقة قنوات SSC الرياضية",
                "باقة قنوات beIN Sports العربية",
                "باقة قنوات أبوظبي الرياضية",
                "باقة قنوات الكأس الرياضية",
                "باقة VIP شاهد",
                "باقة قنوات MBC الكاملة",
                "باقة قنوات OSN الترفيهية",
                "باقة الأحداث الرياضية والبوكسينج (PPV)",
                "باقة أفلام ومسلسلات نتفليكس",
                "باقة قنوات روتانا",
                "باقة قنوات الأطفال والكرتون",
                "باقة القنوات الوثائقية",
                "باقة الأخبار والبرامج السياسية",
                "باقة القنوات العربية العامة",
                "باقة القنوات العالمية الأخرى"
            )

            val categories = channels.groupBy { it.category }
                .toList()
                .sortedBy { (categoryName, _) ->
                    val idx = categoryOrder.indexOf(categoryName)
                    if (idx != -1) idx else categoryOrder.size
                }

            if (categories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DasturTheme.TextMuted, modifier = Modifier.size(42.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (syncError != null) syncError else "لا توجد قنوات مطابقة لبحثك.",
                            color = DasturTheme.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(categories) { (categoryName, groupChannels) ->
                        Column(modifier = Modifier.padding(vertical = 10.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = categoryName,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Text(
                                    text = "قناة ${groupChannels.size}",
                                    color = DasturTheme.TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                             // Dynamic smooth sliding row for Channels matching perfect visual requirements
                             val sortedGroupChannels = remember(groupChannels) {
                                 groupChannels.sortedWith { o1, o2 ->
                                     val isAr1 = o1.name.any { it in '\u0600'..'\u06FF' } || o1.name.uppercase(java.util.Locale.ROOT).contains("AR")
                                     val isAr2 = o2.name.any { it in '\u0600'..'\u06FF' } || o2.name.uppercase(java.util.Locale.ROOT).contains("AR")
                                     val hasFr1 = o1.name.uppercase(java.util.Locale.ROOT).contains("FR")
                                     val hasFr2 = o2.name.uppercase(java.util.Locale.ROOT).contains("FR")
                                     when {
                                         isAr1 && !isAr2 -> -1
                                         !isAr1 && isAr2 -> 1
                                         !hasFr1 && hasFr2 -> -1
                                         hasFr1 && !hasFr2 -> 1
                                         else -> o1.name.compareTo(o2.name, ignoreCase = true)
                                     }
                                 }
                             }

                             LazyRow(
                                 modifier = Modifier.fillMaxWidth(),
                                 contentPadding = PaddingValues(horizontal = 20.dp),
                                 horizontalArrangement = Arrangement.spacedBy(14.dp)
                             ) {
                                 items(sortedGroupChannels) { channel ->
                                     val isFav = favorites.any { it.url == channel.url }
                                     ChannelCard(
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
        placeholder = { Text("ابحث عن القنوات العربية والمباريات...", color = DasturTheme.TextMuted, fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(50.dp)
            .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(12.dp))
            .background(Color.Transparent),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DasturTheme.GlassSurface,
            unfocusedContainerColor = DasturTheme.GlassSurface,
            disabledContainerColor = DasturTheme.GlassSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "بحث",
                tint = DasturTheme.TextMuted,
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "مسح",
                        tint = DasturTheme.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun ChannelCard(
    channel: Channel,
    isFavorite: Boolean,
    isActive: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .width(114.dp)
            .height(154.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) DasturTheme.SurfaceDark else DasturTheme.GlassSurface)
            .clickable(onClick = onSelect)
            .border(
                1.5.dp,
                if (isActive) {
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            DasturTheme.AccentOrange.copy(alpha = glowAlpha),
                            Color(0xFFFFD100).copy(alpha = glowAlpha)
                        )
                    )
                } else {
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(DasturTheme.BorderSoft, DasturTheme.BorderSoft)
                    )
                },
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Upper Card Section for logo loading or fallback text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp)
                    .background(Color(0xFF070708)),
                contentAlignment = Alignment.Center
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = channel.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        error = androidx.compose.ui.graphics.painter.ColorPainter(Color.Transparent),
                        fallback = androidx.compose.ui.graphics.painter.ColorPainter(Color.Transparent)
                    )
                } else {
                    // Gorgeous, modern, dynamic typographic visual avatar placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF25252C),
                                        Color(0xFF0C0C0F)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background decorative circle outline
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                        )
                        
                        Text(
                            text = if (channel.name.isNotBlank()) channel.name.take(2).uppercase() else "TV",
                            color = if (isActive) DasturTheme.AccentOrange else Color(0xFFC5C5CE),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Smooth Live indicator overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(
                            if (isActive) DasturTheme.AccentOrange else Color(0x99000000),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Interactive Star Button placement
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "مفضلة",
                        tint = if (isFavorite) Color(0xFFFFD100) else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Lower Card Name Details Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0A0A0C))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    overflow = TextOverflow.Ellipsis
                )
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "قائمتي المفضلة",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = DasturTheme.TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لم تقم بإضافة أي قنوات للمفضلة حتى الآن",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "اضغط على أيقونة القلب على أي قناة لإضافتها هنا",
                        color = DasturTheme.TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 105.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(favorites) { channel ->
                    ChannelCard(
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
fun SettingsView(onRefreshList: () -> Unit, isLoading: Boolean) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Developer Profile Card matching the original CSS layout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFFFF4500).copy(alpha = 0.08f), Color(0x99141416))
                    )
                )
                .border(1.dp, Color(0xFFFF4500).copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing Avatar
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(Color(0x19FF4500), CircleShape)
                        .border(2.dp, DasturTheme.AccentOrange, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    coil.compose.AsyncImage(
                        model = com.example.R.drawable.dstwr_logo_asset_1781909924808,
                        contentDescription = "Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "محمد الدستور",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "المطور والمؤسس لمنصة DASTUR.TV",
                    color = DasturTheme.AccentOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Social buttons with explicit links in Arabic
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ds.r6"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.GlassSurface),
                        border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("انستغرام", color = Color.White, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@dastur.tv"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DasturTheme.GlassSurface),
                        border = BorderStroke(1.dp, DasturTheme.BorderSoft),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("الدعم الفني", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Multi-Action Configuration Section
        Text(
            text = "خيارات توفير البيانات والحماية",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 1. Refresh Cache Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(onClick = onRefreshList),
            colors = CardDefaults.cardColors(containerColor = DasturTheme.GlassSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DasturTheme.BorderSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = DasturTheme.AccentOrange)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("مزامنة وتحديث قنوات البث", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("تنزيل وتحديث البيانات وحفظها محلياً", color = DasturTheme.TextMuted, fontSize = 10.sp)
                    }
                }
                if (isLoading) {
                    CircularProgressIndicator(color = DasturTheme.AccentOrange, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
                }
            }
        }

        // 2. Family filter configuration card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = DasturTheme.GlassSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DasturTheme.BorderSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00FF7F))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("التصفية الصارمة للعائلات", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("حاجب المحتوى والمصادر الحساسة نشط بالكامل", color = DasturTheme.TextMuted, fontSize = 10.sp)
                    }
                }
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00FF7F))
            }
        }

        // 3. Network optimized card config
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = DasturTheme.GlassSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, DasturTheme.BorderSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF00BFFF))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("توفير فائق لاستهلاك الإنترنت", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("تشغيل فوري عبر التخزين المؤقت وحفظ الحزم", color = DasturTheme.TextMuted, fontSize = 10.sp)
                    }
                }
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00BFFF))
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
        color = Color(0xD20D0D10),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0x12FFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("home", "الرئيسية", Icons.Default.Home),
                Triple("favorites", "المفضلة", Icons.Default.Favorite),
                Triple("settings", "الإعدادات", Icons.Default.Settings)
            )

            tabs.forEach { (tabId, tabName, tabIcon) ->
                val isActive = currentTab == tabId
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tabId) }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = tabIcon,
                        contentDescription = tabName,
                        tint = if (isActive) DasturTheme.AccentOrange else Color(0xFF7A7A85),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tabName,
                        color = if (isActive) Color.White else Color(0xFF7A7A85),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
