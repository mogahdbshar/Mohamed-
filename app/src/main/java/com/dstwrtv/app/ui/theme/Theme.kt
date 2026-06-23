package com.dstwrtv.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF030305),
    surface = Color(0xFF0D0D14),
    onBackground = Color(0xFFFAFAFA),
    onSurface = Color(0xFFFAFAFA)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by defaultfor a premium cinema feel
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        // Always force dynamicDarkColorScheme instead of dynamicLightColorScheme to avoid annoying light mode styles
        dynamicDarkColorScheme(context)
      }
      else -> DarkColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
