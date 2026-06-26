package com.dstwrtv.app.ui.channels.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun CategoryFilterRow(
    filters: List<Pair<String, String>>,
    activeFilterId: String,
    onFilterSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp, top = 2.dp, end = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 14.dp)
    ) {
        items(filters, key = { it.first }) { (id, label) ->
            val isActive = activeFilterId == id
            var isFocused by remember { mutableStateOf(false) }
            val isHighlighted = isActive || isFocused
            
            // Premium smooth color transitions
            val backgroundColor by animateColorAsState(
                targetValue = if (isHighlighted) DSTWRTheme.PrimaryRed else DSTWRTheme.SurfaceDark,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "bgColor"
            )
            
            val borderAccentColor by animateColorAsState(
                targetValue = if (isHighlighted) DSTWRTheme.AccentAmber.copy(alpha = 0.6f) else DSTWRTheme.BorderSoft.copy(alpha = 0.3f),
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "borderColor"
            )

            val textColor by animateColorAsState(
                targetValue = if (isHighlighted) Color.White else DSTWRTheme.TextMuted,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "textColor"
            )

            val scaleFactor by animateFloatAsState(
                targetValue = if (isFocused) 1.1f else (if (isActive) 1.05f else 1.0f),
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .scale(scaleFactor)
                    .onFocusChanged { isFocused = it.isFocused }
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isHighlighted) {
                            Brush.horizontalGradient(
                                colors = listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.PrimaryRed.copy(alpha = 0.8f))
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0.6f))
                            )
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = androidx.compose.foundation.LocalIndication.current
                    ) {
                        onFilterSelect(id)
                    }
                    .border(
                        width = if (isHighlighted) 1.5.dp else 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(borderAccentColor, borderAccentColor.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 7.dp)
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 11.5.sp,
                    fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold
                )
            }
        }
    }
}
