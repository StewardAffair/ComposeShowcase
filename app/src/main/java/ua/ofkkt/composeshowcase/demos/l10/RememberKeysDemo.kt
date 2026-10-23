package ua.ofkkt.composeshowcase.demos.l10

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CountHolder
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LabeledSwitch
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Лекція 10, слайд «remember з ключами».
 */
private val Users = listOf("Олена", "Тарас", "Ірина")

private val Students = listOf("Шевчук", "Андрієнко", "Мельник", "Бондар", "Коваленко", "Ткаченко", "Литвин")

@Composable
fun RememberKeysDemo() {
    var userId by rememberSaveable { mutableIntStateOf(0) }

    DemoColumn {
        DemoSection(
            "1. UserForm(userId) — приклад зі слайда",
            note = "Введіть текст у обидва поля, потім перемкніть користувача.",
        ) {
            ChoiceChips(Users.indices.toList(), userId, { userId = it }, { Users[it] })
            UserFormWithKey(userId)
            UserFormWithoutKey(userId)
            Tip(
                "remember(userId) створює новий стан, щойно змінюється ключ, — поле очищується. " +
                    "Без ключа старий текст «переїжджає» до іншого користувача — це баг.",
                kind = TipKind.Warning,
            )
        }

        DemoSection(
            "2. Кешування обчислення",
            note = "Сортування виконується лише коли змінюється ключ ascending, а не при кожній recomposition.",
        ) {
            var ascending by rememberSaveable { mutableStateOf(true) }
            var unrelated by remember { mutableIntStateOf(0) }
            // Звичайний лічильник (не State): запис у State під час композиції — антипатерн.
            val sortRuns = remember { CountHolder() }

            val sorted = remember(ascending) {
                sortRuns.value++
                if (ascending) Students.sorted() else Students.sortedDescending()
            }

            LabeledSwitch("Сортувати за алфавітом А→Я", checked = ascending, onCheckedChange = { ascending = it })
            LabeledSwitch(
                "Інший стан (не ключ), змін: $unrelated",
                checked = unrelated % 2 == 1,
                onCheckedChange = { unrelated++ },
            )
            Text(sorted.joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
            Text("Сортування виконано разів: ${sortRuns.value}", style = MaterialTheme.typography.labelLarge)
            CodeSnippet(
                """
                val sorted = remember(ascending) {
                    if (ascending) students.sorted() else students.sortedDescending()
                }
                """,
            )
        }

        DemoSection("3. Шпаргалка") {
            Text(
                "remember { }           — обчислює один раз, ні на що не реагує\n" +
                    "remember(key) { }      — перераховує при зміні ключа-параметра\n" +
                    "derivedStateOf { }     — перераховує при зміні прочитаного State<T>\n" +
                    "rememberSaveable { }   — як remember, але переживає поворот екрана",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun UserFormWithKey(userId: Int) {
    var inputText by remember(userId) { mutableStateOf("") }
    OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        label = { Text("remember(userId) — нотатка для ${Users[userId]}") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
private fun UserFormWithoutKey(userId: Int) {
    var inputText by remember { mutableStateOf("") }
    OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        label = { Text("remember без ключа — нотатка для ${Users[userId]}") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = true,
    )
}
