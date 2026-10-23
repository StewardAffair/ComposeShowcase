@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import ua.ofkkt.composeshowcase.ui.common.LabeledSwitch
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Лекція 11, слайди «LazyColumn / LazyRow», «Типові помилки layout» (№1 fillMaxSize в item, №4 ігнорування key).
 */

private data class Task(val id: Int, val title: String)

private val InitialTasks = listOf(
    "Прочитати конспект", "Налаштувати тему", "Зробити картку товару", "Додати LazyColumn",
    "Написати Preview", "Перевірити контраст", "Відправити PR",
).mapIndexed { i, t -> Task(id = i + 1, title = t) }

@Composable
fun LazyListsDemo() {
    DemoColumn {
        KeysSection()
        StickyHeaderSection()
        LazyRowSection()
    }
}

@Composable
private fun KeysSection() {
    val tasks = remember { mutableStateListOf<Task>().apply { addAll(InitialTasks) } }
    var useKeys by rememberSaveable { mutableStateOf(true) }
    var nextId by remember { mutableIntStateOf(InitialTasks.size + 1) }
    val keyFn: ((Task) -> Any)? = if (useKeys) { { task: Task -> task.id } } else null

    DemoSection(
        "1. key: стан елемента прив'язаний до id чи до позиції?",
        note = "Поставте позначку на 1–2 задачах і натисніть «Перемішати». Потім вимкніть key і повторіть.",
    ) {
        LabeledSwitch("items(…, key = { it.id })", checked = useKeys, onCheckedChange = { useKeys = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = { tasks.shuffle() }) { Text("Перемішати") }
            FilledTonalButton(onClick = { tasks.add(0, Task(nextId, "Нова задача #$nextId")); nextId++ }) { Text("+ згори") }
            FilledTonalButton(onClick = { if (tasks.isNotEmpty()) tasks.removeAt(0) }) { Text("− перша") }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                items(items = tasks, key = keyFn) { task ->
                    // Локальний стан КОЖНОГО елемента. З key — «їде» разом із задачею, без key — лишається на позиції.
                    var done by remember { mutableStateOf(false) }
                    ListItem(
                        headlineContent = { Text(task.title) },
                        supportingContent = { Text("id = ${task.id}") },
                        leadingContent = { Checkbox(checked = done, onCheckedChange = { done = it }) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        // animateItem() працює лише з key — без нього Compose не знає, «куди поїхав» елемент.
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
        Tip(
            if (useKeys) {
                "З key позначки переміщуються разом із задачами, а перестановка анімується."
            } else {
                "Без key позначки «прилипли» до позицій: тепер позначено ІНШІ задачі. Це і є баг зі слайда №4."
            },
            kind = if (useKeys) TipKind.Success else TipKind.Warning,
        )
        CodeSnippet(
            """
            LazyColumn {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(task, Modifier.animateItem())
                }
            }
            """,
        )
    }
}

@Composable
private fun StickyHeaderSection() {
    val names = listOf(
        "Андрій", "Анна", "Арсен", "Богдан", "Борис", "Вадим", "Віра", "Галина", "Григорій",
        "Дарина", "Денис", "Єва", "Євген", "Зоряна", "Ігор", "Ірина", "Катерина", "Кирило",
    )
    val grouped = names.groupBy { it.first() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showUp by remember { derivedStateOf { listState.firstVisibleItemIndex > 2 } }

    DemoSection(
        "2. stickyHeader і rememberLazyListState",
        note = "Заголовки-літери «прилипають» до верху. Кнопка «Нагору» з'являється після прокрутки.",
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(MaterialTheme.shapes.medium),
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                grouped.forEach { (letter, group) ->
                    stickyHeader(key = "h_$letter") {
                        Text(
                            letter.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                    items(group, key = { it }) { name ->
                        // Елементу — fillMaxWidth, а НЕ fillMaxSize (типова помилка №1 зі слайда).
                        ListItem(headlineContent = { Text(name) }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            if (showUp) {
                SmallFloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                ) { Icon(Icons.Filled.KeyboardArrowUp, "Нагору") }
            }
        }
    }
}

@Composable
private fun LazyRowSection() {
    val categories = listOf("Усі", "Смартфони", "Ноутбуки", "Навушники", "Годинники", "Планшети", "Аксесуари", "Ігри")
    DemoSection("3. LazyRow", note = "Горизонтальний варіант. contentPadding дає відступ лише на краях списку, а не обрізає вміст.") {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories, key = { it }) { category ->
                AssistChip(onClick = {}, label = { Text(category) })
            }
        }
    }
}
