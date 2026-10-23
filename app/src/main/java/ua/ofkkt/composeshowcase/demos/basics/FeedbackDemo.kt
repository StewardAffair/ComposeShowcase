@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LocalSnackbarHostState
import ua.ofkkt.composeshowcase.ui.common.Tip

@Composable
fun FeedbackDemo() {
    DemoColumn {
        ProgressSection()
        SnackbarSection()
        DialogSection()
        BottomSheetSection()
        MenuAndBadgeSection()
    }
}

@Composable
private fun ProgressSection() {
    var target by remember { mutableFloatStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    // Плавна зміна значення прогресу.
    val progress by animateFloatAsState(targetValue = target, label = "progress")

    // Імітація завантаження: LaunchedEffect(loading) перезапускається, коли loading змінюється.
    LaunchedEffect(loading) {
        if (!loading) return@LaunchedEffect
        target = 0f
        while (target < 1f) {
            delay(150)
            target = (target + 0.07f).coerceAtMost(1f)
        }
        loading = false
    }

    DemoSection(
        "1. Індикатори прогресу",
        note = "Невизначений — коли тривалість невідома. Визначений — коли знаємо частку виконаного.",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator()
            CircularProgressIndicator(progress = { progress })
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        }
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { loading = true }, enabled = !loading) {
            Text(if (loading) "Завантаження…" else "Почати завантаження")
        }
    }
}

@Composable
private fun SnackbarSection() {
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    var items by remember { mutableIntStateOf(5) }

    DemoSection(
        "2. Snackbar з дією",
        note = "showSnackbar — suspend-функція, тому викликаємо її в корутині з rememberCoroutineScope().",
    ) {
        Text("Елементів у кошику: $items")
        Button(
            onClick = {
                if (items > 0) {
                    items--
                    scope.launch {
                        val result = snackbar.showSnackbar(
                            message = "Елемент видалено",
                            actionLabel = "Скасувати",
                            duration = SnackbarDuration.Short,
                        )
                        if (result == SnackbarResult.ActionPerformed) items++
                    }
                }
            },
        ) { Text("Видалити елемент") }
    }
}

@Composable
private fun DialogSection() {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var answer by rememberSaveable { mutableStateOf("—") }

    DemoSection(
        "3. AlertDialog",
        note = "Діалог у Compose — це просто composable, який показується, коли showDialog == true.",
    ) {
        OutlinedButton(onClick = { showDialog = true }) { Text("Видалити акаунт…") }
        Text("Відповідь: $answer", style = MaterialTheme.typography.bodySmall)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; answer = "закрито поза діалогом" },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("Видалити акаунт?") },
            text = { Text("Цю дію неможливо скасувати. Усі ваші дані буде втрачено.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false; answer = "підтверджено" }) { Text("Видалити") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; answer = "скасовано" }) { Text("Скасувати") }
            },
        )
    }
}

@Composable
private fun BottomSheetSection() {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var picked by rememberSaveable { mutableStateOf("—") }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    DemoSection("4. ModalBottomSheet") {
        Button(onClick = { showSheet = true }) { Text("Відкрити меню дій") }
        Text("Обрано: $picked", style = MaterialTheme.typography.bodySmall)
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState) {
            Column(Modifier.padding(bottom = 24.dp)) {
                Text(
                    "Дії з документом",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
                listOf(
                    Icons.Filled.Share to "Поділитися",
                    Icons.Filled.Edit to "Редагувати",
                    Icons.Filled.Delete to "Видалити",
                ).forEach { (icon, label) ->
                    ListItem(
                        headlineContent = { Text(label) },
                        leadingContent = { Icon(icon, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().clickable {
                            picked = label
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showSheet = false
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuAndBadgeSection() {
    var menuOpen by remember { mutableStateOf(false) }
    var lastAction by rememberSaveable { mutableStateOf("—") }
    var notifications by rememberSaveable { mutableIntStateOf(3) }

    DemoSection("5. DropdownMenu і BadgedBox") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.Center) {
            Box {
                IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.MoreVert, "Більше дій") }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Редагувати") },
                        leadingIcon = { Icon(Icons.Filled.Edit, null) },
                        onClick = { lastAction = "Редагувати"; menuOpen = false },
                    )
                    DropdownMenuItem(
                        text = { Text("Поділитися") },
                        leadingIcon = { Icon(Icons.Filled.Share, null) },
                        onClick = { lastAction = "Поділитися"; menuOpen = false },
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Видалити") },
                        leadingIcon = { Icon(Icons.Filled.Delete, null) },
                        onClick = { lastAction = "Видалити"; menuOpen = false },
                    )
                }
            }
            IconButton(onClick = { notifications = 0 }) {
                BadgedBox(badge = { if (notifications > 0) Badge { Text("$notifications") } }) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Сповіщення: $notifications")
                }
            }
            IconButton(onClick = { notifications++ }) {
                BadgedBox(badge = { Badge() }) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Кошик, є нові товари")
                }
            }
        }
        Text("Остання дія з меню: $lastAction", style = MaterialTheme.typography.bodySmall)
        Tip("Натисніть дзвіночок — бейдж зникне; кошик — лічильник сповіщень зросте. BadgedBox — це Box з накладанням (Лекція 11).")
    }
}
