package ua.ofkkt.composeshowcase.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Тема застосунку. Покриває слайди Лекції 13:
 *  • «MaterialTheme зсередини» — MaterialTheme = обгортка над CompositionLocalProvider;
 *  • «Розширення теми власними токенами» — LocalExtendedColors;
 *  • «Dynamic Color: реалізація» — перевірка SDK + обов'язковий fallback;
 *  • «Анімований перехід теми» — animateColorAsState для кольорів схеми.
 */
@Composable
fun ShowcaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    animate: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val targetScheme = when {
        // Fallback обов'язковий: на Android 11 і старіших dynamic*ColorScheme() викликати не можна.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    val colorScheme = if (animate) animateColorScheme(targetScheme) else targetScheme
    val extended = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extended) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

@Composable
private fun Color.animated(label: String): Color =
    animateColorAsState(
        targetValue = this,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = label,
    ).value

/** Плавний перехід ~300 мс між світлою та темною схемою (анімуємо основні ролі). */
@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme = target.copy(
    primary = target.primary.animated("primary"),
    onPrimary = target.onPrimary.animated("onPrimary"),
    primaryContainer = target.primaryContainer.animated("primaryContainer"),
    onPrimaryContainer = target.onPrimaryContainer.animated("onPrimaryContainer"),
    secondary = target.secondary.animated("secondary"),
    onSecondary = target.onSecondary.animated("onSecondary"),
    secondaryContainer = target.secondaryContainer.animated("secondaryContainer"),
    onSecondaryContainer = target.onSecondaryContainer.animated("onSecondaryContainer"),
    tertiary = target.tertiary.animated("tertiary"),
    onTertiary = target.onTertiary.animated("onTertiary"),
    tertiaryContainer = target.tertiaryContainer.animated("tertiaryContainer"),
    onTertiaryContainer = target.onTertiaryContainer.animated("onTertiaryContainer"),
    background = target.background.animated("background"),
    onBackground = target.onBackground.animated("onBackground"),
    surface = target.surface.animated("surface"),
    onSurface = target.onSurface.animated("onSurface"),
    surfaceVariant = target.surfaceVariant.animated("surfaceVariant"),
    onSurfaceVariant = target.onSurfaceVariant.animated("onSurfaceVariant"),
    surfaceTint = target.surfaceTint.animated("surfaceTint"),
    outline = target.outline.animated("outline"),
    outlineVariant = target.outlineVariant.animated("outlineVariant"),
    surfaceContainerLowest = target.surfaceContainerLowest.animated("surfaceContainerLowest"),
    surfaceContainerLow = target.surfaceContainerLow.animated("surfaceContainerLow"),
    surfaceContainer = target.surfaceContainer.animated("surfaceContainer"),
    surfaceContainerHigh = target.surfaceContainerHigh.animated("surfaceContainerHigh"),
    surfaceContainerHighest = target.surfaceContainerHighest.animated("surfaceContainerHighest"),
)
