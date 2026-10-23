@file:OptIn(ExperimentalLayoutApi::class)

package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LabeledSwitch
import ua.ofkkt.composeshowcase.ui.common.Tip

@Composable
fun ButtonsDemo() {
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val onClick: () -> Unit = { clicks++ }

    DemoColumn {
        DemoSection(
            "1. П'ять кнопок Material3",
            note = "Від найсильнішого акценту до найслабшого. На екрані — лише одна головна дія (Button).",
        ) {
            Text("Натискань: $clicks", style = MaterialTheme.typography.titleLarge)
            LabeledSwitch("enabled", checked = enabled, onCheckedChange = { enabled = it })
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onClick, enabled = enabled) { Text("Button") }
                FilledTonalButton(onClick = onClick, enabled = enabled) { Text("FilledTonal") }
                ElevatedButton(onClick = onClick, enabled = enabled) { Text("Elevated") }
                OutlinedButton(onClick = onClick, enabled = enabled) { Text("Outlined") }
                TextButton(onClick = onClick, enabled = enabled) { Text("Text") }
            }
            CodeSnippet(
                """
                var clicks by rememberSaveable { mutableIntStateOf(0) }
                Button(onClick = { clicks++ }) { Text("Button") }
                """,
            )
        }

        DemoSection("2. Кнопка з іконкою", note = "Слот content — це RowScope: іконку і текст розміщують поруч.") {
            Button(onClick = onClick, contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("Додати")
            }
            Tip("contentDescription = null, бо поруч є текст «Додати» — TalkBack прочитає його.")
        }

        DemoSection("3. Кнопки-іконки") {
            var favorite by rememberSaveable { mutableStateOf(false) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClick) { Icon(Icons.Filled.Share, contentDescription = "Поділитися") }
                FilledIconButton(onClick = onClick) { Icon(Icons.Filled.Edit, contentDescription = "Редагувати") }
                FilledTonalIconButton(onClick = onClick) { Icon(Icons.Filled.Add, contentDescription = "Додати") }
                OutlinedIconButton(onClick = onClick) { Icon(Icons.Filled.Share, contentDescription = "Поділитися") }
                IconToggleButton(checked = favorite, onCheckedChange = { favorite = it }) {
                    Icon(
                        if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (favorite) "Прибрати з обраного" else "Додати в обране",
                        tint = if (favorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Tip("Іконка без тексту ЗАВЖДИ потребує contentDescription (Лекція 13, Accessibility).")
        }

        DemoSection("4. Floating Action Button", note = "FAB — головна дія екрана. Зазвичай передається у Scaffold(floatingActionButton = …).") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                SmallFloatingActionButton(onClick = onClick) { Icon(Icons.Filled.Add, "Додати") }
                FloatingActionButton(onClick = onClick) { Icon(Icons.Filled.Add, "Додати") }
                LargeFloatingActionButton(onClick = onClick) { Icon(Icons.Filled.Add, "Додати") }
            }
            var expanded by rememberSaveable { mutableStateOf(true) }
            ExtendedFloatingActionButton(
                onClick = { expanded = !expanded },
                expanded = expanded,
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                text = { Text("Новий запис") },
            )
            Text(
                "Натисніть ExtendedFAB — він анімовано згорнеться (параметр expanded).",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
