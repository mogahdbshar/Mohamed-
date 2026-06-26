package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun PrivacySettingsView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassGroup(
            title = "دليل الضمانات الأمنية",
            subtitle = "أمان وسرية المستخدم هي ركيزة التشغيل لمنصة البث"
        ) {
            Text(
                text = "نحن في DSTWR TV نولي أهمية قصوى لخصوصيتك. جميع عمليات معالجة وحفظ البيانات والاشتراكات والمفضلة تتم محلياً في جهازك، ولا نقوم برفع أي بيانات تتعلق بسجل مشاهدتك وقنواتك المفضلة إلى خوادم خارجية.\n\nإن نظام تصفية العائلات والحاجز الأخلاقي مفعل تلقائياً بالكامل ولا يمكن تعطيله لضمان بيئة آمنة دائماً.\n\nتتم عمليات المزامنة بشكل مجهول ومحمي لضمان خصوصيتك بالكامل.",
                color = DSTWRTheme.TextMuted,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
