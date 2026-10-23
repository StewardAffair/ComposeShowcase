package ua.ofkkt.composeshowcase.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/*
 * Лекція 13, слайд «Розширення теми власними токенами» — 4 кроки:
 *  1) data class ExtendedColors
 *  2) LocalExtendedColors = staticCompositionLocalOf { ... }
 *  3) CompositionLocalProvider(LocalExtendedColors provides ...) — див. Theme.kt
 *  4) читання: MaterialTheme.extendedColors.success
 *
 * @Immutable підказує Compose Compiler, що тип стабільний (Лекція 10, «Стабільність типів»).
 */
@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
)

val LightExtendedColors = ExtendedColors(
    success = Color(0xFF2E7D32),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFC8E6C9),
    onSuccessContainer = Color(0xFF0B3D0F),
    warning = Color(0xFF8A5100),
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFFFFDDB8),
    onWarningContainer = Color(0xFF2C1600),
    info = Color(0xFF00639B),
    onInfo = Color(0xFFFFFFFF),
    infoContainer = Color(0xFFCEE5FF),
    onInfoContainer = Color(0xFF001D33),
)

val DarkExtendedColors = ExtendedColors(
    success = Color(0xFF8BD68F),
    onSuccess = Color(0xFF003909),
    successContainer = Color(0xFF1B5E20),
    onSuccessContainer = Color(0xFFC8E6C9),
    warning = Color(0xFFFFB868),
    onWarning = Color(0xFF4A2800),
    warningContainer = Color(0xFF693C00),
    onWarningContainer = Color(0xFFFFDDB8),
    info = Color(0xFF96CCFF),
    onInfo = Color(0xFF003353),
    infoContainer = Color(0xFF004A77),
    onInfoContainer = Color(0xFFCEE5FF),
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

/** Зручний доступ у стилі MaterialTheme.colorScheme: MaterialTheme.extendedColors.success */
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
