package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme

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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassGroup(
            title = "نوع الاتصال ومصدر القنوات",
            subtitle = "اختر صيغة رابط ملف القنوات (M3U) أو اشتراكك عبر الأبي ميت لـ Xtream"
        ) {
            var inputMode by remember { mutableStateOf("m3u") }
            var xtreamHost by remember { mutableStateOf("") }
            var xtreamUser by remember { mutableStateOf("") }
            var xtreamPass by remember { mutableStateOf("") }

            LaunchedEffect(customM3uUrl) {
                if (customM3uUrl.contains("/get.php?") && customM3uUrl.contains("username=") && customM3uUrl.contains("password=")) {
                    try {
                        val uri = java.net.URI(customM3uUrl)
                        val query = uri.query ?: ""
                        val params = query.split("&").associate {
                            val parts = it.split("=")
                            val key = parts.getOrNull(0) ?: ""
                            val value = parts.getOrNull(1) ?: ""
                            key to value
                        }
                        
                        val user = params["username"] ?: ""
                        val pass = params["password"] ?: ""
                        
                        if (user.isNotBlank() && pass.isNotBlank()) {
                            val host = customM3uUrl.substringBefore("/get.php?")
                            xtreamHost = host
                            xtreamUser = user
                            xtreamPass = pass
                            inputMode = "xtream"
                        }
                    } catch (e: Exception) {
                        // Ignore parsing errors
                    }
                }
            }

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
                    value = customM3uUrl,
                    onValueChange = onCustomM3uUrlChange,
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
                        var targetUrl = customM3uUrl
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
                            onCustomM3uUrlChange(targetUrl)
                        } else {
                            if (customM3uUrl.isNotBlank() && !customM3uUrl.startsWith("http://") && !customM3uUrl.startsWith("https://")) {
                                onSyncIsSuccessChange(false)
                                onSyncStatusMessageChange("الرابط غير صالح. يرجى إدخال رابط M3U صحيح يبدأ بـ http:// أو https://")
                                return@Button
                            }
                        }

                        onIsSavingChange(true)
                        onSyncIsSuccessChange(null)
                        onSyncStatusMessageChange("جاري جلب القنوات ودمجها مع النظام...")
                        onRefreshList(targetUrl) { result ->
                            onIsSavingChange(false)
                            result.onSuccess { count ->
                                onSyncIsSuccessChange(true)
                                onSyncStatusMessageChange("تم جلب القنوات بنجاح! تم تحميل $count قناة وإضافتها بنجاح.")
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
                            text = "حفظ ومزامنة المصدر",
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
