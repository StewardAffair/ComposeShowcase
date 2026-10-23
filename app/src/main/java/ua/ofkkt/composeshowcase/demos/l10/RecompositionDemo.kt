package ua.ofkkt.composeshowcase.demos.l10

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.RecompositionBadge
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.common.rememberRecompositionCount

/*
 * Лекція 10, слайди «Recomposition: що це таке» та «Що запускає Recomposition».
 *
 * 🔁 N — скільки разів перекомпонувалась відповідна функція.
 * Важливо: Column/Row/Box — inline-функції, вони НЕ створюють окремої області
 * рекомпозиції. Окрема область = окремий @Composable fun.
 */
@Composable
fun RecompositionDemo() {
    DemoColumn {
        DemoSection(
            "1. Хто перемальовується при зміні стану?",
            note = "Натисніть кнопку кілька разів і порівняйте лічильники.",
        ) {
            DirectReadExample()
            Tip(
                "Батько читає count → перекомпоновується. CountDisplay отримує новий count → перекомпоновується. " +
                    "StaticLabel не має параметрів, що змінились → Compose його ПРОПУСКАЄ (skipping).",
            )
        }

        DemoSection(
            "2. Відкладене читання стану (лямбда замість значення)",
            note = "Тепер батько передає не значення, а лямбду { count }. Стан читається вже всередині дитини.",
        ) {
            DeferredReadExample()
            Tip(
                "Батько НЕ читає count під час композиції, тож його лічильник не росте — перекомпоновується лише дитина. " +
                    "Цей прийом корисний для значень, що змінюються дуже часто (скрол, анімація).",
                kind = TipKind.Success,
            )
            CodeSnippet(
                """
                // було:  CountDisplay(count)            → батько читає стан
                // стало: CountDisplayLambda { count }   → читає лише дитина
                @Composable
                fun CountDisplayLambda(countProvider: () -> Int) {
                    Text("count = " + countProvider())
                }
                """,
            )
        }
    }
}

@Composable
private fun DirectReadExample() {
    var count by remember { mutableIntStateOf(0) }
    val parentRecompositions = rememberRecompositionCount()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        RecompositionBadge("Батько (читає count)", parentRecompositions)
        CountDisplay(count)
        StaticLabel()
        Button(onClick = { count++ }) { Text("count++ (зараз $count)") }
    }
}

@Composable
private fun DeferredReadExample() {
    var count by remember { mutableIntStateOf(0) }
    val parentRecompositions = rememberRecompositionCount()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        RecompositionBadge("Батько (НЕ читає count)", parentRecompositions)
        CountDisplayLambda { count }
        StaticLabel()
        Button(onClick = { count++ }) { Text("count++") }
    }
}

@Composable
private fun CountDisplay(count: Int) {
    val n = rememberRecompositionCount()
    RecompositionBadge("CountDisplay(count = $count)", n)
}

@Composable
private fun CountDisplayLambda(countProvider: () -> Int) {
    val n = rememberRecompositionCount()
    RecompositionBadge("CountDisplayLambda → ${countProvider()}", n)
}

@Composable
private fun StaticLabel() {
    val n = rememberRecompositionCount()
    RecompositionBadge("StaticLabel() — без параметрів", n)
}
