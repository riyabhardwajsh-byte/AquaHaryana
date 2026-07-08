package com.example.ui.theme

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = SkyBlue,
    secondary = WaterCyan,
    tertiary = OceanBlue,
    background = Color(0xFF0F172A), // Slate-900 background
    surface = Color(0xFF1E293B),    // Slate-800 surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFF1F5F9), // Slate-100 text
    onSurface = Color(0xFFF1F5F9)     // Slate-100 text
  )

private val LightColorScheme =
  lightColorScheme(
    primary = OceanBlue,
    secondary = SkyBlue,
    tertiary = WaterCyan,
    background = IceWhite,      // Warm paper-white #FDFBFF
    surface = Color.White,       // Pure white cards/containers
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = SlateGray,    // Deep Slate-900
    onSurface = SlateGray        // Deep Slate-900
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors by default to preserve the premium water identity of AquaHaryana
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
