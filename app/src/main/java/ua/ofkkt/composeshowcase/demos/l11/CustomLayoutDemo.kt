package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import kotlin.math.max
import kotlin.math.roundToInt

/* Лекція 11, слайд «Custom Layout: вступ» — три кроки: оголошення Layout → measure → place. */
@Composable
fun CustomLayoutDemo() {
    var step by rememberSaveable { mutableFloatStateOf(32f) }
    var baseline by rememberSaveable { mutableFloatStateOf(32f) }

    DemoColumn {
        DemoSection(
            "1. «Сходи»: власний Layout",
            note = "Кожна дитина розміщується нижче і правіше за попередню. Повзунок змінює крок.",
        ) {
            Text("Крок: ${step.roundToInt()} dp", style = MaterialTheme.typography.labelLarge)
            Slider(value = step, onValueChange = { step = it }, valueRange = 0f..64f)
            StaircaseLayout(step = step.dp) {
                DemoBlock("1", index = 0)
                DemoBlock("2", index = 1)
                DemoBlock("3", index = 2)
                DemoBlock("4", index = 0)
            }
            CodeSnippet(
                """
                Layout(content, modifier) { measurables, constraints ->
                    // 1) measure: кожна дитина повертає Placeable з розміром
                    val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
                    val height = placeables.sumOf { it.height }
                    // 2) place: ми самі задаємо координати
                    layout(constraints.maxWidth, height) {
                        var y = 0
                        placeables.forEachIndexed { i, p ->
                            p.placeRelative(x = i * stepPx, y = y)
                            y += p.height
                        }
                    }
                }
                """,
            )
        }

        DemoSection(
            "2. Спрощений FlowRow",
            note = "Якщо елемент не влазить у рядок — переносимо на наступний. (У Compose є готовий FlowRow, див. «Сітки, Pager».)",
        ) {
            SimpleFlowRow(Modifier.fillMaxWidth()) {
                listOf("Kotlin", "Compose", "Layout", "measure", "place", "Constraints", "Modifier", "Row", "Column", "Box")
                    .forEach { AssistChip(onClick = {}, label = { Text(it) }) }
            }
        }

        DemoSection(
            "3. Modifier.layout: відступ від базової лінії тексту",
            note = "Дизайнери часто задають відстань від верху до БАЗОВОЇ ЛІНІЇ тексту, а не до верху блоку. padding так не вміє.",
        ) {
            Text("Відстань до базової лінії: ${baseline.roundToInt()} dp", style = MaterialTheme.typography.labelLarge)
            Slider(value = baseline, onValueChange = { baseline = it }, valueRange = 16f..64f)
            Row {
                Text(
                    "firstBaselineToTop",
                    Modifier
                        .firstBaselineToTop(baseline.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                )
                Text(
                    "padding(top)",
                    Modifier
                        .padding(start = 16.dp, top = baseline.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                )
            }
            Tip("Custom Layout — просунута тема: для 95% задач вистачає Row / Column / Box.")
        }
    }
}

@Composable
private fun StaircaseLayout(step: Dp, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstraints) }
        val stepPx = step.roundToPx()
        val width = placeables.mapIndexed { i, p -> i * stepPx + p.width }.maxOrNull() ?: 0
        val height = placeables.sumOf { it.height }
        layout(
            width.coerceIn(constraints.minWidth, constraints.maxWidth),
            height.coerceIn(constraints.minHeight, constraints.maxHeight),
        ) {
            var y = 0
            placeables.forEachIndexed { i, placeable ->
                placeable.placeRelative(x = i * stepPx, y = y)
                y += placeable.height
            }
        }
    }
}

@Composable
private fun SimpleFlowRow(modifier: Modifier = Modifier, spacing: Dp = 8.dp, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val gap = spacing.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val positions = ArrayList<IntOffset>(placeables.size)
        var x = 0
        var y = 0
        var rowHeight = 0
        placeables.forEach { p ->
            if (x > 0 && x + p.width > constraints.maxWidth) {
                x = 0
                y += rowHeight + gap
                rowHeight = 0
            }
            positions += IntOffset(x, y)
            x += p.width + gap
            rowHeight = max(rowHeight, p.height)
        }
        layout(constraints.maxWidth, y + rowHeight) {
            placeables.forEachIndexed { i, p -> p.placeRelative(positions[i]) }
        }
    }
}

/** Приклад з документації Compose: відступ від верху до першої базової лінії тексту. */
private fun Modifier.firstBaselineToTop(firstBaselineToTop: Dp) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    val firstBaseline = placeable[FirstBaseline]
    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
    val height = placeable.height + placeableY
    layout(placeable.width, height) {
        placeable.placeRelative(0, placeableY)
    }
}
