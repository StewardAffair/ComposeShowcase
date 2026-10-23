package ua.ofkkt.composeshowcase.demos.l10

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LabeledSwitch
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import kotlin.math.roundToInt

/*
 * Лекція 10, слайди «LaunchedEffect», «DisposableEffect та SideEffect».
 * Доповнення: rememberCoroutineScope, rememberUpdatedState, snapshotFlow.
 *
 * Угорі — журнал подій: у ньому видно, коли запускається і скасовується кожен ефект.
 */

private val Profiles = listOf("Олена", "Тарас", "Ірина")

/** Простий журнал подій із часом від відкриття екрана. */
private class EventLog(private val startedAt: Long = System.currentTimeMillis()) {
    val entries = mutableStateListOf<String>()
    fun add(message: String) {
        val seconds = (System.currentTimeMillis() - startedAt) / 1000.0
        entries.add(0, "+%.1fс  %s".format(seconds, message))
        if (entries.size > 40) entries.removeAt(entries.lastIndex)
    }
}

/** «Зовнішній» не-Compose об'єкт, який треба синхронізувати зі станом (для SideEffect). */
private class FakeAnalytics {
    var currentUser: String = "—"
    var syncCount: Int = 0
}

@Composable
fun SideEffectsDemo() {
    val log = remember { EventLog() }

    // Одноразовий ефект при першому показі екрана.
    LaunchedEffect(Unit) { log.add("LaunchedEffect(Unit): екран показано") }

    Column {
        LogPane(log)
        DemoColumn(Modifier.weight(1f)) {
            LaunchedEffectSection(log)
            CoroutineScopeSection(log)
            DisposableSection(log)
            SideEffectSection(log)
            UpdatedStateSection(log)
            SnapshotFlowSection(log)
        }
    }
}

@Composable
private fun LogPane(log: EventLog) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Журнал подій", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { log.entries.clear() }) { Text("Очистити") }
        }
        LazyColumn {
            items(log.entries) { entry ->
                Text(entry, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun LaunchedEffectSection(log: EventLog) {
    var userId by remember { mutableIntStateOf(0) }
    var loadedFor by remember { mutableStateOf<Int?>(null) }

    // Перезапускається при зміні userId; попередня корутина скасовується автоматично.
    LaunchedEffect(userId) {
        log.add("▶ LaunchedEffect($userId): завантаження ${Profiles[userId]}…")
        try {
            delay(1500) // імітація мережевого запиту
            loadedFor = userId
            log.add("✔ ${Profiles[userId]} завантажено")
        } catch (e: CancellationException) {
            log.add("✖ запит для ${Profiles[userId]} СКАСОВАНО")
            throw e // корутину, що скасовується, не можна «проковтувати»
        }
    }

    DemoSection(
        "1. LaunchedEffect(key)",
        note = "Швидко перемикайте користувачів: незавершені запити скасовуються (див. журнал).",
    ) {
        ChoiceChips(Profiles.indices.toList(), userId, { userId = it }, { Profiles[it] })
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (loadedFor != userId) {
                CircularProgressIndicator(Modifier.size(24.dp))
                Text("Завантаження…")
            } else {
                Text("Профіль: ${Profiles[userId]}", style = MaterialTheme.typography.titleMedium)
            }
        }
        CodeSnippet(
            """
            LaunchedEffect(userId) {
                // перезапуститься при новому userId
                viewModel.loadUser(userId)
            }
            """,
        )
    }
}

@Composable
private fun CoroutineScopeSection(log: EventLog) {
    val scope = rememberCoroutineScope()
    DemoSection(
        "2. rememberCoroutineScope (доповнення)",
        note = "LaunchedEffect не можна викликати в onClick — це не composable-контекст. Для обробників подій беруть scope.",
    ) {
        Button(onClick = {
            scope.launch {
                log.add("onClick → корутина стартувала")
                repeat(3) { delay(700); log.add("   тік ${it + 1}") }
                log.add("корутина завершилась")
            }
        }) { Text("Запустити корутину з onClick") }
        Tip("Scope прив'язаний до composable: якщо вийти з екрана, корутина скасується.")
    }
}

@Composable
private fun DisposableSection(log: EventLog) {
    var subscribed by remember { mutableStateOf(true) }
    DemoSection(
        "3. DisposableEffect: підписка + очищення",
        note = "Згорніть застосунок (Home) і поверніться — у журналі з'являться ON_PAUSE / ON_STOP / ON_START / ON_RESUME.",
    ) {
        LabeledSwitch("Підписка на Lifecycle активна", checked = subscribed, onCheckedChange = { subscribed = it })
        if (subscribed) LifecycleLogger(log)
        CodeSnippet(
            """
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event -> log(event) }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
            """,
        )
        Tip("Вимкніть перемикач — composable покине композицію і спрацює onDispose.", kind = TipKind.Success)
    }
}

@Composable
private fun LifecycleLogger(log: EventLog) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> log.add("Lifecycle: ${event.name}") }
        lifecycleOwner.lifecycle.addObserver(observer)
        log.add("DisposableEffect: спостерігача додано")
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            log.add("onDispose: спостерігача видалено")
        }
    }
    Text("👂 Слухаю події Lifecycle…", style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun SideEffectSection(log: EventLog) {
    val analytics = remember { FakeAnalytics() }
    var user by remember { mutableIntStateOf(0) }
    DemoSection(
        "4. SideEffect: синхронізація з не-Compose кодом",
        note = "SideEffect виконується після КОЖНОЇ успішної recomposition і передає стан у звичайний об'єкт.",
    ) {
        ChoiceChips(Profiles.indices.toList(), user, { user = it }, { Profiles[it] })
        AnalyticsSync(Profiles[user], analytics)
        OutlinedButton(onClick = {
            log.add("analytics: user=${analytics.currentUser}, синхронізацій=${analytics.syncCount}")
        }) { Text("Прочитати стан analytics") }
        Tip(
            "Не записуйте в State всередині SideEffect: це викличе нову recomposition → новий SideEffect → нескінченний цикл.",
            kind = TipKind.Warning,
        )
    }
}

@Composable
private fun AnalyticsSync(userName: String, analytics: FakeAnalytics) {
    SideEffect {
        analytics.currentUser = userName
        analytics.syncCount++
    }
}

@Composable
private fun UpdatedStateSection(log: EventLog) {
    var message by remember { mutableStateOf("Привіт") }
    var restartKey by remember { mutableIntStateOf(0) }
    DemoSection(
        "5. rememberUpdatedState (доповнення)",
        note = "Натисніть «Старт», а потім протягом 3 секунд змініть повідомлення. Ефект НЕ перезапуститься, але візьме найсвіжіше значення.",
    ) {
        ChoiceChips(listOf("Привіт", "Бувай", "Успіхів"), message, { message = it }, { it })
        Button(onClick = { restartKey++ }) { Text("Старт таймера 3с") }
        if (restartKey > 0) DelayedMessage(message, restartKey, log)
    }
}

@Composable
private fun DelayedMessage(message: String, restartKey: Int, log: EventLog) {
    val currentMessage by rememberUpdatedState(message)
    LaunchedEffect(restartKey) {
        log.add("таймер #$restartKey стартував")
        delay(3000)
        log.add("таймер #$restartKey: «$currentMessage»")
    }
}

@Composable
private fun SnapshotFlowSection(log: EventLog) {
    var value by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        snapshotFlow { (value * 10).roundToInt() }
            .distinctUntilChanged()
            .collect { log.add("snapshotFlow → $it / 10") }
    }
    DemoSection(
        "6. snapshotFlow (доповнення)",
        note = "Перетворює читання State на Flow — зручно, щоб застосувати оператори корутин (distinctUntilChanged, debounce…).",
    ) {
        Slider(value = value, onValueChange = { value = it })
    }
}
