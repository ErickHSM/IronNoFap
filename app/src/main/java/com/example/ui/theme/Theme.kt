package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IronWillColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF003816),
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldOnContainer,

    secondary = BlueAccent,
    onSecondary = Color(0xFF003350),
    secondaryContainer = Color(0xFF0F2E47),
    onSecondaryContainer = Color(0xFFBAE6FD),

    tertiary = AmberWarning,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Color(0xFFFDE68A),

    error = CrimsonAlert,
    onError = Color(0xFF450A0A),
    errorContainer = CrimsonContainer,
    onErrorContainer = CrimsonOnContainer,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondary,

    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IronWillColorScheme,
        typography = Typography,
        content = content
    )
}
