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

    val channels by viewModel.filteredChannels.collectAsState()
    val favorites by viewModel.favoriteChannels.collectAsState()
    val selectedChannel by viewModel.selectedChannel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val syncError by viewModel.syncError.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

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
                text = "دستور",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "سبورت",
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
            // Outstanding Cinematic Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DasturTheme.GlassSurface)
                    .border(1.dp, DasturTheme.BorderSoft, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = DasturTheme.AccentOrange,
                        modifier = Modifier
                            .size(46.dp)
                            .background(DasturTheme.AccentOrangeGlow, CircleShape)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "اختر بـاقة أو قـناة لتشغيل البث الفوري المباشر",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تحديثات وتأقلم تلقائي فائق السرعة",
                        color = DasturTheme.TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
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
            // Dynamically order categories to give Supreme Priority to Arabic Channels
            // channels representing general sports like Bein Arabic and packages are sorted to first order
            val categories = channels.groupBy { it.category }
                .toList()
                .sortedWith { o1, o2 ->
                    val isArabic1 = o1.first.contains("عربي", true) || o1.first.contains("beIN", true) || o1.first.contains("سبورت", true)
                    val isArabic2 = o2.first.contains("عربي", true) || o2.first.contains("beIN", true) || o2.first.contains("سبورت", true)
                    when {
                        isArabic1 && !isArabic2 -> -1
                        !isArabic1 && isArabic2 -> 1
                        else -> o1.first.compareTo(o2.first)
                    }
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
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(groupChannels) { channel ->
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
    Box(
        modifier = Modifier
            .width(114.dp)
            .height(154.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DasturTheme.GlassSurface)
            .clickable(onClick = onSelect)
            .border(
                1.dp,
                if (isActive) DasturTheme.AccentOrange else DasturTheme.BorderSoft,
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Upper Card Section for logo setup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp)
                    .background(Color(0xFF070708)),
                contentAlignment = Alignment.Center
            ) {
                // If logo is provided, we use a beautiful text preview backed by first char matching mock logo design
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = channel.name.take(2).uppercase(),
                        color = if (isActive) DasturTheme.AccentOrange else Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (isActive) DasturTheme.AccentOrange else DasturTheme.GlassSurface,
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
                }

                // Interactive Star Button placement matching pure HTML aesthetic
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
                        tint = if (isFavorite) Color(0xFFFFD700) else Color.White.copy(alpha = 0.4f),
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
                        .size(68.dp)
                        .background(Color(0x19FF4500), CircleShape)
                        .border(2.dp, DasturTheme.AccentOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = DasturTheme.AccentOrange,
                        modifier = Modifier.size(28.dp)
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
    NavigationBar(
        containerColor = DasturTheme.PureBlack,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Support Edge-to-Edge safely matching bottom navigation inset
    ) {
        val tabs = listOf(
            Triple("home", "الرئيسية", Icons.Default.Home),
            Triple("favorites", "المفضلة", Icons.Default.Favorite),
            Triple("settings", "الإعدادات", Icons.Default.Settings)
        )

        tabs.forEach { (tabId, tabName, tabIcon) ->
            val isActive = currentTab == tabId
            NavigationBarItem(
                selected = isActive,
                onClick = { onTabSelected(tabId) },
                icon = {
                    Icon(
                        imageVector = tabIcon,
                        contentDescription = tabName,
                        tint = if (isActive) DasturTheme.AccentOrange else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = tabName,
                        color = if (isActive) Color.White else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = DasturTheme.GlassSurface
                )
            )
        }
    }
}
