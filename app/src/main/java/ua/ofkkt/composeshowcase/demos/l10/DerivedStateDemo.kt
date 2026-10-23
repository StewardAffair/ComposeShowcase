package ua.ofkkt.composeshowcase.demos.l10

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.RecompositionBadge
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.common.rememberRecompositionCount

/*
 * Лекція 10, слайд «derivedStateOf».
 * derivedStateOf корисний, коли вихідний стан змінюється ЧАСТІШЕ, ніж похідний результат.
 */
@Composable
fun DerivedStateDemo() {
    DemoColumn {
        DemoSection(
            "1. Валідація поля (приклад зі слайда)",
            note = "Введіть кілька символів. Обидва індикатори показують text.length > 3, але перекомпоновуються по-різному.",
        ) {
            var text by rememberSaveable { mutableStateOf("") }
            val isValid by remember { derivedStateOf { text.length > 3 } }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Логін (більше 3 символів)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            // Обидві дитини отримують лямбду — тож стан читається саме в них.
            ValidityIndicator("Без derivedStateOf: { text.length > 3 }") { text.length > 3 }
            ValidityIndicator("З derivedStateOf: { isValid }") { isValid }
            Tip(
                "Перший індикатор читає text — перекомпоновується на КОЖЕН символ. " +
                    "Другий читає похідний стан — лише коли результат змінюється false ↔ true.",
                kind = TipKind.Success,
            )
            CodeSnippet(
                """
                val isValid by remember {
                    derivedStateOf { text.length > 3 }
                }
                """,
            )
        }

        DemoSection(
            "2. Кнопка «Нагору» у списку",
            note = "firstVisibleItemIndex змінюється на кожен прокручений елемент, а потрібна нам лише відповідь «> 0?».",
        ) {
            val listState = rememberLazyListState()
            DirectScrollReader(listState)
            DerivedScrollReader(listState)
            ScrollableList(listState)
            Tip(
                "Прокрутіть список: лічильник «напряму» росте з кожним елементом, «derivedStateOf» — " +
                    "лише двічі (коли кнопка з'являється і зникає).",
            )
        }

        DemoSection("3. Коли НЕ використовувати derivedStateOf") {
            Text(
                "• якщо похідне значення змінюється так само часто, як вихідне (наприклад, fullName = first + last);\n" +
                    "• якщо обчислення не читає жодного State<T> — тоді достатньо remember(key);\n" +
                    "• «про всяк випадок» — derivedStateOf має власну вартість.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Tip("derivedStateOf — це оптимізація, а не заміна звичайному remember.", kind = TipKind.Warning)
        }
    }
}

@Composable
private fun ValidityIndicator(label: String, isValid: () -> Boolean) {
    val n = rememberRecompositionCount()
    val valid = isValid()
    RecompositionBadge("$label → ${if (valid) "✅" else "❌"}", n)
}

@Composable
private fun DirectScrollReader(state: LazyListState) {
    val n = rememberRecompositionCount()
    val showButton = state.firstVisibleItemIndex > 0
    RecompositionBadge("Напряму: показати кнопку = $showButton", n)
}

@Composable
private fun DerivedScrollReader(state: LazyListState) {
    val n = rememberRecompositionCount()
    val showButton by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    RecompositionBadge("derivedStateOf: показати кнопку = $showButton", n)
}

@Composable
private fun ScrollableList(state: LazyListState) {
    val scope = rememberCoroutineScope()
    val showButton by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    Box(
        Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(MaterialTheme.shapes.medium),
    ) {
        LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
            items(50) { index ->
                ListItem(headlineContent = { Text("Елемент #$index") })
            }
        }
        if (showButton) {
            SmallFloatingActionButton(
                onClick = { scope.launch { state.animateScrollToItem(0) } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
            ) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Нагору") }
        }
    }
}
