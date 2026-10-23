package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.theme.extendedColors
import kotlin.math.max
import kotlin.math.min

/* Лекція 13, слайд «Accessibility та контраст». */

/** Коефіцієнт контрасту WCAG 2.x: (L1 + 0.05) / (L2 + 0.05), де L — відносна яскравість. */
private fun contrastRatio(a: Color, b: Color): Double {
    val la = a.luminance() + 0.05
    val lb = b.luminance() + 0.05
    return max(la, lb) / min(la, lb)
}

private class Pair2(val name: String, val fg: Color, val bg: Color)

@Composable
fun AccessibilityDemo() {
    val c = MaterialTheme.colorScheme
    val ext = MaterialTheme.extendedColors
    val pairs = listOf(
        Pair2("onPrimary / primary", c.onPrimary, c.primary),
        Pair2("onPrimaryContainer / primaryContainer", c.onPrimaryContainer, c.primaryContainer),
        Pair2("onTertiary / tertiary", c.onTertiary, c.tertiary),
        Pair2("onSurface / surface", c.onSurface, c.surface),
        Pair2("onSurfaceVariant / surface", c.onSurfaceVariant, c.surface),
        Pair2("outline / surface (межі, ≥ 3:1)", c.outline, c.surface),
        Pair2("onSuccess / success", ext.onSuccess, ext.success),
        Pair2("onWarningContainer / warningContainer", ext.onWarningContainer, ext.warningContainer),
        // Навмисно поганий приклад зі слайда «Типові помилки теми» №3 — хардкод «гарного» сірого.
        Pair2("❗ сірий #BDBDBD на білому (хардкод)", Color(0xFFBDBDBD), Color.White),
    )

    DemoColumn {
        DemoSection(
            "1. Контраст пар кольорів поточної теми",
            note = "AA: ≥ 4.5 для звичайного тексту, ≥ 3 для великого (18sp+) та іконок. AAA: ≥ 7. Перемкніть тему — значення перерахуються.",
        ) {
            pairs.forEach { ContrastRow(it) }
            Tip("Material3 ColorScheme спроєктована так, щоб стандартні пари проходили щонайменше AA. Власні кольори — перевіряйте!", kind = TipKind.Warning)
        }

        DemoSection(
            "2. contentDescription",
            note = "Іконка без тексту — обов'язково з описом. Декоративна (поруч є текст) — contentDescription = null.",
        ) {
            CodeSnippet(
                """
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Закрити")   // ✅
                }
                Row { Icon(Icons.Filled.Email, null); Text("Пошта") }          // ✅ декоративна
                """,
            )
        }

        DemoSection(
            "3. Групування: mergeDescendants і clearAndSetSemantics",
            note = "Без групування TalkBack читає аватар, ім'я та рейтинг як три окремі елементи.",
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {}
                    .clip(MaterialTheme.shapes.medium)
                    .background(c.surfaceContainerHigh)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(c.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Person, contentDescription = null, tint = c.onPrimaryContainer) }
                Column(Modifier.weight(1f)) {
                    Text("Ірина Мельник", style = MaterialTheme.typography.titleSmall)
                    Text("Ментор курсу", style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "★★★★☆",
                    color = c.tertiary,
                    modifier = Modifier.clearAndSetSemantics { contentDescription = "Рейтинг 4 з 5" },
                )
            }
            Tip("TalkBack прочитає: «Ірина Мельник, Ментор курсу, Рейтинг 4 з 5» — одним елементом, а не «чорна зірка ×4».")
        }

        DemoSection(
            "4. Мінімальна зона дотику 48 dp",
            note = "Ліворуч — клікабельна зона 24 dp (важко влучити пальцем). Праворуч — minimumInteractiveComponentSize() розширює її до 48 dp.",
        ) {
            var clicks by remember { mutableIntStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .border(1.dp, c.error)
                        .clickable(role = Role.Button, onClickLabel = "Закрити") { clicks++ }
                        .size(24.dp),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Close, contentDescription = "Закрити (24 dp)") }
                Box(
                    Modifier
                        .minimumInteractiveComponentSize()
                        .border(1.dp, ext.success)
                        .clickable(role = Role.Button) { clicks++ },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Close, contentDescription = "Закрити (48 dp)", modifier = Modifier.size(24.dp)) }
                Text("натискань: $clicks", Modifier.width(120.dp))
            }
            Tip("IconButton, Checkbox, Switch, RadioButton уже мають зону 48 dp — це ще одна причина використовувати готові компоненти.", kind = TipKind.Success)
        }

        DemoSection("5. Як перевіряти") {
            Text(
                "• TalkBack: Налаштування → Спеціальні можливості → TalkBack\n" +
                    "• Accessibility Scanner (застосунок від Google)\n" +
                    "• Material Theme Builder / WebAIM Contrast Checker — для кольорів\n" +
                    "• Великий шрифт — екран «Типографіка» цього застосунку",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ContrastRow(pair: Pair2) {
    val ratio = contrastRatio(pair.fg, pair.bg)
    val badge = when {
        ratio >= 7.0 -> "AAA"
        ratio >= 4.5 -> "AA"
        ratio >= 3.0 -> "AA large"
        else -> "✗ FAIL"
    }
    val ext = MaterialTheme.extendedColors
    val badgeBg = when {
        ratio >= 4.5 -> ext.successContainer
        ratio >= 3.0 -> ext.warningContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val badgeFg = when {
        ratio >= 4.5 -> ext.onSuccessContainer
        ratio >= 3.0 -> ext.onWarningContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier
                .size(width = 52.dp, height = 40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(pair.bg)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center,
        ) { Text("Aa", color = pair.fg, style = MaterialTheme.typography.titleMedium) }
        Column(Modifier.weight(1f)) {
            Text(pair.name, style = MaterialTheme.typography.bodySmall)
            Text("%.2f : 1".format(ratio), style = MaterialTheme.typography.labelLarge)
        }
        Text(
            badge,
            color = badgeFg,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(badgeBg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
