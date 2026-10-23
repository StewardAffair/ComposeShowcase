package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.theme.LocalThemeSettings
import ua.ofkkt.composeshowcase.ui.theme.isDarkTheme

/* Лекція 13 — ColorScheme: понад 30 семантичних ролей кольору Material3. */

private class Role(val name: String, val color: (ColorScheme) -> Color, val on: (ColorScheme) -> Color, val onName: String)

private val Groups: List<Pair<String, List<Role>>> = listOf(
    "Акцентні" to listOf(
        Role("primary", { it.primary }, { it.onPrimary }, "onPrimary"),
        Role("primaryContainer", { it.primaryContainer }, { it.onPrimaryContainer }, "onPrimaryContainer"),
        Role("secondary", { it.secondary }, { it.onSecondary }, "onSecondary"),
        Role("secondaryContainer", { it.secondaryContainer }, { it.onSecondaryContainer }, "onSecondaryContainer"),
        Role("tertiary", { it.tertiary }, { it.onTertiary }, "onTertiary"),
        Role("tertiaryContainer", { it.tertiaryContainer }, { it.onTertiaryContainer }, "onTertiaryContainer"),
        Role("error", { it.error }, { it.onError }, "onError"),
        Role("errorContainer", { it.errorContainer }, { it.onErrorContainer }, "onErrorContainer"),
    ),
    "Поверхні" to listOf(
        Role("background", { it.background }, { it.onBackground }, "onBackground"),
        Role("surface", { it.surface }, { it.onSurface }, "onSurface"),
        Role("surfaceVariant", { it.surfaceVariant }, { it.onSurfaceVariant }, "onSurfaceVariant"),
        Role("surfaceContainerLowest", { it.surfaceContainerLowest }, { it.onSurface }, "onSurface"),
        Role("surfaceContainerLow", { it.surfaceContainerLow }, { it.onSurface }, "onSurface"),
        Role("surfaceContainer", { it.surfaceContainer }, { it.onSurface }, "onSurface"),
        Role("surfaceContainerHigh", { it.surfaceContainerHigh }, { it.onSurface }, "onSurface"),
        Role("surfaceContainerHighest", { it.surfaceContainerHighest }, { it.onSurface }, "onSurface"),
        Role("inverseSurface", { it.inverseSurface }, { it.inverseOnSurface }, "inverseOnSurface"),
    ),
    "Лінії" to listOf(
        Role("outline", { it.outline }, { it.surface }, "surface"),
        Role("outlineVariant", { it.outlineVariant }, { it.onSurface }, "onSurface"),
    ),
)

@Composable
fun ColorSchemeDemo() {
    val scheme = MaterialTheme.colorScheme
    val settings = LocalThemeSettings.current
    val mode = buildString {
        append(if (settings.isDarkTheme()) "темна" else "світла")
        if (settings.dynamicColor) append(" · Dynamic Color")
    }
    DemoColumn {
        Tip("Поточна схема: $mode. Змініть її на екрані «Перемикач теми і Dynamic Color» — палітра тут оновиться.")
        Groups.forEach { (title, roles) ->
            DemoSection(title) {
                roles.forEach { role -> RoleSwatch(role, scheme) }
            }
        }
        DemoSection("Правило пар", note = "Текст і іконки на кольорі X завжди беріть з ролі onX — так гарантується контраст в обох темах.") {
            Text(
                "Не: Text(color = Color.White) на primary\nТак: Text(color = MaterialTheme.colorScheme.onPrimary)",
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun RoleSwatch(role: Role, scheme: ColorScheme) {
    val bg = role.color(scheme)
    val fg = role.on(scheme)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(role.name, color = fg, style = MaterialTheme.typography.titleSmall)
            Text("текст: ${role.onName}", color = fg, style = MaterialTheme.typography.bodySmall)
        }
        Text(bg.hex(), color = fg, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.labelMedium)
    }
}

internal fun Color.hex(): String = "#%06X".format(toArgb() and 0xFFFFFF)
