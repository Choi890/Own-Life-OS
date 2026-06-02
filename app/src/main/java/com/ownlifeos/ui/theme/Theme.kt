package com.ownlifeos.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = CorePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAE9FF),
    onPrimaryContainer = Color(0xFF202052),
    secondary = CoreTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDF4EF),
    onSecondaryContainer = Color(0xFF073E3A),
    tertiary = CoreRose,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE5EA),
    onTertiaryContainer = Color(0xFF4A1423),
    error = CoreRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFE5DC),
    onErrorContainer = Color(0xFF5A1D17),
    background = CoreMist,
    onBackground = CoreInk,
    surface = CoreSurface,
    onSurface = CoreInk,
    surfaceVariant = CoreSurfaceSoft,
    onSurfaceVariant = CoreMuted,
    outline = Color(0xFFB9C0CE),
    outlineVariant = CoreLine,
    inverseSurface = CoreInk,
    inverseOnSurface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF202050),
    primaryContainer = Color(0xFF33335F),
    onPrimaryContainer = Color(0xFFE8E8FF),
    secondary = DarkTeal,
    onSecondary = Color(0xFF063A37),
    secondaryContainer = Color(0xFF1D4F4B),
    onSecondaryContainer = Color(0xFFDDF4EF),
    tertiary = DarkRose,
    onTertiary = Color(0xFF4A1423),
    tertiaryContainer = Color(0xFF623144),
    onTertiaryContainer = Color(0xFFFFE8EE),
    error = DarkRed,
    onError = Color(0xFF561C1C),
    errorContainer = Color(0xFF73332D),
    onErrorContainer = Color(0xFFFFE4E4),
    background = DarkInk,
    onBackground = Color(0xFFF6F7FA),
    surface = DarkSurface,
    onSurface = Color(0xFFF4F4FA),
    surfaceVariant = DarkSurfaceSoft,
    onSurfaceVariant = Color(0xFFD2D6E2),
    outline = Color(0xFF7C8291),
    outlineVariant = Color(0xFF343A46),
    inverseSurface = Color(0xFFF4F4FA),
    inverseOnSurface = DarkInk
)

private val OwnLifeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

@Composable
fun OwnLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = OwnLifeShapes,
        content = content
    )
}
