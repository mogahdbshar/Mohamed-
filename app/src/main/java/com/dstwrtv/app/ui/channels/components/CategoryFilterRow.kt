package com.dstwrtv.app.ui.channels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DasturTheme

@Composable
fun CategoryFilterRow(
    filters: List<Pair<String, String>>,
    activeFilterId: String,
    onFilterSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        items(filters) { (id, label) ->
            val isActive = activeFilterId == id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isActive) DasturTheme.PrimaryRed else DasturTheme.SurfaceDark)
                    .clickable { onFilterSelect(id) }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(
                        1.dp,
                        if (isActive) DasturTheme.PrimaryRed else DasturTheme.BorderSoft,
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
