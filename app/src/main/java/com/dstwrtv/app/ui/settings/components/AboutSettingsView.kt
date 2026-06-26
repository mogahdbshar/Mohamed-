package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun AboutSettingsView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassGroup(
            title = "حقوق وعمل منصة DSTWR TV",
            subtitle = "معلومات النظام والحقوق"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إصدار التطبيق",
                    color = DSTWRTheme.TextMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(DSTWRTheme.AccentAmber.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .border(1.dp, DSTWRTheme.AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "v2.5.0",
                        color = DSTWRTheme.AccentAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            HorizontalDivider(
                color = DSTWRTheme.BorderSoft.copy(alpha = 0.4f),
                thickness = 1.dp
            )
            Text(
                text = "تطبيق DSTWR TV هو خيارك لمشاهدة القنوات التلفزيونية والبرامج بشكل مباشر.\n\n" +
                       "تم تصميم التطبيق لتقديم تجربة مشاهدة سلسة وسهلة الاستخدام تلبي تطلعات المشاهدين بجودة ممتازة وخالية من الإعلانات.\n\n" +
                       "جميع الحقوق محفوظة لصالح محمد الدستور ٢٠٢٦ م.",
                color = DSTWRTheme.TextMuted,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
