@file:OptIn(ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.demos.l13

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LabeledSwitch
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.theme.LocalThemeSettings
import ua.ofkkt.composeshowcase.ui.theme.ThemeMode
import ua.ofkkt.composeshowcase.ui.theme.supportsDynamicColor

/*
 * Лекція 13, слайди «Перемикання світлої/темної теми», «Dynamic Color: реалізація», «Анімований перехід теми».
 * Стан — ThemeSettings (ui/theme/ThemeSettings.kt), застосування — ShowcaseTheme (ui/theme/Theme.kt).
 */
@Composable
fun ThemeSwitcherDemo() {
    val settings = LocalThemeSettings.current
    DemoColumn {
        DemoSection(
            "1. Режим теми",
            note = "Три режими: Системна (за замовчуванням) слідує за налаштуваннями пристрою.",
        ) {
            val modes = ThemeMode.entries
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                modes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = settings.mode == mode,
                        onClick = { settings.mode = mode },
                        shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                    ) { Text(mode.label) }
                }
            }
            Text(
                "isSystemInDarkTheme() = ${isSystemInDarkTheme()}",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        DemoSection(
            "2. Dynamic Color (Material You)",
            note = "Кольори генеруються зі шпалер пристрою. Доступно з Android 12 (API 31).",
        ) {
            LabeledSwitch(
                "Dynamic Color",
                checked = settings.dynamicColor && supportsDynamicColor,
                onCheckedChange = { settings.dynamicColor = it },
                enabled = supportsDynamicColor,
            )
            if (supportsDynamicColor) {
                Tip("Змініть шпалери пристрою й поверніться — палітра застосунку підлаштується.", kind = TipKind.Success)
            } else {
                Tip(
                    "На цьому пристрої Android API ${Build.VERSION.SDK_INT} < 31: Dynamic Color недоступний, працює статичний fallback.",
                    kind = TipKind.Warning,
                )
            }
            CodeSnippet(
                """
                val colorScheme = when {
                    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                    dark -> DarkColors      // fallback обов'язковий!
                    else -> LightColors
                }
                """,
            )
            Tip("Зважте: Dynamic Color замінює фірмові кольори. Для сильного бренду його часто не вмикають.")
        }

        DemoSection("3. Анімований перехід") {
            LabeledSwitch(
                "Плавний перехід 300 мс (animateColorAsState)",
                checked = settings.animateTransition,
                onCheckedChange = { settings.animateTransition = it },
            )
            Text(
                "Перемикайте тему з увімкненою і вимкненою анімацією — різкий стрибок помітно одразу.",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        DemoSection("4. Попередній перегляд компонентів") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {}) { Text("Button") }
                FilledTonalButton(onClick = {}) { Text("Tonal") }
                OutlinedButton(onClick = {}) { Text("Outlined") }
            }
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Картка на surfaceContainerHighest", Modifier.weight(1f))
                    Switch(checked = true, onCheckedChange = null)
                }
            }
            FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Favorite, contentDescription = "Подобається") }
            Tip("Збереження вибору між запусками — DataStore Preferences (домашнє завдання 2 Лекції 13). Тут вибір переживає лише поворот екрана.")
        }
    }
}
