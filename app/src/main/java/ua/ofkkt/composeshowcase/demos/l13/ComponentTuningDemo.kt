package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.theme.extendedColors

/* Лекція 13, слайди «Компонентне тюнінгування» і «Рівні кастомізації теми». */
@Composable
fun ComponentTuningDemo() {
    val c = MaterialTheme.colorScheme
    DemoColumn {
        DemoSection(
            "1. Button: ButtonDefaults.buttonColors",
            note = "Деструктивна дія — обґрунтований виняток з теми.",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}) { Text("Звичайна") }
                // Причина: деструктивна дія за дизайн-специфікацією
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = c.error, contentColor = c.onError),
                ) { Text("Видалити") }
                OutlinedButton(onClick = {}, border = BorderStroke(2.dp, c.tertiary)) { Text("Рамка") }
            }
            CodeSnippet(
                """
                Button(
                    onClick = { },
                    // Destructive action per design spec
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) { Text("Видалити") }
                """,
            )
        }

        DemoSection("2. Card: кольори й elevation") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = c.primaryContainer, contentColor = c.onPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) { Text("Картка-акцент на primaryContainer", Modifier.padding(16.dp)) }
        }

        DemoSection("3. TextField: прозорий контейнер") {
            var text by rememberSaveable { mutableStateOf("") }
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Коментар") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
            )
        }

        DemoSection("4. Switch: кольори зі статусного токена") {
            var on by rememberSaveable { mutableStateOf(true) }
            val ext = MaterialTheme.extendedColors
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Автозбереження", Modifier.weight(1f))
                Switch(
                    checked = on,
                    onCheckedChange = { on = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = ext.success, checkedThumbColor = ext.onSuccess),
                )
            }
            Tip("Правило: якщо тюнінгуєте більше 3 компонентів одного типу — краще змінити глобальну тему.", kind = TipKind.Warning)
        }

        DemoSection("5. Чотири рівні кастомізації (таблиця зі слайда)") {
            LevelRow("Глобальний", "ColorScheme застосунку", "низький", c.primaryContainer, c.onPrimaryContainer)
            LevelRow("Секційний", "вкладений MaterialTheme", "середній", c.secondaryContainer, c.onSecondaryContainer)
            LevelRow("Компонентний", "colors = у Button, Card", "середній", c.tertiaryContainer, c.onTertiaryContainer)
            LevelRow("Екземпляра", "Modifier.background(Color(…))", "високий", c.errorContainer, c.onErrorContainer)
            Tip("Чим нижчий рівень без потреби — тим більше непослідовностей з часом. Хардкод кольору — найгірший варіант.")
        }
    }
}

@Composable
private fun LevelRow(level: String, example: String, risk: String, bg: Color, fg: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(level, color = fg, style = MaterialTheme.typography.titleSmall)
            Text(example, color = fg, style = MaterialTheme.typography.bodySmall)
        }
        Text("ризик: $risk", color = fg, style = MaterialTheme.typography.labelMedium)
    }
}
