package com.dstwrtv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PremiumSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
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
            isFail -> Color(0xFFEF4444)
            isWarn -> DSTWRTheme.PrimaryRed
            isSuccess -> Color(0xFF10B981)
            else -> DSTWRTheme.AccentAmber
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFA07070A), Color(0xEE12121A))
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
                        contentDescription = null,
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
