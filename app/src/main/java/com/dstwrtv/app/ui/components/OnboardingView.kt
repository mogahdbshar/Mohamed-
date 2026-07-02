package com.dstwrtv.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class OnboardingStep {
    SELECTOR,
    M3U_INPUT,
    XTREAM_INPUT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingView(
    onComplete: (String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(OnboardingStep.SELECTOR) }
    var m3uUrl by remember { mutableStateOf("") }
    var xtreamHost by remember { mutableStateOf("") }
    var xtreamUser by remember { mutableStateOf("") }
    var xtreamPass by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF130825),
                            Color(0xFF090312),
                            DSTWRTheme.PureBlack
                        )
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DSTWRTheme.PrimaryRed.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * 0.2f),
                        radius = size.minDimension * 0.9f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            DstwrLogo(size = 72.dp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "مرحباً بك في DSTWR TV",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "اختر الطريقة المفضلة لبدء تشغيل ومزامنة قنواتك التلفزيونية",
                color = DSTWRTheme.TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "step_transition"
            ) { currentStep ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (currentStep) {
                        OnboardingStep.SELECTOR -> {
                            OnboardingCardOption(
                                title = "استخدام الباقات الافتراضية المدمجة",
                                subtitle = "تحميل قنوات النظام العامة، الإخبارية والأطفال والرياضة الموفرة مجاناً تلقائياً.",
                                icon = Icons.Rounded.PlayArrow,
                                iconColor = DSTWRTheme.AccentAmber,
                                onClick = {
                                    isLoading = true
                                    errorMessage = null
                                    onComplete("") // Empty url means default preloaded channels
                                }
                            )

                            OnboardingCardOption(
                                title = "إضافة رابط ملف M3U مخصص",
                                subtitle = "أضف ملف قنواتك الخاص بصيغة M3U/M3U8 لدمجه داخل باقات التطبيق.",
                                icon = Icons.Rounded.Link,
                                iconColor = DSTWRTheme.PrimaryRed,
                                onClick = { step = OnboardingStep.M3U_INPUT }
                            )

                            OnboardingCardOption(
                                title = "ربط اشتراك Xtream Codes",
                                subtitle = "ادخل بيانات سيرفر Xtream (رابط، يوزر، باسوورد) وسيجلب التطبيق القنوات لك.",
                                icon = Icons.Rounded.Cloud,
                                iconColor = Color(0xFF3B82F6),
                                onClick = { step = OnboardingStep.XTREAM_INPUT }
                            )
                        }

                        OnboardingStep.M3U_INPUT -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { step = OnboardingStep.SELECTOR }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "رجوع",
                                        tint = DSTWRTheme.PrimaryRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "الرجوع للقائمة الرئيسية",
                                        color = DSTWRTheme.PrimaryRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "رابط M3U مخصص",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
                                )

                                TextField(
                                    value = m3uUrl,
                                    onValueChange = { m3uUrl = it },
                                    placeholder = {
                                        Text(
                                            text = "انسخ رابط M3U أو M3U8 الخاص بك هنا...",
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
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Button(
                                    onClick = {
                                        if (m3uUrl.isBlank() || (!m3uUrl.startsWith("http://") && !m3uUrl.startsWith("https://"))) {
                                            errorMessage = "يرجى إدخال رابط M3U صحيح يبدأ بـ http:// أو https://"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null
                                        onComplete(m3uUrl.trim())
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.PrimaryRed),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading
                                ) {
                                    Text("حفظ وتشغيل القنوات", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OnboardingStep.XTREAM_INPUT -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { step = OnboardingStep.SELECTOR }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "رجوع",
                                        tint = DSTWRTheme.PrimaryRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "الرجوع للقائمة الرئيسية",
                                        color = DSTWRTheme.PrimaryRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "بيانات حساب Xtream API",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
                                )

                                TextField(
                                    value = xtreamHost,
                                    onValueChange = { xtreamHost = it },
                                    placeholder = {
                                        Text(
                                            text = "عنوان الخادم (الهوست) مثل: http://example.com:8080",
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
                                    shape = RoundedCornerShape(12.dp)
                                )

                                TextField(
                                    value = xtreamUser,
                                    onValueChange = { xtreamUser = it },
                                    placeholder = {
                                        Text(
                                            text = "اسم المستخدم",
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
                                    shape = RoundedCornerShape(12.dp)
                                )

                                TextField(
                                    value = xtreamPass,
                                    onValueChange = { xtreamPass = it },
                                    placeholder = {
                                        Text(
                                            text = "كلمة المرور",
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
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Button(
                                    onClick = {
                                        if (xtreamHost.isBlank() || xtreamUser.isBlank() || xtreamPass.isBlank()) {
                                            errorMessage = "يرجى ملء جميع حقول حساب Xtream بالكامل"
                                            return@Button
                                        }
                                        if (!xtreamHost.startsWith("http://") && !xtreamHost.startsWith("https://")) {
                                            errorMessage = "يجب أن يبدأ عنوان السيرفر بـ http:// أو https://"
                                            return@Button
                                        }
                                        
                                        isLoading = true
                                        errorMessage = null
                                        val cleanHost = xtreamHost.trim().removeSuffix("/")
                                        val generatedUrl = "$cleanHost/get.php?username=${xtreamUser.trim()}&password=${xtreamPass.trim()}&type=m3u_plus&output=ts"
                                        onComplete(generatedUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DSTWRTheme.PrimaryRed),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isLoading
                                ) {
                                    Text("ربط واستيراد الحساب", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = DSTWRTheme.PrimaryRed, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "جاري مزامنة وجلب القنوات وتجهيز الباقات...",
                    color = DSTWRTheme.TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }

            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x2CEF4444)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Rounded.Warning, contentDescription = "تنبيه", tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, color = Color(0xFFF87171), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
                LaunchedEffect(msg) {
                    isLoading = false
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "بالمتابعة، أنت توافق على دمج مصادرك الخاصة مع باقات العرض ونظام الترشيح العائلي التلقائي للتطبيق.",
                color = DSTWRTheme.TextMuted.copy(alpha = 0.5f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = onSkip,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, DSTWRTheme.BorderSoft),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "تخطي والتشغيل الفوري للمدمج", color = Color.White, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OnboardingCardOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DSTWRTheme.SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, iconColor.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = subtitle,
                    color = DSTWRTheme.TextMuted,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowLeft,
                contentDescription = null,
                tint = DSTWRTheme.TextMuted.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
