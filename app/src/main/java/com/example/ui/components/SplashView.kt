package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1B0F2A), DasturTheme.PureBlack),
                    radius = 2000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(colors = listOf(DasturTheme.PrimaryRed.copy(alpha = 0.08f), Color.Transparent)))
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
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
                                "DSTWR",
                                color = DasturTheme.TextMain,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "TV",
                                color = DasturTheme.PrimaryRed,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "منصة البث المباشر المتكاملة",
                        color = DasturTheme.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
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
