package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dstwrtv.app.ui.components.DSTWRTheme

@Composable
fun SourcesSettingsView(
    sourceMode: String,
    onSourceModeChange: (String) -> Unit,
    showDevPackage: Boolean,
    onShowDevPackageChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassGroup(
            title = "خيارات العرض وطريقة الدمج",
            subtitle = "حدد الطريقة المفضلة لعرض وتصفية قنوات البث ومطابقتها"
        ) {
            val modes = listOf(
                "merged" to Triple("Merged Mode", "قنوات المطور الافتراضية مع قنواتك المضافة معاً بنظام مدمج سلس", "merged"),
                "user_only" to Triple("User Only", "قصر البث على قنواتك وملفك المرفوع فقط (إخفاء الافتراضي)", "user_only"),
                "dev_only" to Triple("Developer Only", "مشاهدة قنوات المطور الافتراضية المدمجة في النظام فقط لحمايتك", "dev_only")
            )

            modes.forEachIndexed { idx, (_, info) ->
                val (mTitle, mSub, mKey) = info
                PremiumRadioButtonRow(
                    title = mTitle,
                    subtitle = mSub,
                    selected = sourceMode == mKey,
                    onSelect = {
                        onSourceModeChange(mKey)
                    }
                )
                if (idx < modes.size - 1) {
                    HorizontalDivider(color = DSTWRTheme.BorderSoft.copy(alpha = 0.4f), thickness = 1.dp)
                }
            }
        }

        GlassGroup(
            title = "عرض المطور السريع للباقات",
            subtitle = "إظهار أو حجب الباقات ومحطات المطور من اللوائح بنقرة واحدة"
        ) {
            PremiumSwitchRow(
                title = "تفعيل قنوات وباقات المطور الافتراضية",
                subtitle = if (showDevPackage) "نشط = يظهر قنوات النظام" else "معطل = يحجب قنوات النظام كاملة ويقتصر على باقتك",
                checked = showDevPackage,
                onCheckedChange = onShowDevPackageChange,
                icon = Icons.Rounded.FavoriteBorder,
                iconColor = DSTWRTheme.PrimaryRed
            )
        }
    }
}
