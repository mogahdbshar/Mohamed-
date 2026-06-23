package com.dstwrtv.app.ui.player.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFFFF4500),
                strokeWidth = 3.dp,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "جاري تهيئة البث المباشر...",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorOverlay(
    errorMessage: String,
    isRetrying: Boolean = false,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF151515))
                .padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFF4500).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRetrying) Icons.Default.Refresh else Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color(0xFFFF4500),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = errorMessage,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            if (isRetrying) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier.width(120.dp).height(2.dp).clip(CircleShape),
                    color = Color(0xFFFF4500),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRetry,
                enabled = !isRetrying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRetrying) Color.DarkGray else Color(0xFFFF4500),
                    disabledContainerColor = Color.DarkGray
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (isRetrying) "جاري الربط..." else "إعادة المحاولة الآن",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
