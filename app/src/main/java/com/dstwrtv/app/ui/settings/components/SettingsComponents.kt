package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme

data class ThemeInfo(
    val id: String,
    val name: String,
    val description: String,
    val primary: Color,
    val accent: Color,
    val bg: Color
)

@Composable
fun GlassGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                color = DSTWRTheme.TextMain,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = DSTWRTheme.TextMuted,
                    fontSize = 9.5.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DSTWRTheme.SurfaceDark,
                            DSTWRTheme.SurfaceDark.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(1.2.dp, DSTWRTheme.BorderSoft, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PremiumSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null,
    iconColor: Color = DSTWRTheme.PrimaryRed
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    iconColor.copy(alpha = 0.22f),
                                    iconColor.copy(alpha = 0.04f)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    iconColor.copy(alpha = 0.43f),
                                    iconColor.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    color = DSTWRTheme.TextMain,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = DSTWRTheme.TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DSTWRTheme.PrimaryRed,
                uncheckedThumbColor = DSTWRTheme.TextMuted,
                uncheckedTrackColor = DSTWRTheme.SecondaryDark,
                uncheckedBorderColor = DSTWRTheme.BorderSoft
            )
        )
    }
}

@Composable
fun PremiumRadioButtonRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (selected) Color.White else DSTWRTheme.TextMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = DSTWRTheme.TextMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = DSTWRTheme.PrimaryRed,
                unselectedColor = DSTWRTheme.TextMuted
            )
        )
    }
}

@Composable
fun PremiumMenuOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    DSTWRTheme.BorderSoft,
                    DSTWRTheme.BorderSoft.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DSTWRTheme.SurfaceDark,
                            DSTWRTheme.SurfaceDark.copy(alpha = 0.08f)
                        )
                    )
                )
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.08f)
            ) {
                drawCircle(
                    color = iconColor,
                    radius = 200f,
                    center = androidx.compose.ui.geometry.Offset(size.width - 40f, size.height / 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                )
                drawCircle(
                    color = iconColor,
                    radius = 100f,
                    center = androidx.compose.ui.geometry.Offset(size.width - 40f, size.height / 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.8f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        iconColor.copy(alpha = 0.24f),
                                        iconColor.copy(alpha = 0.05f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.2.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        iconColor.copy(alpha = 0.45f),
                                        iconColor.copy(alpha = 0.12f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            color = DSTWRTheme.TextMain,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = subtitle,
                            color = DSTWRTheme.TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
                if (trailingContent != null) {
                    trailingContent()
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(DSTWRTheme.SecondaryDark, CircleShape)
                            .border(1.dp, DSTWRTheme.BorderSoft.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionRow(
    theme: ThemeInfo,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) theme.primary.copy(alpha = 0.08f) else Color.Transparent)
            .border(
                1.dp, 
                if (isSelected) theme.primary.copy(alpha = 0.4f) else DSTWRTheme.BorderSoft.copy(alpha = 0.4f), 
                RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(theme.bg)
                .border(2.dp, theme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(theme.accent)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = theme.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
            Text(text = theme.description, color = DSTWRTheme.TextMuted, fontSize = 10.sp, lineHeight = 14.sp)
        }
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = theme.primary)
        )
    }
}
