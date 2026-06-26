package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun QualitySettingsView(
    ambientGlowEnabled: Boolean,
    onAmbientGlowChange: (Boolean) -> Unit,
    useHardwareAcceleration: Boolean,
    onUseHardwareAccelerationChange: (Boolean) -> Unit,
    forceWidescreen: Boolean,
    onForceWidescreenChange: (Boolean) -> Unit,
    activeThemeId: String,
    onThemeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassGroup(
            title = "خصائص العرض والمشغل الذكي",
            subtitle = "خيارات الإضاءة التفاعلية وفك الترميز وحسابات الأبعاد"
        ) {
            PremiumSwitchRow(
                title = "وضع السينما والإضاءة المحيطة",
                subtitle = "تفعيل هالة توهج لينة خلف مشغل الفيديو للتخفيف من إجهاد العين",
                checked = ambientGlowEnabled,
                onCheckedChange = onAmbientGlowChange,
                icon = Icons.Rounded.Favorite,
                iconColor = DSTWRTheme.AccentAmber
            )
            HorizontalDivider(color = DSTWRTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
            PremiumSwitchRow(
                title = "فك ترميز الهاردوير (تسريع الأجهزة)",
                subtitle = "استخدام المعالج الرسومي للجهاز لتقليل استهلاك البطارية والسخونة",
                checked = useHardwareAcceleration,
                onCheckedChange = onUseHardwareAccelerationChange,
                icon = Icons.Rounded.Build,
                iconColor = DSTWRTheme.PrimaryRed
            )
            HorizontalDivider(color = DSTWRTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
            PremiumSwitchRow(
                title = "فرض ملء الشاشة بنسبة 16:9",
                subtitle = "مكافحة ظهور الحواف السوداء تلقائياً عبر مطابقة أبعاد الشاشة",
                checked = forceWidescreen,
                onCheckedChange = onForceWidescreenChange,
                icon = Icons.Rounded.PlayArrow,
                iconColor = Color(0xFF3B82F6)
            )
        }

        GlassGroup(
            title = "تغيير سمة التطبيق (الثيمات)",
            subtitle = "اختر التنسيق اللوني الذي يعكس ذوقك، وسيتحول كامل نظام الألوان والأضواء والخلفيات بنسبة ١٠٪ لتكتمل تجربة الرفاهية."
        ) {
            val activeThemes = remember {
                listOf(
                    ThemeInfo("classic_dark", "Classic Dark (كلاسيك داكن)", "تنسيق داكن مريح ومستقر مناسب للاستخدام اليومي المستمر.", Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF111216)),
                    ThemeInfo("amoled_black", "AMOLED Black (سواد عميق)", "تنسيق أسود مطلق مخصص لشاشات AMOLED لتوفير الطاقة التام وراحة للعين.", Color(0xFFFAFAFA), Color(0xFF8E8E93), Color(0xFF000000)),
                    ThemeInfo("ocean_breeze", "Ocean (نسمات المحيط)", "مستوحى من درجات لون المحيط الهادئ الممزوج بالأزرق المرجاني والسيان.", Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF010A15)),
                    ThemeInfo("forest_emerald", "Forest (زمرد الغابات)", "مزيج متناغم رائع من درجات اللون الأخضر الزمردي والمطفي.", Color(0xFF059669), Color(0xFF34D399), Color(0xFF020E08)),
                    ThemeInfo("crimson_gold", "Crimson (قرمزي ملكي)", "تنسيق فخم يجمع بين الأحمر القرمزي البراق والذهب الخالص والأسود.", Color(0xFFE50914), Color(0xFFFFD700), Color(0xFF080102)),
                    ThemeInfo("sunset_warmth", "Sunset (دفء الغروب)", "طيف دافئ من ألوان الشفق الأرجواني، البرتقالي والذهبي.", Color(0xFFF43F5E), Color(0xFFFB923C), Color(0xFF0A020F)),
                    ThemeInfo("space_cosmic", "Space (الفضاء الكوني)", "تنسيق مستوحى من سديم الفضاء الكوني بلونه البنفسجي الساحر والأزرق النجمي.", Color(0xFF8B5CF6), Color(0xFFA78BFA), Color(0xFF05030E)),
                    ThemeInfo("metropolis_neon", "Metropolis (أضواء العاصمة)", "تنسيق عصري يتميز بلون رمادي خرساني مع لمسات نيون براقة.", Color(0xFF06B6D4), Color(0xFFFDE047), Color(0xFF0F1115)),
                    ThemeInfo("dynamic_chameleon", "Dynamic Theme (الثيم المتغير)", "يتغير لونه تلقائياً حسب القناة الحالية لعرض تجربة غامرة.", Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFF050508))
                )
            }

            activeThemes.forEach { theme ->
                val isSelected = activeThemeId == theme.id
                Spacer(modifier = Modifier.height(12.dp))
                ThemeOptionRow(
                    theme = theme,
                    isSelected = isSelected,
                    onSelect = { onThemeChange(theme.id) }
                )
            }
        }
    }
}
