package ua.ofkkt.composeshowcase

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import ua.ofkkt.composeshowcase.navigation.ShowcaseApp
import ua.ofkkt.composeshowcase.ui.theme.LocalThemeSettings
import ua.ofkkt.composeshowcase.ui.theme.ShowcaseTheme
import ua.ofkkt.composeshowcase.ui.theme.ThemeSettings
import ua.ofkkt.composeshowcase.ui.theme.isDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Налаштування теми переживають поворот екрана завдяки власному Saver.
            val settings = rememberSaveable(saver = ThemeSettings.Saver) { ThemeSettings() }
            val darkTheme = settings.isDarkTheme()

            // Колір іконок у статус-барі має відповідати темі застосунку, а не системи.
            // DisposableEffect(darkTheme) перезапускається лише коли тема змінюється.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { darkTheme },
                )
                onDispose { }
            }

            CompositionLocalProvider(LocalThemeSettings provides settings) {
                ShowcaseTheme(
                    darkTheme = darkTheme,
                    dynamicColor = settings.dynamicColor,
                    animate = settings.animateTransition,
                ) {
                    ShowcaseApp()
                }
            }
        }
    }

    private companion object {
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
