package ua.ofkkt.composeshowcase.demos.l10

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.theme.ShowcaseTheme

/*
 * Лекція 10, слайди «State Hoisting: детальніше» і «State<T> vs MutableState<T>».
 * Принцип UDF: стан тече вниз (параметри), події йдуть вгору (колбеки).
 */

private val Languages = listOf(
    "Kotlin", "Java", "Swift", "Dart", "TypeScript", "JavaScript", "Python", "Go", "Rust", "C#", "C++",
)

@Composable
fun StateHoistingDemo() {
    DemoColumn {
        DemoSection(
            "1. Stateful vs Stateless",
            note = "Два лічильники виглядають однаково. Різниця — де живе стан.",
        ) {
            Text("Stateful: стан усередині, керувати ним ззовні неможливо", style = MaterialTheme.typography.labelLarge)
            StatefulCounter()
            CodeSnippet(
                """
                @Composable
                fun StatefulCounter() {
                    var count by remember { mutableIntStateOf(0) }
                    Button(onClick = { count++ }) { Text("Count: " + count) }
                }
                """,
            )
            Text("Stateless: стан приходить параметром", style = MaterialTheme.typography.labelLarge)
            var hoisted by rememberSaveable { mutableIntStateOf(0) }
            StatelessCounter(count = hoisted, onIncrement = { hoisted++ })
            CodeSnippet(
                """
                @Composable
                fun StatelessCounter(count: Int, onIncrement: () -> Unit) {
                    Button(onClick = onIncrement) { Text("Count: " + count) }
                }
                """,
            )
        }

        DemoSection(
            "2. Навіщо піднімати стан: спільне значення",
            note = "Два stateless-компоненти показують і змінюють ОДИН піднятий стан. Зі stateful-версією це неможливо.",
        ) {
            var shared by rememberSaveable { mutableIntStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatelessCounter(count = shared, onIncrement = { shared++ })
                StatelessCounter(count = shared, onIncrement = { shared += 10 })
            }
            Button(onClick = { shared = 0 }) { Text("Скинути (керування ззовні)") }
            Tip("Stateless-компонент легко тестувати й показувати в @Preview з будь-яким значенням.", kind = TipKind.Success)
        }

        DemoSection(
            "3. Пошук: стан тече вниз, події — вгору",
            note = "SearchField не знає про список. LanguageList не знає про поле. Обидва керуються з батька.",
        ) {
            var query by rememberSaveable { mutableStateOf("") }
            val filtered = Languages.filter { it.contains(query, ignoreCase = true) }
            SearchField(value = query, onValueChange = { query = it })
            LanguageList(items = filtered)
        }

        DemoSection(
            "4. State<T> — передаємо лише для читання",
            note = "Дочірній composable отримує State<Int> і не може його змінити — тільки прочитати .value.",
        ) {
            val counter = remember { mutableIntStateOf(0) }
            ReadOnlyViewer(counter)
            Button(onClick = { counter.intValue++ }) { Text("Змінити у батьку") }
        }
    }
}

@Composable
private fun StatefulCounter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) { Text("Count: $count") }
}

/** Stateless: лише відображає count і повідомляє про подію onIncrement. */
@Composable
fun StatelessCounter(count: Int, onIncrement: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onIncrement, modifier = modifier) { Text("Count: $count") }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Мова програмування") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) { Icon(Icons.Filled.Clear, "Очистити") }
            }
        },
        singleLine = true,
    )
}

@Composable
private fun LanguageList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (items.isEmpty()) Text("Нічого не знайдено", color = MaterialTheme.colorScheme.error)
        items.forEach { Text("• $it") }
    }
}

@Composable
private fun ReadOnlyViewer(state: State<Int>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Дитина бачить: ${state.value}", style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true, name = "Stateless з різними значеннями")
@Composable
private fun StatelessCounterPreview() {
    ShowcaseTheme {
        Column {
            StatelessCounter(count = 0, onIncrement = {})
            StatelessCounter(count = 42, onIncrement = {})
        }
    }
}
