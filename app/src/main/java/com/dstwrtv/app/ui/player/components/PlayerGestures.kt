package com.dstwrtv.app.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun GestureOverlay(
    visible: Boolean,
    gestureType: String?,
    gestureValue: Float
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = if (gestureType == "brightness") Alignment.CenterStart else Alignment.CenterEnd
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(45.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                    .padding(vertical = 16.dp, horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = if (gestureType == "brightness") Icons.Rounded.WbSunny else {
                        if (gestureValue == 0f) Icons.Rounded.VolumeOff else if (gestureValue < 0.5f) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(120.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(gestureValue)
                            .background(DSTWRTheme.PrimaryRed, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}
