package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme
import java.util.UUID

@Composable
fun ServerSettingsView(
    customM3uUrl: String,
    onCustomM3uUrlChange: (String) -> Unit,
    isSaving: Boolean,
    onIsSavingChange: (Boolean) -> Unit,
    syncStatusMessage: String?,
    onSyncStatusMessageChange: (String?) -> Unit,
    syncIsSuccess: Boolean?,
    onSyncIsSuccessChange: (Boolean?) -> Unit,
    onRefreshList: (String, (Result<Int>) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    var playlists by remember { mutableStateOf(PlaylistStorage.getPlaylists(context)) }
    
    var playlistName by remember { mutableStateOf("") }
    var inputMode by remember { mutableStateOf("m3u") }
    
    var m3uUrlInput by remember { mutableStateOf("") }
    var xtreamHost by remember { mutableStateOf("") }
    var xtreamUser by remember { mutableStateOf("") }
    var xtreamPass by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Saved Playlists Manager
        GlassGroup(
            title = "قوائم التشغيل المحفوظة",
            subtitle = "إدارة وتبديل مصادر قنواتك النشطة داخل التطبيق"
        ) {
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد قوائم تشغيل مضافة حالياً. استخدم النموذج أدناه لإضافة سيرفرك الأول.",
                        color = DSTWRTheme.TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    playlists.forEach { pl ->
                        val isActive = pl.url == customM3uUrl
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isActive) DSTWRTheme.PrimaryRed.copy(alpha = 0.1f) else DSTWRTheme.SecondaryDark)
                                .border(
                                    1.dp,
                                    if (isActive) DSTWRTheme.PrimaryRed.copy(alpha = 0.4f) else DSTWRTheme.BorderSoft.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    onIsSavingChange(true)
                                    onSyncIsSuccessChange(null)
                                    onSyncStatusMessageChange("جاري تبديل وتنشيط قائمة [${pl.name}]...")
                                    
                                    onCustomM3uUrlChange(pl.url)
                                    onRefreshList(pl.url) { result ->
                                        onIsSavingChange(false)
                                        result.onSuccess { count ->
                                            onSyncIsSuccessChange(true)
                                            onSyncStatusMessageChange("تم تنشيط قائمة [${pl.name}] بنجاح! تم تحميل $count قناة.")
                                        }.onFailure { err ->
                                            onSyncIsSuccessChange(false)
                                            onSyncStatusMessageChange("خطأ أثناء تنشيط القائمة: ${err.message}")
                                        }
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        if (isActive) DSTWRTheme.PrimaryRed.copy(alpha = 0.2f) else DSTWRTheme.SurfaceDark,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isActive) Icons.Rounded.Check else Icons.AutoMirrored.Rounded.List,
                                    contentDescription = null,
                                    tint = if (isActive) DSTWRTheme.PrimaryRed else DSTWRTheme.TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pl.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (pl.type == "xtream") "اشتراك Xtream API" else "ملف M3U خارجي",
                                    color = DSTWRTheme.TextMuted,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val updated = playlists.filter { it.id != pl.id }
                                    playlists = updated
                                    PlaylistStorage.savePlaylists(context, updated)
                                    
                                    // If deleted the active playlist, reset
                                    if (isActive) {
                                        onCustomM3uUrlChange("")
                                        onRefreshList("") {}
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "حذف",
                                    tint = DSTWRTheme.TextMuted.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Add New Playlist Form
        GlassGroup(
            title = "إضافة قائمة تشغيل جديدة",
            subtitle = "أدخل رابط M3U أو حساب Xtream جديد لحفظه في قائمتك"
        ) {
            Text(
                text = "اسم القائمة",
                color = DSTWRTheme.TextMain,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
            TextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                placeholder = {
                    Text(
                        text = "مثال: اشتراكي الخاص، باقة الرياضة...",
                        color = DSTWRTheme.TextMuted,
                        fontSize = 11.sp
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DSTWRTheme.SecondaryDark,
                    unfocusedContainerColor = DSTWRTheme.SecondaryDark,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(DSTWRTheme.SecondaryDark, RoundedCornerShape(10.dp))
                        .border(
                            1.2.dp,
                            if (inputMode == "m3u") DSTWRTheme.PrimaryRed else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { inputMode = "m3u" }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "رابط M3U",
                        color = if (inputMode == "m3u") Color.White else DSTWRTheme.TextMain,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(DSTWRTheme.SecondaryDark, RoundedCornerShape(10.dp))
                        .border(
                            1.2.dp,
                            if (inputMode == "xtream") DSTWRTheme.PrimaryRed else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { inputMode = "xtream" }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Xtream Codes",
                        color = if (inputMode == "xtream") Color.White else DSTWRTheme.TextMain,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(
                color = DSTWRTheme.BorderSoft.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            if (inputMode == "m3u") {
                Text(
                    text = "رابط ملف القنوات (M3U / M3U8)",
                    color = DSTWRTheme.TextMain,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "انسخ والصق رابط قنواتك المخصصة مباشرة تحت لدمجها تلقائياً بالباقات.",
                    color = DSTWRTheme.TextMuted,
                    fontSize = 9.5.sp
                )

                TextField(
                    value = m3uUrlInput,
                    onValueChange = { m3uUrlInput = it },
                    placeholder = {
                        Text(
                            text = "أدخل رابط m3u الخاص بك هنا...",
                            color = DSTWRTheme.TextMuted,
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DSTWRTheme.SecondaryDark,
                        unfocusedContainerColor = DSTWRTheme.SecondaryDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                )
            } else {
                Text(
                    text = "بيانات حساب Xtream API",
                    color = DSTWRTheme.TextMain,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "أدخل خادم (Host) واسم مستخدم ورمز المرور لاشتراكك المباشر.",
                    color = DSTWRTheme.TextMuted,
                    fontSize = 9.5.sp
                )

                TextField(
                    value = xtreamHost,
                    onValueChange = { xtreamHost = it },
                    placeholder = {
                        Text(
                            text = "مثال: http://iptv-server.com:8080",
                            color = DSTWRTheme.TextMuted,
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DSTWRTheme.SecondaryDark,
                        unfocusedContainerColor = DSTWRTheme.SecondaryDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        value = xtreamUser,
                        onValueChange = { xtreamUser = it },
                        placeholder = {
                            Text(
                                text = "اسم المستخدم",
                                color = DSTWRTheme.TextMuted,
                                fontSize = 11.5.sp
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .border(1.2.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DSTWRTheme.SecondaryDark,
                            unfocusedContainerColor = DSTWRTheme.SecondaryDark,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    )
                    TextField(
                        value = xtreamPass,
                        onValueChange = { xtreamPass = it },
                        placeholder = {
                            Text(
                                text = "كلمة المرور",
                                color = DSTWRTheme.TextMuted,
                                fontSize = 11.5.sp
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .border(1.2.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DSTWRTheme.SecondaryDark,
                            unfocusedContainerColor = DSTWRTheme.SecondaryDark,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    )
                }
            }

            if (syncStatusMessage != null) {
                val (bgCol, borderCol, textCol) = when (syncIsSuccess) {
                    true -> Triple(Color(0x1F10B981), Color(0xFF10B981), Color(0xFF34D399))
                    false -> Triple(Color(0x1FEF4444), Color(0xFFEF4444), Color(0xFFF87171))
                    null -> Triple(DSTWRTheme.PrimaryRed.copy(alpha = 0.12f), DSTWRTheme.PrimaryRed, DSTWRTheme.PrimaryRed)
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
                                color = DSTWRTheme.PrimaryRed
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
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (playlistName.isBlank()) {
                            onSyncIsSuccessChange(false)
                            onSyncStatusMessageChange("يرجى إدخال اسم للقائمة أولاً.")
                            return@Button
                        }
                        
                        var targetUrl = m3uUrlInput.trim()
                        if (inputMode == "xtream") {
                            if (xtreamHost.isBlank() || xtreamUser.isBlank() || xtreamPass.isBlank()) {
                                onSyncIsSuccessChange(false)
                                onSyncStatusMessageChange("يرجى ملء جميع حقول حساب Xtream (الرابط، المستخدم، كلمة المرور)")
                                return@Button
                            }
                            if (!xtreamHost.startsWith("http://") && !xtreamHost.startsWith("https://")) {
                                onSyncIsSuccessChange(false)
                                onSyncStatusMessageChange("رابط الهوست غير صالح. يجب أن يبدأ بـ http:// أو https://")
                                return@Button
                            }

                            val cleanHost = xtreamHost.trim().removeSuffix("/")
                            targetUrl = "$cleanHost/get.php?username=${xtreamUser.trim()}&password=${xtreamPass.trim()}&type=m3u_plus&output=ts"
                        } else {
                            if (targetUrl.isBlank() || (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://"))) {
                                onSyncIsSuccessChange(false)
                                onSyncStatusMessageChange("الرابط غير صالح. يرجى إدخال رابط M3U صحيح يبدأ بـ http:// أو https://")
                                return@Button
                            }
                        }

                        onIsSavingChange(true)
                        onSyncIsSuccessChange(null)
                        onSyncStatusMessageChange("جاري جلب القنوات وحفظ القائمة...")
                        
                        onRefreshList(targetUrl) { result ->
                            onIsSavingChange(false)
                            result.onSuccess { count ->
                                onSyncIsSuccessChange(true)
                                onSyncStatusMessageChange("تم جلب القنوات بنجاح! تم تحميل $count قناة وإضافتها بنجاح.")
                                
                                val newPlaylist = SavedPlaylist(
                                    id = UUID.randomUUID().toString(),
                                    name = playlistName.trim(),
                                    url = targetUrl,
                                    type = inputMode,
                                    host = xtreamHost,
                                    user = xtreamUser,
                                    pass = xtreamPass
                                )
                                val newList = playlists + newPlaylist
                                playlists = newList
                                PlaylistStorage.savePlaylists(context, newList)
                                
                                onCustomM3uUrlChange(targetUrl)
                                
                                // Reset form inputs
                                playlistName = ""
                                m3uUrlInput = ""
                                xtreamHost = ""
                                xtreamUser = ""
                                xtreamPass = ""
                            }.onFailure { err ->
                                onSyncIsSuccessChange(false)
                                onSyncStatusMessageChange("خطأ في الاتصال: ${err.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.PrimaryRed),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جاري الجلب...",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "حفظ ومزامنة القائمة",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                if (customM3uUrl.isNotBlank()) {
                    Button(
                        onClick = {
                            onIsSavingChange(true)
                            onSyncIsSuccessChange(null)
                            onSyncStatusMessageChange("جاري استعادة ملف البث الافتراضي المدمج...")
                            onCustomM3uUrlChange("")
                            onRefreshList("") { result ->
                                onIsSavingChange(false)
                                result.onSuccess {
                                    onSyncIsSuccessChange(true)
                                    onSyncStatusMessageChange("تمت استعادة مصادر البث وتحديث القنوات الافتراضية بنجاح.")
                                }.onFailure { err ->
                                    onSyncIsSuccessChange(false)
                                    onSyncStatusMessageChange("خطأ: ${err.message}")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.SecondaryDark),
                        modifier = Modifier
                            .weight(0.8f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DSTWRTheme.BorderSoft),
                        enabled = !isSaving
                    ) {
                        Text(
                            text = "استعادة الافتراضي",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
