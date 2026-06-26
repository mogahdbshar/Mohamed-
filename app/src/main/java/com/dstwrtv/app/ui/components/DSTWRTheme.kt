package com.dstwrtv.app.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.dstwrtv.app.model.Channel

// ═══════════════════════════════════════════════
// DSTWR TV — Beautiful Premium Dark Design System
// ═══════════════════════════════════════════════
object DSTWRTheme {
    var PureBlack by mutableStateOf(Color(0xFF030305))      // Absolute luxury deep black
    var SurfaceDark by mutableStateOf(Color(0x1AFFFFFF))    // Semi-translucent glass (approx 10% white)
    var SecondaryDark by mutableStateOf(Color(0x0EFFFFFF))  // Sub-layer glass (approx 5% white)
    var BorderSoft by mutableStateOf(Color(0x2BFFFFFF))     // Elegant glass edge refraction (17% white)
    var PrimaryRed by mutableStateOf(Color(0xFFE50914))     // Iconic Cinematic Red
    var AccentAmber by mutableStateOf(Color(0xFFFFD700))     // Classic 24K Gold
    val TextMain = Color(0xFFFAFAFA)                        // Crisp Modern White
    val TextMuted = Color(0xFF9CA3AF)                       // Sophisticated Slate Muted
}

fun applyThemeStyle(themeId: String) {
    when (themeId) {
        "classic_dark" -> {
            DSTWRTheme.PureBlack = Color(0xFF111216)
            DSTWRTheme.PrimaryRed = Color(0xFF3F51B5)
            DSTWRTheme.AccentAmber = Color(0xFF2196F3)
            DSTWRTheme.SurfaceDark = Color(0x1F2C3E50)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "amoled_black" -> {
            DSTWRTheme.PureBlack = Color(0xFF000000)
            DSTWRTheme.PrimaryRed = Color(0xFFFAFAFA)
            DSTWRTheme.AccentAmber = Color(0xFF8E8E93)
            DSTWRTheme.SurfaceDark = Color(0x1A222222)
            DSTWRTheme.SecondaryDark = Color(0x0CFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x1EFFFFFF)
        }
        "ocean_breeze" -> {
            DSTWRTheme.PureBlack = Color(0xFF010A15)
            DSTWRTheme.PrimaryRed = Color(0xFF0284C7)
            DSTWRTheme.AccentAmber = Color(0xFF38BDF8)
            DSTWRTheme.SurfaceDark = Color(0x1F1E293B)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "forest_emerald" -> {
            DSTWRTheme.PureBlack = Color(0xFF020E08)
            DSTWRTheme.PrimaryRed = Color(0xFF059669)
            DSTWRTheme.AccentAmber = Color(0xFF34D399)
            DSTWRTheme.SurfaceDark = Color(0x1F14532D)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "crimson_gold", "crimson_neon" -> {
            DSTWRTheme.PureBlack = Color(0xFF080102)
            DSTWRTheme.PrimaryRed = Color(0xFFE50914)
            DSTWRTheme.AccentAmber = Color(0xFFFFD700)
            DSTWRTheme.SurfaceDark = Color(0x1F221111)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "sunset_warmth" -> {
            DSTWRTheme.PureBlack = Color(0xFF0A020F)
            DSTWRTheme.PrimaryRed = Color(0xFFF43F5E)
            DSTWRTheme.AccentAmber = Color(0xFFFB923C)
            DSTWRTheme.SurfaceDark = Color(0x1F2E121E)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "space_cosmic" -> {
            DSTWRTheme.PureBlack = Color(0xFF05030E)
            DSTWRTheme.PrimaryRed = Color(0xFF8B5CF6)
            DSTWRTheme.AccentAmber = Color(0xFFA78BFA)
            DSTWRTheme.SurfaceDark = Color(0x1F1E1B4B)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "metropolis_neon" -> {
            DSTWRTheme.PureBlack = Color(0xFF0F1115)
            DSTWRTheme.PrimaryRed = Color(0xFF06B6D4)
            DSTWRTheme.AccentAmber = Color(0xFFFDE047)
            DSTWRTheme.SurfaceDark = Color(0x1F1F2937)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        "dynamic_chameleon" -> {
            DSTWRTheme.PureBlack = Color(0xFF050508)
            DSTWRTheme.PrimaryRed = Color(0xFF10B981)
            DSTWRTheme.AccentAmber = Color(0xFF3B82F6)
            DSTWRTheme.SurfaceDark = Color(0x1AFFFFFF)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2BFFFFFF)
        }
        // Legacy themes retained for full backwards compatibility
        "noir_diamond" -> {
            DSTWRTheme.PureBlack = Color(0xFF010103)
            DSTWRTheme.PrimaryRed = Color(0xFFE50914)
            DSTWRTheme.AccentAmber = Color(0xFFFFD700)
            DSTWRTheme.SurfaceDark = Color(0x1F22222E)
            DSTWRTheme.SecondaryDark = Color(0x0EFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x3DFFFFFF)
        }
        "indigo_sapphire" -> {
            DSTWRTheme.PureBlack = Color(0xFF050814)
            DSTWRTheme.PrimaryRed = Color(0xFF6366F1)
            DSTWRTheme.AccentAmber = Color(0xFF60E5FF)
            DSTWRTheme.SurfaceDark = Color(0x1DFFFFFF)
            DSTWRTheme.SecondaryDark = Color(0x12FFFFFF)
            DSTWRTheme.BorderSoft = Color(0x35FFFFFF)
        }
        "midnight_velvet" -> {
            DSTWRTheme.PureBlack = Color(0xFF0A0208)
            DSTWRTheme.PrimaryRed = Color(0xFFF43F5E)
            DSTWRTheme.AccentAmber = Color(0xFFFB923C)
            DSTWRTheme.SurfaceDark = Color(0x22FFFFFF)
            DSTWRTheme.SecondaryDark = Color(0x15FFFFFF)
            DSTWRTheme.BorderSoft = Color(0x3AFFFFFF)
        }
        "emerald_onyx" -> {
            DSTWRTheme.PureBlack = Color(0xFF020604)
            DSTWRTheme.PrimaryRed = Color(0xFF10B981)
            DSTWRTheme.AccentAmber = Color(0xFFD9F99D)
            DSTWRTheme.SurfaceDark = Color(0x19FFFFFF)
            DSTWRTheme.SecondaryDark = Color(0x0DFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2AFFFFFF)
        }
        "carbon_platinum" -> {
            DSTWRTheme.PureBlack = Color(0xFF0B0B0D)
            DSTWRTheme.PrimaryRed = Color(0xFFFACC15)
            DSTWRTheme.AccentAmber = Color(0xFFE2E8F0)
            DSTWRTheme.SurfaceDark = Color(0x1AFFFFFF)
            DSTWRTheme.SecondaryDark = Color(0x0CFFFFFF)
            DSTWRTheme.BorderSoft = Color(0x2CFFFFFF)
        }
    }
}

fun getChannelAmbientColor(channel: Channel?): Color {
    if (channel == null) return Color(0xFF673AB7)
    val cat = channel.category.lowercase()
    val name = channel.name.lowercase()
    
    return when {
        cat.contains("news") || cat.contains("أخبار") || cat.contains("اخبار") || name.contains("الجزيرة") || name.contains("العربية") || name.contains("news") -> Color(0xFF03A9F4)
        cat.contains("sport") || cat.contains("رياضة") || cat.contains("الرياضية") || name.contains("bein") || name.contains("الكأس") -> Color(0xFF00E676)
        cat.contains("islam") || cat.contains("قرأن") || cat.contains("قرآن") || cat.contains("قران") || cat.contains("دعوة") || name.contains("المجد") || name.contains("سنة") -> Color(0xFFFFD54F)
        cat.contains("kids") || cat.contains("أطفال") || cat.contains("atfal") || name.contains("طيور") || name.contains("براعم") -> Color(0xFFF06292)
        cat.contains("cinema") || cat.contains("أفلام") || cat.contains("سينما") || cat.contains("دراما") || name.contains("mbc") || name.contains("روتانا") -> Color(0xFFE50914)
        else -> {
            val hash = channel.name.hashCode()
            val colors = listOf(Color(0xFFE50914), Color(0xFF9C27B0), Color(0xFF03A9F4), Color(0xFF00E676), Color(0xFFFFB703), Color(0xFFE040FB))
            colors[Math.abs(hash) % colors.size]
        }
    }
}
