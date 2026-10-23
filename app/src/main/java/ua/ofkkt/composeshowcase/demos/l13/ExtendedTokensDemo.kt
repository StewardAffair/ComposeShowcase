package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.theme.extendedColors

/*
 * Лекція 13, слайд «Розширення теми власними токенами».
 * Реалізація — ui/theme/ExtendedColors.kt (+ CompositionLocalProvider у Theme.kt).
 */
@Composable
fun ExtendedTokensDemo() {
    val ext = MaterialTheme.extendedColors
    DemoColumn {
        DemoSection(
            "1. Статусні банери",
            note = "У стандартній ColorScheme є лише error. Кольорів success, warning, info немає — додаємо їх самі.",
        ) {
            StatusBanner(Icons.Filled.CheckCircle, "Оплату успішно проведено", ext.successContainer, ext.onSuccessContainer)
            StatusBanner(Icons.Filled.Warning, "Залишилось 2 дні до дедлайну", ext.warningContainer, ext.onWarningContainer)
            StatusBanner(Icons.Filled.Info, "Доступна нова версія застосунку", ext.infoContainer, ext.onInfoContainer)
        }

        DemoSection("2. Кнопки та бейджі на токенах") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = ext.success, contentColor = ext.onSuccess),
                ) { Text("Підтвердити") }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = ext.warning, contentColor = ext.onWarning),
                ) { Text("Відкласти") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Здано", ext.success, ext.onSuccess)
                Chip("На перевірці", ext.info, ext.onInfo)
                Chip("Прострочено", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
            }
            Tip("Перемкніть темну тему: токени мають окремі значення для світлої та темної схем (LightExtendedColors / DarkExtendedColors).")
        }

        DemoSection("3. Чотири кроки зі слайда") {
            CodeSnippet(
                """
                // 1. data class
                @Immutable
                data class ExtendedColors(val success: Color, val onSuccess: Color, …)

                // 2. CompositionLocal
                val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

                // 3. Provider у темі
                CompositionLocalProvider(LocalExtendedColors provides extended) {
                    MaterialTheme(colorScheme, typography, shapes, content)
                }

                // 4. Читання (зручне розширення)
                val MaterialTheme.extendedColors: ExtendedColors
                    @Composable @ReadOnlyComposable get() = LocalExtendedColors.current

                Icon(tint = MaterialTheme.extendedColors.success)
                """,
            )
        }
    }
}

@Composable
private fun StatusBanner(icon: ImageVector, text: String, container: Color, content: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(container)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = content)
        Column { Text(text, color = content, style = MaterialTheme.typography.bodyLarge) }
    }
}

@Composable
private fun Chip(text: String, bg: Color, fg: Color) {
    Text(
        text,
        color = fg,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
