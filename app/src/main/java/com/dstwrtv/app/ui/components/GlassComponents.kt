package com.dstwrtv.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * A highly-polished reusable Glass UI Card that implements beautiful translucent gradients
 * and a refracting glowing edge border. Supports performance optimizations on all devices.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    isActive: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val backgroundBrush = remember(isActive, DSTWRTheme.SurfaceDark, DSTWRTheme.PrimaryRed) {
        Brush.verticalGradient(
            colors = if (isActive) {
                listOf(
                    DSTWRTheme.PrimaryRed.copy(alpha = 0.22f),
                    DSTWRTheme.SurfaceDark.copy(alpha = 0.7f)
                )
            } else {
                listOf(
                    DSTWRTheme.SurfaceDark.copy(alpha = 0.85f),
                    DSTWRTheme.SecondaryDark.copy(alpha = 0.45f)
                )
            }
        )
    }

    val borderBrush = remember(isActive, DSTWRTheme.BorderSoft, DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber) {
        Brush.linearGradient(
            colors = if (isActive) {
                listOf(
                    DSTWRTheme.PrimaryRed.copy(alpha = 0.7f),
                    DSTWRTheme.AccentAmber.copy(alpha = 0.35f)
                )
            } else {
                listOf(
                    DSTWRTheme.BorderSoft.copy(alpha = 0.6f),
                    DSTWRTheme.BorderSoft.copy(alpha = 0.1f)
                )
            }
        )
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .clip(shape)
            .background(backgroundBrush)
            .border(borderWidth, borderBrush, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        onClick()
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * A state-of-the-art Glass UI Dialog that implements a perfect translucent popup window
 * with high readability, beautiful typography, custom buttons, and edge glow refraction.
 */
@Composable
fun GlassDialog(
    onDismissRequest: () -> Unit,
    title: String,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DSTWRTheme.SurfaceDark.copy(alpha = 0.95f),
                            DSTWRTheme.PureBlack.copy(alpha = 0.92f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DSTWRTheme.BorderSoft,
                            DSTWRTheme.BorderSoft.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = DSTWRTheme.BorderSoft.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                content()
            }
        }
    }
}

/**
 * A beautiful glass floating panel used to hold categories, search options, or lists.
 */
@Composable
fun GlassFloatingPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DSTWRTheme.SurfaceDark.copy(alpha = 0.88f),
                        DSTWRTheme.SecondaryDark.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        DSTWRTheme.BorderSoft,
                        DSTWRTheme.BorderSoft.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
            .padding(16.dp),
        content = content
    )
}

/**
 * Reusable elegant glass-themed button with interactive states and glowing boundaries.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = Color.White
    ),
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable RowScope.() -> Unit
) {
    val borderBrush = remember(enabled, DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber) {
        if (enabled) {
            Brush.horizontalGradient(
                colors = listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber)
            )
        } else {
            Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.1f))
            )
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (enabled) DSTWRTheme.SurfaceDark.copy(alpha = 0.4f)
                else Color.White.copy(alpha = 0.02f)
            )
            .border(1.2.dp, borderBrush, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}
