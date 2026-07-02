package com.dstwrtv.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme
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
    val remoteConfigManager = remember { (context.applicationContext as com.dstwrtv.app.DstwrApplication).remoteConfigManager }
    var activeSubPage by remember { mutableStateOf<String?>(null) }
    
    var useHardwareAcceleration by remember { mutableStateOf(true) }
    var forceWidescreen by remember { mutableStateOf(false) }
    
    val sharedPrefs = remember { context.getSharedPreferences("dstwr_prefs", android.content.Context.MODE_PRIVATE) }
    var customM3uUrl by remember { mutableStateOf(sharedPrefs.getString("custom_m3u_url", "") ?: "") }
    
    val defaultSourceMode = if (remoteConfigManager.hideDeveloperUI) "user_only" else (sharedPrefs.getString("source_mode", "merged") ?: "merged")
    val defaultShowDev = if (remoteConfigManager.hideDeveloperUI) false else sharedPrefs.getBoolean("show_dev_package", true)

    var sourceMode by remember(remoteConfigManager.hideDeveloperUI) { mutableStateOf(defaultSourceMode) }
    var showDevPackage by remember(remoteConfigManager.hideDeveloperUI) { mutableStateOf(defaultShowDev) }

    var isSaving by remember { mutableStateOf(false) }
    var syncStatusMessage by remember { mutableStateOf<String?>(null) }
    var syncIsSuccess by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
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
                        .background(DSTWRTheme.SurfaceDark, CircleShape)
                        .border(1.dp, DSTWRTheme.BorderSoft, CircleShape)
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
                    color = DSTWRTheme.TextMain,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when(activeSubPage) {
                "server" -> {
                    ServerSettingsView(
                        customM3uUrl = customM3uUrl,
                        onCustomM3uUrlChange = { url ->
                            customM3uUrl = url
                            sharedPrefs.edit().putString("custom_m3u_url", url).apply()
                        },
                        isSaving = isSaving,
                        onIsSavingChange = { isSaving = it },
                        syncStatusMessage = syncStatusMessage,
                        onSyncStatusMessageChange = { syncStatusMessage = it },
                        syncIsSuccess = syncIsSuccess,
                        onSyncIsSuccessChange = { syncIsSuccess = it },
                        onRefreshList = onRefreshList
                    )
                }
                "quality" -> {
                    QualitySettingsView(
                        ambientGlowEnabled = ambientGlowEnabled,
                        onAmbientGlowChange = onAmbientGlowChange,
                        useHardwareAcceleration = useHardwareAcceleration,
                        onUseHardwareAccelerationChange = { useHardwareAcceleration = it },
                        forceWidescreen = forceWidescreen,
                        onForceWidescreenChange = { forceWidescreen = it },
                        activeThemeId = activeThemeId,
                        onThemeChange = onThemeChange
                    )
                }
                "privacy" -> {
                    PrivacySettingsView()
                }
                "sources" -> {
                    SourcesSettingsView(
                        sourceMode = sourceMode,
                        onSourceModeChange = { mKey ->
                            sourceMode = mKey
                            sharedPrefs.edit().putString("source_mode", mKey).apply()
                            onRefreshList(customM3uUrl) {}
                        },
                        showDevPackage = showDevPackage,
                        onShowDevPackageChange = { isChecked ->
                            showDevPackage = isChecked
                            sharedPrefs.edit().putBoolean("show_dev_package", isChecked).apply()
                            onRefreshList(customM3uUrl) {}
                        }
                    )
                }
                "about" -> {
                    AboutSettingsView()
                }
            }
        } else {
            DeveloperProfileCard(
                totalChannelsCount = totalChannelsCount,
                favoritesCount = favoritesCount
            )
            
            PremiumMenuOptionCard(
                title = "تحديث القنوات والمزامنة",
                subtitle = "جلب أحدث القنوات من السيرفر وتحديث القائمة",
                icon = Icons.Rounded.Refresh,
                iconColor = Color(0xFF10B981),
                onClick = {
                    onShowNotification("جاري تحديث القنوات...")
                    onRefreshList(customM3uUrl) { result ->
                        if (result.isSuccess) {
                            onShowNotification("تم تحديث القنوات بنجاح")
                        } else {
                            onShowNotification(result.exceptionOrNull()?.message ?: "فشل في تحديث القنوات")
                        }
                    }
                }
            )

            PremiumMenuOptionCard(
                title = "إعدادات المصدر والبيانات",
                subtitle = "تحديث ملفات M3U أو ربط حسابات Xtream API",
                icon = Icons.Rounded.Cloud,
                iconColor = DSTWRTheme.PrimaryRed,
                onClick = { activeSubPage = "server" }
            )
            PremiumMenuOptionCard(
                title = "تخصيص ثيم النظام والألوان",
                subtitle = "تغيير المظهر العام وتفعيل الإضاءة السينمائية",
                icon = Icons.Rounded.Info,
                iconColor = DSTWRTheme.AccentAmber,
                onClick = { activeSubPage = "quality" }
            )
            if (!remoteConfigManager.hideDeveloperUI) {
                PremiumMenuOptionCard(
                    title = "إدارة مصادر القنوات المدمجة",
                    subtitle = "التحكم في ظهور باقات المطور أو قصر العرض على ملفك",
                    icon = Icons.AutoMirrored.Rounded.List,
                    iconColor = Color(0xFF10B981),
                    onClick = { activeSubPage = "sources" }
                )
            }
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
        Spacer(modifier = Modifier.height(130.dp))
    }
}

fun getAppPackageName(context: android.content.Context): String {
    return context.packageName
}
