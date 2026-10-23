package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/* Лекція 13, слайд «Типографіка: кастомні шрифти». Налаштування теми — ui/theme/Type.kt */

private fun Typography.allStyles(): List<Pair<String, TextStyle>> = listOf(
    "displayLarge" to displayLarge, "displayMedium" to displayMedium, "displaySmall" to displaySmall,
    "headlineLarge" to headlineLarge, "headlineMedium" to headlineMedium, "headlineSmall" to headlineSmall,
    "titleLarge" to titleLarge, "titleMedium" to titleMedium, "titleSmall" to titleSmall,
    "bodyLarge" to bodyLarge, "bodyMedium" to bodyMedium, "bodySmall" to bodySmall,
    "labelLarge" to labelLarge, "labelMedium" to labelMedium, "labelSmall" to labelSmall,
)

@Composable
fun TypographyDemo() {
    var fontScale by rememberSaveable { mutableFloatStateOf(1f) }
    DemoColumn {
        DemoSection(
            "1. Шкала типографіки Material3",
            note = "Display / Headline — «бренд-шрифтом» (Serif з Type.kt), Title / Body / Label — системним.",
        ) {
            MaterialTheme.typography.allStyles().forEach { (name, style) ->
                Column {
                    Text(name, style = style, maxLines = 1)
                    Text(
                        "${style.fontSize.value.toInt()}sp · line ${style.lineHeight.value.toInt()}sp · ${style.fontWeight?.weight ?: 400}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        DemoSection(
            "2. Великий шрифт (доступність)",
            note = "Повзунок імітує системне налаштування «Розмір шрифту». Розміри в sp масштабуються, у dp — ні.",
        ) {
            Text("fontScale = ${"%.2f".format(fontScale)}", style = MaterialTheme.typography.labelLarge)
            Slider(value = fontScale, onValueChange = { fontScale = it }, valueRange = 0.85f..2f)
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Заголовок картки", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Основний текст має залишатися читабельним і не обрізатися навіть при fontScale = 2.0. " +
                            "Не задавайте фіксовану висоту контейнерам з текстом.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Tip("Перевіряйте екрани з великим шрифтом — це пункт чек-листа перед релізом (слайд «Тестування теми»).", kind = TipKind.Warning)
        }

        DemoSection("3. Як підключити власний шрифт") {
            CodeSnippet(
                """
                // 1) файли → app/src/main/res/font/brand_regular.ttf, brand_bold.ttf
                // 2) FontFamily
                val Brand = FontFamily(
                    Font(R.font.brand_regular, FontWeight.Normal),
                    Font(R.font.brand_bold, FontWeight.Bold),
                )
                // 3) Typography
                val AppTypography = Typography(
                    titleLarge = TextStyle(fontFamily = Brand, fontWeight = FontWeight.Bold, fontSize = 22.sp),
                )
                // 4) MaterialTheme(typography = AppTypography) { … }
                """,
            )
            Text(
                "Шрифти OFL / Apache 2.0 (більшість Google Fonts) — безпечні для комерційного використання.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Default,
            )
        }
    }
}
