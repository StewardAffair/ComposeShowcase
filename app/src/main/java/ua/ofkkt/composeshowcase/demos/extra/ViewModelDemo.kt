package ua.ofkkt.composeshowcase.demos.extra

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Доповнення — місток до блоку «Архітектура»: ViewModel + StateFlow + UDF.
 * Стан екрана — один незмінний UiState. UI лише відображає його і викликає функції-події.
 */

data class TodoItem(val id: Int, val title: String, val done: Boolean = false)

data class TodoUiState(
    val items: ImmutableList<TodoItem> = persistentListOf(),
    val isLoading: Boolean = false,
) {
    val doneCount: Int get() = items.count { it.done }
}

class TodoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()
    private var nextId = 1

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1200) // імітація запиту до репозиторію
            val loaded = listOf("Переглянути Лекцію 10", "Зробити картку товару", "Перевірити контраст теми")
                .map { TodoItem(nextId++, it) }
            _uiState.update { it.copy(items = loaded.toImmutableList(), isLoading = false) }
        }
    }

    fun add(title: String) {
        if (title.isBlank()) return
        _uiState.update { it.copy(items = (it.items + TodoItem(nextId++, title.trim())).toImmutableList()) }
    }

    fun toggle(id: Int) {
        _uiState.update { state ->
            state.copy(items = state.items.map { if (it.id == id) it.copy(done = !it.done) else it }.toImmutableList())
        }
    }

    fun remove(id: Int) {
        _uiState.update { state -> state.copy(items = state.items.filterNot { it.id == id }.toImmutableList()) }
    }
}

@Composable
fun ViewModelDemo(viewModel: TodoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Текст поля — суто UI-стан, тому rememberSaveable, а не ViewModel.
    var input by rememberSaveable { mutableStateOf("") }

    DemoColumn {
        DemoSection(
            "Список справ на ViewModel",
            note = "Додайте кілька справ і поверніть екран: список залишиться — він живе у ViewModel, а не в composable.",
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Нова справа") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = { viewModel.add(input); input = "" }, enabled = input.isNotBlank()) { Text("Додати") }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Виконано ${state.doneCount} з ${state.items.size}", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                OutlinedButton(onClick = viewModel::load, enabled = !state.isLoading) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Text("Завантажити")
                }
            }
            if (state.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                    Text("Завантаження…")
                }
            }
            state.items.forEach { item ->
                TodoRow(item, onToggle = { viewModel.toggle(item.id) }, onDelete = { viewModel.remove(item.id) })
            }
        }

        DemoSection("Як це влаштовано") {
            CodeSnippet(
                """
                class TodoViewModel : ViewModel() {
                    private val _uiState = MutableStateFlow(TodoUiState())
                    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

                    fun toggle(id: Int) = _uiState.update { s -> s.copy(items = …) }
                }

                @Composable
                fun TodoScreen(viewModel: TodoViewModel = viewModel()) {
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    TodoList(state.items, onToggle = viewModel::toggle)   // state ↓  events ↑
                }
                """,
            )
            Tip(
                "Той самий принцип UDF, що й у State hoisting (Лекція 10), але «піднятий» ще вище — до ViewModel. " +
                    "collectAsStateWithLifecycle зупиняє збір, коли екран не видно.",
                kind = TipKind.Success,
            )
        }
    }
}

@Composable
private fun TodoRow(item: TodoItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .toggleable(value = item.done, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = item.done, onCheckedChange = null)
        Text(
            item.title,
            Modifier.weight(1f),
            textDecoration = if (item.done) TextDecoration.LineThrough else null,
            color = if (item.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        )
        IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Видалити «${item.title}»") }
    }
}
