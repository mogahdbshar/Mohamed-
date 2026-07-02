package com.dstwrtv.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.core.settings.RemoteConfigManager

@Composable
fun RemoteControlProtectionOverlay(
    config: RemoteConfigManager,
    currentVersionCode: Int = 1,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val appStatus = config.appStatus
    val minVersion = config.minAppVersion
    
    // Check if force update is needed
    val needsForceUpdate = minVersion > currentVersionCode
    
    // State to handle optional update dialog
    var showOptionalUpdate by remember(config.latestAppVersion) {
        mutableStateOf(config.latestAppVersion > currentVersionCode && !needsForceUpdate)
    }

    // State to handle announcement dialog
    var showAnnouncement by remember(config.announcementShow) {
        mutableStateOf(config.announcementShow && config.announcementMessage.isNotBlank())
    }

    if (needsForceUpdate) {
        // Unskippable Force Update Screen
        BlockingStatusScreen(
            title = "يتوفر تحديث إجباري جديد",
            message = config.updateMessage,
            icon = Icons.Rounded.SystemUpdate,
            iconColor = DSTWRTheme.PrimaryRed,
            actionText = "تحميل التحديث الآن",
            actionUrl = config.updateUrl
        )
    } else if (appStatus == "maintenance") {
        // Maintenance Screen
        BlockingStatusScreen(
            title = config.maintenanceTitle.ifBlank { "الصيانة قيد التشغيل" },
            message = config.maintenanceMessage.ifBlank { "التطبيق حالياً في صيانة مبرمجة لتقديم أفضل جودة وسيرفرات أسرع. نعتذر عن الإزعاج ونعدكم بالعودة قريباً جداً!" },
            icon = Icons.Rounded.Build,
            iconColor = DSTWRTheme.AccentAmber,
            actionText = "قناة الدعم الفني على تليجرام",
            actionUrl = config.supportButtonUrl,
            showSupportButton = config.supportButtonVisible
        )
    } else if (appStatus == "suspended") {
        // Suspended Screen
        BlockingStatusScreen(
            title = config.suspendedTitle.ifBlank { "تنبيه إيقاف الخدمة" },
            message = config.suspendedMessage.ifBlank { "تم إيقاف هذا الإصدار بشكل نهائي أو مؤقت من قبل الإدارة. يرجى مراجعة الدعم الفني للاستفسار." },
            icon = Icons.Rounded.Warning,
            iconColor = Color(0xFFEF4444),
            actionText = "تواصل مع الإدارة عبر تليجرام",
            actionUrl = config.supportButtonUrl,
            showSupportButton = config.supportButtonVisible
        )
    } else {
        // Render main content with optional overlays on top
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            // Optional Update Dialog
            if (showOptionalUpdate) {
                AlertDialog(
                    onDismissRequest = { showOptionalUpdate = false },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color(0xFF111216),
                    titleContentColor = Color.White,
                    textContentColor = DSTWRTheme.TextMuted,
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SystemUpdate,
                                contentDescription = null,
                                tint = DSTWRTheme.AccentAmber
                            )
                            Text(
                                text = "تحديث جديد متوفر!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    text = {
                        Text(
                            text = config.updateMessage,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showOptionalUpdate = false
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(config.updateUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.PrimaryRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("تحديث الآن", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showOptionalUpdate = false }) {
                            Text("لاحقاً", color = DSTWRTheme.TextMuted)
                        }
                    }
                )
            }

            // Remote Announcement Popup
            if (showAnnouncement && config.announcementType == "popup") {
                val dismissBtn: @Composable (() -> Unit)? = if (config.announcementSkippable) {
                    {
                        TextButton(onClick = { showAnnouncement = false }) {
                            Text("إغلاق", color = DSTWRTheme.TextMuted)
                        }
                    }
                } else null

                AlertDialog(
                    onDismissRequest = { 
                        if (config.announcementSkippable) showAnnouncement = false 
                    },
                    modifier = Modifier.border(1.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    containerColor = Color(0xFF12131A),
                    titleContentColor = Color.White,
                    textContentColor = DSTWRTheme.TextMuted,
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = DSTWRTheme.AccentAmber,
                                modifier = Modifier.size(26.dp)
                            )
                            Text(
                                text = config.announcementTitle.ifBlank { "إعلان هام" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    },
                    text = {
                        Text(
                            text = config.announcementMessage,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Right,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { showAnnouncement = false },
                            colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.PrimaryRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("حسناً فهمت", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = dismissBtn
                )
            }
        }
    }
}

@Composable
private fun BlockingStatusScreen(
    title: String,
    message: String,
    icon: ImageVector,
    iconColor: Color,
    actionText: String,
    actionUrl: String,
    showSupportButton: Boolean = true
) {
    val context = LocalContext.current
    
    // Breathing & scaling animation for cinematic look
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030305))
    ) {
        // Red glowing ambient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(iconColor.copy(alpha = 0.15f), Color.Transparent),
                        radius = 1600f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Container with futuristic aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(iconColor.copy(alpha = 0.08f))
                    .border(BorderStroke(1.dp, iconColor.copy(alpha = 0.25f)), RoundedCornerShape(32.dp))
                    .scale(scale)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Informational Box (Glassmorphic look)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x0EFFFFFF))
                    .border(BorderStroke(1.dp, Color(0x1FFFFFFF)), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (showSupportButton && actionUrl.isNotBlank()) {
                // Shiny primary action button
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(actionUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = iconColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = actionText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (iconColor == DSTWRTheme.AccentAmber) Color.Black else Color.White
                    )
                }
            }
        }
    }
}
