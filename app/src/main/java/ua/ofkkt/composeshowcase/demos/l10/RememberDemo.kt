package ua.ofkkt.composeshowcase.demos.l10

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
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
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Лекція 10, слайди «remember та mutableStateOf» і «remember vs rememberSaveable».
 */
@Composable
fun RememberDemo() {
    var showCounters by rememberSaveable { mutableStateOf(true) }

    DemoColumn {
        DemoSection(
            "1. Три способи зберегти лічильник",
            note = "Натисніть кожну кнопку кілька разів, а потім ПОВЕРНІТЬ ЕКРАН (зміна конфігурації).",
        ) {
            LabeledSwitch(
                "Показувати лічильники (вимкніть — composable покине композицію)",
                checked = showCounters,
                onCheckedChange = { showCounters = it },
            )
            if (showCounters) {
                NoRememberCounter()
                RememberCounter()
                RememberSaveableCounter()
            }
            Tip(
                "Результат: без remember — завжди 0; remember — переживає recomposition, але не поворот; " +
                    "rememberSaveable — переживає і поворот, і process death (значення лежить у Bundle). " +
                    "Якщо прибрати composable з композиції — втрачають стан обидва.",
            )
        }

        DemoSection(
            "2. Порівняльна таблиця зі слайда",
        ) {
            ComparisonRow("Механізм", "Поворот", "Process death", header = true)
            ComparisonRow("remember", "❌", "❌")
            ComparisonRow("rememberSaveable", "✅", "✅ (прості типи)")
            ComparisonRow("ViewModel", "✅", "⚠️ лише з SavedStateHandle")
            Tip("ViewModel-варіант дивіться на екрані «ViewModel + StateFlow» у групі «Додаткові теми».")
        }

        DemoSection(
            "3. rememberSaveable для власного класу: Saver",
            note = "Bundle не вміє зберігати довільні класи. Розкладаємо об'єкт на прості значення — mapSaver / listSaver.",
        ) {
            SaverExample()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun NoRememberCounter() {
    // ❌ НЕПРАВИЛЬНО: новий MutableState створюється при кожній recomposition.
    // Клік змінює стан → recomposition → створюється новий стан зі значенням 0.
    var count by mutableIntStateOf(0)
    var clicks by remember { mutableIntStateOf(0) }
    CounterRow(
        title = "Без remember",
        code = "var count by mutableIntStateOf(0)",
        value = count,
        hint = "кліків: $clicks",
        onClick = { count++; clicks++ },
    )
}

@Composable
private fun RememberCounter() {
    var count by remember { mutableIntStateOf(0) }
    CounterRow("remember", "var count by remember { mutableIntStateOf(0) }", count, onClick = { count++ })
}

@Composable
private fun RememberSaveableCounter() {
    var count by rememberSaveable { mutableIntStateOf(0) }
    CounterRow("rememberSaveable", "var count by rememberSaveable { mutableIntStateOf(0) }", count, onClick = { count++ })
}

@Composable
private fun CounterRow(title: String, code: String, value: Int, hint: String? = null, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text("значення: $value" + (hint?.let { " · $it" } ?: ""), style = MaterialTheme.typography.bodyMedium)
            }
            FilledTonalButton(onClick = onClick) { Text("+1") }
        }
        CodeSnippet(code)
    }
}

@Composable
private fun ComparisonRow(a: String, b: String, c: String, header: Boolean = false) {
    val style = if (header) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium
    Row(Modifier.fillMaxWidth()) {
        Text(a, Modifier.weight(1.3f), style = style)
        Text(b, Modifier.weight(0.8f), style = style)
        Text(c, Modifier.weight(1.4f), style = style)
    }
}

/** Фільтр пошуку — власний клас, який не можна покласти в Bundle напряму. */
private data class SearchFilter(val query: String, val minRating: Int)

private val SearchFilterSaver = mapSaver<SearchFilter>(
    save = { mapOf("query" to it.query, "minRating" to it.minRating) },
    restore = { SearchFilter(it["query"] as String, it["minRating"] as Int) },
)

@Composable
private fun SaverExample() {
    var filter by rememberSaveable(stateSaver = SearchFilterSaver) {
        mutableStateOf(SearchFilter(query = "", minRating = 3))
    }
    OutlinedTextField(
        value = filter.query,
        onValueChange = { filter = filter.copy(query = it) },
        label = { Text("Запит") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Мін. рейтинг: ${filter.minRating}", Modifier.weight(1f))
        Button(onClick = { filter = filter.copy(minRating = (filter.minRating % 5) + 1) }) { Text("Змінити") }
    }
    Tip("Поверніть екран — і запит, і рейтинг збережуться.", kind = TipKind.Success)
    CodeSnippet(
        """
        val SearchFilterSaver = mapSaver(
            save = { mapOf("query" to it.query, "minRating" to it.minRating) },
            restore = { SearchFilter(it["query"] as String, it["minRating"] as Int) },
        )
        var filter by rememberSaveable(stateSaver = SearchFilterSaver) {
            mutableStateOf(SearchFilter("", 3))
        }
        """,
    )
}
