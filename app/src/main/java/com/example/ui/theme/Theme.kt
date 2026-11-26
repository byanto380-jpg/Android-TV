package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.darkColorScheme as tvDarkColorScheme

private val NusantaraM3ColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = VoidNavy,
    primaryContainer = GlassSurfaceElevated,
    onPrimaryContainer = CyanSoft,
    secondary = GoldAccent,
    onSecondary = VoidNavy,
    tertiary = IndigoAccent,
    background = VoidNavy,
    onBackground = TextPrimary,
    surface = DeepSlate,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurface,
    onSurfaceVariant = TextSecondary,
    error = CoralLive,
    outline = GlassBorder
)

@OptIn(ExperimentalTvMaterial3Api::class)
private val NusantaraTvColorScheme = tvDarkColorScheme(
    primary = CyanPrimary,
    onPrimary = VoidNavy,
    primaryContainer = GlassSurfaceElevated,
    onPrimaryContainer = CyanSoft,
    secondary = GoldAccent,
    onSecondary = VoidNavy,
    tertiary = IndigoAccent,
    background = VoidNavy,
    onBackground = TextPrimary,
    surface = DeepSlate,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurface,
    onSurfaceVariant = TextSecondary,
    error = CoralLive,
    border = GlassBorder
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NusantaraTVTheme(
    content: @Composable () -> Unit
) {
    TvMaterialTheme(
        colorScheme = NusantaraTvColorScheme
    ) {
        MaterialTheme(
            colorScheme = NusantaraM3ColorScheme,
            typography = NusantaraTypography,
            content = content
        )
    }
}
