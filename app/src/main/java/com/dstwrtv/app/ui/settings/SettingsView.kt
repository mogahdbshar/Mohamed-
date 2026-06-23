package com.dstwrtv.app.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DasturTheme
import com.dstwrtv.app.ui.components.DstwrLogo
import com.dstwrtv.app.ui.settings.components.*

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
    
    var useHardwareAcceleration by remember { mutableStateOf(true) }
    var forceWidescreen by remember { mutableStateOf(false) }
    
    val sharedPrefs = remember { context.getSharedPreferences("dstwr_prefs", android.content.Context.MODE_PRIVATE) }
    var customM3uUrl by remember { mutableStateOf(sharedPrefs.getString("custom_m3u_url", "") ?: "") }
    
    var sourceMode by remember { mutableStateOf(sharedPrefs.getString("source_mode", "merged") ?: "merged") }
    var showDevPackage by remember { mutableStateOf(sharedPrefs.getBoolean("show_dev_package", true)) }

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
                                        syncStatusMessage = "جاري جلب القنوات ودمجها مع النظام..."
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
                                        Text("استعادة الافتراضي", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                icon = Icons.Rounded.Favorite,
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
                                subtitle = "مكافحة ظهور الحواف السوداء تلقائياً عبر مطابقة أبعاد الشاشة",
                                checked = forceWidescreen,
                                onCheckedChange = { forceWidescreen = it },
                                icon = Icons.Rounded.PlayArrow,
                                iconColor = Color(0xFF3B82F6)
                            )
                        }

                        GlassGroup(
                            title = "تغيير سمة التطبيق (الثيمات)",
                            subtitle = "اختر التنسيق اللوني الذي يعكس ذوقك، وسيتحول كامل نظام الألوان والأضواء والخلفيات بنسبة ١٠٪ لتكتمل تجربة الرفاهية."
                        ) {
                            val activeThemes = remember {
                                listOf(
                                    ThemeInfo("noir_diamond", "Noir Diamond", "تنسيق يجمع بين اللون الأسود والذهبي.", Color(0xFFE50914), Color(0xFFFFD700), Color(0xFF020204)),
                                    ThemeInfo("indigo_sapphire", "Indigo Sapphire (ياقوت إنديجو)", "طابع هادئ مريح للعين يمزج بين زرقة المحيط وبريق الياقوت الأزرق.", Color(0xFF6366F1), Color(0xFF60E5FF), Color(0xFF050814)),
                                    ThemeInfo("midnight_velvet", "Midnight Velvet (مخمل منتصف الليل)", "رونق كلاسيكي دافئ يمزج بين درجات الأرجواني المخملي وذهب الغروب.", Color(0xFF8B5CF6), Color(0xFFF43F5E), Color(0xFF0A0208)),
                                    ThemeInfo("emerald_onyx", "Emerald Onyx (زمرد أونيكس)", "تنسيق حيوي مستوحى من الأونيكس الأسود وبريق الزمرد الأخضر الخام.", Color(0xFF10B981), Color(0xFFD9F99D), Color(0xFF020604)),
                                    ThemeInfo("carbon_platinum", "Carbon Platinum (كربون بلاتينيوم)", "طابع تقني فائق الدقة يجمع بين ألياف الكربون والبلاتين اللامع.", Color(0xFFFACC15), Color(0xFFE2E8F0), Color(0xFF0B0B0D))
                                )
                            }

                            activeThemes.forEach { theme ->
                                val isSelected = activeThemeId == theme.id
                                Spacer(modifier = Modifier.height(12.dp))
                                ThemeOptionRow(
                                    theme = theme,
                                    isSelected = isSelected,
                                    onSelect = { onThemeChange(theme.id) }
                                )
                            }
                        }
                    }
                }
                "privacy" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassGroup(
                            title = "دليل الضمانات الأمنية",
                            subtitle = "أمان وسرية المستخدم هي ركيزة التشغيل لمنصة البث"
                        ) {
                            Text(
                                text = "نحن في DSTWR TV نولي أهمية قصوى لخصوصيتك. جميع عمليات معالجة وحفظ البيانات والاشتراكات والمفضلة تتم محلياً في جهازك، ولا نقوم برفع أي بيانات تتعلق بسجل مشاهدتك وقنواتك المفضلة إلى خوادم خارجية.\n\nإن نظام تصفية العائلات والحاجز الأخلاقي مفعل تلقائياً بالكامل ولا يمكن تعطيله لضمان بيئة آمنة دائماً.\n\nتتم عمليات المزامنة بشكل مجهول ومحمي لضمان خصوصيتك بالكامل.",
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
                            subtitle = "معلومات النظام والحقوق"
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إصدار التطبيق", color = DasturTheme.TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .background(DasturTheme.AccentAmber.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                        .border(1.dp, DasturTheme.AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("v2.5.0", color = DasturTheme.AccentAmber, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            HorizontalDivider(color = DasturTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
                            Text(
                                text = "تطبيق DSTWR TV هو خيارك لمشاهدة القنوات التلفزيونية والبرامج بشكل مباشر.\n\n" +
                                "تم تصميم التطبيق لتقديم تجربة مشاهدة سلسة وسهلة الاستخدام تلبي تطلعات المشاهدين بجودة ممتازة وخالية من الإعلانات.\n\n" +
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.2.dp, DasturTheme.BorderSoft)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(DasturTheme.SecondaryDark, DasturTheme.SurfaceDark)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
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
                            DstwrLogo(size = 58.dp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "محمد الدستور",
                                color = DasturTheme.TextMain,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(0.8.dp, Color(0x73FFFFFF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Verified Badge",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "المطور والمهندس الرئيس لشبكات IPTV الرقمية وتصميم منصة DSTWR TV.\n" +
                                   "المشرف العام على استقرار وجدولة خوادم البث المباشر المدمجة، فك تشفير وتثبيت جودة العرض والأداء المستمر.",
                            color = DasturTheme.TextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        
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
                    }
                }
            }

            PremiumMenuOptionCard(
                title = "إعدادات المصدر والبيانات",
                subtitle = "تحديث ملفات M3U أو ربط حسابات Xtream API",
                icon = Icons.Rounded.Refresh,
                iconColor = DasturTheme.PrimaryRed,
                onClick = { activeSubPage = "server" }
            )
            PremiumMenuOptionCard(
                title = "تخصيص ثيم النظام والألوان",
                subtitle = "تغيير المظهر العام وتفعيل الإضاءة السينمائية",
                icon = Icons.Rounded.Info,
                iconColor = DasturTheme.AccentAmber,
                onClick = { activeSubPage = "quality" }
            )
            PremiumMenuOptionCard(
                title = "إدارة مصادر القنوات المدمجة",
                subtitle = "التحكم في ظهور باقات المطور أو قصر العرض على ملفك",
                icon = Icons.Rounded.List,
                iconColor = Color(0xFF10B981),
                onClick = { activeSubPage = "sources" }
            )
            PremiumMenuOptionCard(
                title = "الخصوصية والاتفاقية الأمنية",
                subtitle = "دليل حماية البيانات ومعلومات التشغيل الآمن",
                icon = Icons.Rounded.Build,
                iconColor = Color(0xFF3B82F6),
                onClick = { activeSubPage = "privacy" }
            )
            PremiumMenuOptionCard(
                title = "حول تطبيق DSTWR TV",
                subtitle = "معلومات الإصدار الحالي وحقوق الملكية للمطور",
                icon = Icons.Rounded.Star,
                iconColor = Color(0xFFA855F7),
                onClick = { activeSubPage = "about" }
            )
            
            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}


fun getAppPackageName(context: android.content.Context): String {
    return context.packageName
}




