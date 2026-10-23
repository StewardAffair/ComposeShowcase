package ua.ofkkt.composeshowcase.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/*
 * Лекція 13, слайд «Перемикання світлої/темної теми» — три режими: Системна / Світла / Темна.
 *
 * Стан живе у MainActivity через rememberSaveable(saver = ThemeSettings.Saver):
 * переживає поворот екрана (Лекція 10, rememberSaveable + власний Saver).
 * Для збереження між запусками застосунку потрібен DataStore — це домашнє завдання Лекції 13.
 */
enum class ThemeMode(val label: String) { System("Системна"), Light("Світла"), Dark("Темна") }

@Stable
class ThemeSettings(
    mode: ThemeMode = ThemeMode.System,
    dynamicColor: Boolean = false,
    animateTransition: Boolean = true,
) {
    var mode by mutableStateOf(mode)
    var dynamicColor by mutableStateOf(dynamicColor)
    var animateTransition by mutableStateOf(animateTransition)

    companion object {
        /** Власний Saver: розкладаємо об'єкт на прості типи, які вміє зберігати Bundle. */
        val Saver: Saver<ThemeSettings, Any> = listSaver(
            save = { listOf(it.mode.ordinal, it.dynamicColor, it.animateTransition) },
            restore = {
                ThemeSettings(
                    mode = ThemeMode.entries[it[0] as Int],
                    dynamicColor = it[1] as Boolean,
                    animateTransition = it[2] as Boolean,
                )
            },
        )
    }
}

/** Dynamic Color доступний лише з Android 12 (API 31, Build.VERSION_CODES.S). */
val supportsDynamicColor: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun ThemeSettings.isDarkTheme(): Boolean = when (mode) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}

/** Налаштування теми доступні будь-якому екрану через CompositionLocal (Лекція 13). */
val LocalThemeSettings = staticCompositionLocalOf<ThemeSettings> {
    error("ThemeSettings не надано: обгорніть UI у CompositionLocalProvider(LocalThemeSettings provides ...)")
}
