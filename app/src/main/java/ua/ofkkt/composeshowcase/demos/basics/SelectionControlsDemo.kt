@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import kotlin.math.roundToInt

@Composable
fun SelectionControlsDemo() {
    DemoColumn {
        CheckboxSection()
        RadioSection()
        SwitchSection()
        SliderSection()
        ChipsSection()
        SegmentedSection()
    }
}

@Composable
private fun CheckboxSection() {
    // Стан трьох «дочірніх» чекбоксів; батьківський TriStateCheckbox — похідний стан.
    val labels = listOf("Kotlin", "Compose", "Coroutines")
    val checked = remember { mutableStateListOf(true, false, false) }
    val parentState = when {
        checked.all { it } -> ToggleableState.On
        checked.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }
    DemoSection(
        "1. Checkbox і TriStateCheckbox",
        note = "Батьківський чекбокс не має власного стану — він обчислюється з дочірніх.",
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .triStateToggleable(
                    state = parentState,
                    role = Role.Checkbox,
                    onClick = {
                        val newValue = parentState != ToggleableState.On
                        for (i in checked.indices) checked[i] = newValue
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TriStateCheckbox(state = parentState, onClick = null)
            Text("Вивчено все", style = MaterialTheme.typography.titleSmall)
        }
        labels.forEachIndexed { index, label ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp)
                    .toggleable(value = checked[index], role = Role.Checkbox, onValueChange = { checked[index] = it }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = checked[index], onCheckedChange = null)
                Text(label)
            }
        }
        Tip("onCheckedChange = null у Checkbox + Modifier.toggleable на рядку → клікабельний увесь рядок, а TalkBack бачить один елемент.")
    }
}

@Composable
private fun RadioSection() {
    val options = listOf("Android", "iOS", "Kotlin Multiplatform")
    var selected by rememberSaveable { mutableIntStateOf(2) }
    DemoSection("2. RadioButton-група") {
        Column(Modifier.selectableGroup()) {
            options.forEachIndexed { index, option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(selected = index == selected, role = Role.RadioButton, onClick = { selected = index })
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = index == selected, onClick = null)
                    Text(option, Modifier.padding(start = 8.dp))
                }
            }
        }
        Text("Обрано: ${options[selected]}", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SwitchSection() {
    var notifications by rememberSaveable { mutableStateOf(true) }
    var wifiOnly by rememberSaveable { mutableStateOf(false) }
    DemoSection("3. Switch") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Сповіщення", Modifier.weight(1f))
            Switch(checked = notifications, onCheckedChange = { notifications = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Лише Wi-Fi (з іконкою в повзунку)", Modifier.weight(1f))
            Switch(
                checked = wifiOnly,
                onCheckedChange = { wifiOnly = it },
                thumbContent = {
                    Icon(
                        if (wifiOnly) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                },
            )
        }
    }
}

@Composable
private fun SliderSection() {
    var volume by rememberSaveable { mutableFloatStateOf(0.4f) }
    var rating by rememberSaveable { mutableFloatStateOf(3f) }
    var range by remember { mutableStateOf(200f..800f) }
    DemoSection("4. Slider і RangeSlider") {
        Text("Гучність: ${(volume * 100).roundToInt()}%")
        Slider(value = volume, onValueChange = { volume = it })
        Text("Оцінка (steps = 3 → значення 1…5): ${rating.roundToInt()}")
        Slider(value = rating, onValueChange = { rating = it }, valueRange = 1f..5f, steps = 3)
        Text("Ціна: від ${range.start.roundToInt()} до ${range.endInclusive.roundToInt()} грн")
        RangeSlider(value = range, onValueChange = { range = it }, valueRange = 0f..1000f)
    }
}

@Composable
private fun ChipsSection() {
    val tags = remember { mutableStateListOf("Compose", "Kotlin", "Android") }
    var onlyFree by rememberSaveable { mutableStateOf(false) }
    DemoSection(
        "5. Chips",
        note = "Assist — дія, Filter — фільтр (вкл/викл), Input — введене користувачем значення, Suggestion — підказка.",
    ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { },
                label = { Text("Додати в календар") },
                leadingIcon = { Icon(Icons.Filled.Event, null, Modifier.size(AssistChipDefaults.IconSize)) },
            )
            FilterChip(
                selected = onlyFree,
                onClick = { onlyFree = !onlyFree },
                label = { Text("Безкоштовні") },
                leadingIcon = if (onlyFree) {
                    { Icon(Icons.Filled.Done, null, Modifier.size(FilterChipDefaults.IconSize)) }
                } else {
                    null
                },
            )
            SuggestionChip(onClick = { tags.add("Material3") }, label = { Text("+ Material3") })
        }
        Text("Теги (натисніть ✕, щоб видалити):", style = MaterialTheme.typography.bodySmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tags.forEach { tag ->
                InputChip(
                    selected = false,
                    onClick = { tags.remove(tag) },
                    label = { Text(tag) },
                    trailingIcon = { Icon(Icons.Filled.Close, "Видалити $tag", Modifier.size(InputChipDefaults.IconSize)) },
                )
            }
        }
    }
}

@Composable
private fun SegmentedSection() {
    val options = listOf("День", "Тиждень", "Місяць")
    var selected by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("6. SegmentedButton", note = "Перемикач між 2–5 взаємовиключними варіантами.") {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = index == selected,
                    onClick = { selected = index },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                ) { Text(label) }
            }
        }
        Text("Період: ${options[selected]}", style = MaterialTheme.typography.bodySmall)
    }
}
