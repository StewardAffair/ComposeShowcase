package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.RecompositionBadge
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.common.rememberRecompositionCount

/*
 * Лекція 13, слайди «CompositionLocal: механізм» і «MaterialTheme зсередини».
 */

/** Власний CompositionLocal зі значенням за замовчуванням. */
private val LocalUserName = compositionLocalOf { "Гість" }

/** Два локали для порівняння стратегій оновлення. */
private val LocalDynamicTick = compositionLocalOf { 0 }
private val LocalStaticTick = staticCompositionLocalOf { 0 }

@Composable
fun CompositionLocalDemo() {
    DemoColumn {
        DemoSection(
            "1. Власний CompositionLocal і перевизначення",
            note = "Значення «тече» вниз по дереву без параметрів. Найближчий Provider перевизначає його для свого піддерева.",
        ) {
            Greeting() // за замовчуванням: «Гість»
            CompositionLocalProvider(LocalUserName provides "Роман Євгенович") {
                Greeting()
                Column(Modifier.padding(start = 24.dp)) {
                    CompositionLocalProvider(LocalUserName provides "Студент групи 41") {
                        Greeting()
                    }
                    Greeting()
                }
            }
            CodeSnippet(
                """
                val LocalUserName = compositionLocalOf { "Гість" }

                CompositionLocalProvider(LocalUserName provides "Роман") {
                    Greeting()   // читає LocalUserName.current
                }
                """,
            )
        }

        DemoSection(
            "2. Вбудовані локали: LocalContentColor і LocalTextStyle",
            note = "Surface сам задає LocalContentColor = contentColorFor(color). Тому Text та Icon усередині автоматично контрастні.",
        ) {
            Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small) {
                ContentSample(Modifier.padding(12.dp))
            }
            Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.small) {
                ContentSample(Modifier.padding(12.dp))
            }
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.error) {
                ProvideTextStyle(MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic)) {
                    ContentSample(Modifier.padding(12.dp))
                }
            }
            Tip("ContentSample не має жодного параметра кольору чи стилю — усе береться з CompositionLocal.")
        }

        DemoSection(
            "3. Вкладений MaterialTheme для піддерева",
            note = "MaterialTheme — це обгортка над CompositionLocalProvider. Його можна вкласти, щоб змінити тему розділу (наприклад, «Premium»).",
        ) {
            val base = MaterialTheme.colorScheme
            MaterialTheme(
                colorScheme = base.copy(
                    primary = base.tertiary,
                    onPrimary = base.onTertiary,
                    primaryContainer = base.tertiaryContainer,
                    onPrimaryContainer = base.onTertiaryContainer,
                ),
            ) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Premium-розділ", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        Button(onClick = {}) { Text("Оформити") }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Звичайна тема", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Button(onClick = {}) { Text("Кнопка") }
            }
        }

        StaticVsDynamicSection()
    }
}

@Composable
private fun Greeting() {
    Text("Привіт, ${LocalUserName.current}!", style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun ContentSample(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Star, contentDescription = null)
        Text("Колір і стиль успадковано")
    }
}

/* ---------- compositionLocalOf vs staticCompositionLocalOf ---------- */

@Composable
private fun StaticVsDynamicSection() {
    var tick by remember { mutableIntStateOf(0) }
    DemoSection(
        "4. compositionLocalOf vs staticCompositionLocalOf",
        note = "Натисніть кнопку: значення обох локалів зміниться. Подивіться, хто перекомпоновується.",
    ) {
        Button(onClick = { tick++ }) { Text("Змінити значення (зараз $tick)") }

        Text("compositionLocalOf", style = MaterialTheme.typography.labelLarge)
        CompositionLocalProvider(LocalDynamicTick provides tick) {
            LocalBlock(dynamic = true)
        }
        Text("staticCompositionLocalOf", style = MaterialTheme.typography.labelLarge)
        CompositionLocalProvider(LocalStaticTick provides tick) {
            LocalBlock(dynamic = false)
        }
        Tip(
            "compositionLocalOf відстежує, хто читає значення: перекомпоновується лише «читач». " +
                "staticCompositionLocalOf не відстежує — при зміні перекомпоновується ВСЕ піддерево. " +
                "Тому static — для значень, що майже не змінюються (тема, Context), а compositionLocalOf — для змінних.",
            kind = TipKind.Success,
        )
    }
}

@Composable
private fun LocalBlock(dynamic: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LocalReader(dynamic)
        NonReader()
    }
}

@Composable
private fun LocalReader(dynamic: Boolean) {
    val n = rememberRecompositionCount()
    val value = if (dynamic) LocalDynamicTick.current else LocalStaticTick.current
    RecompositionBadge("Читає Local (= $value)", n)
}

@Composable
private fun NonReader() {
    val n = rememberRecompositionCount()
    RecompositionBadge("НЕ читає Local", n)
}
