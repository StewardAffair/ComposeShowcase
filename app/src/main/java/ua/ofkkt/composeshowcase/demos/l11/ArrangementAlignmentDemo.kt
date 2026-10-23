package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

/* Лекція 11, слайди «Arrangement: розподіл простору вздовж осі» і «Alignment: вирівнювання впоперек осі». */

private enum class Container { Row, Column }

private enum class Arr(val rowCode: String, val columnCode: String) {
    Start("Arrangement.Start", "Arrangement.Top"),
    Center("Arrangement.Center", "Arrangement.Center"),
    End("Arrangement.End", "Arrangement.Bottom"),
    SpaceBetween("Arrangement.SpaceBetween", "Arrangement.SpaceBetween"),
    SpaceAround("Arrangement.SpaceAround", "Arrangement.SpaceAround"),
    SpaceEvenly("Arrangement.SpaceEvenly", "Arrangement.SpaceEvenly"),
    SpacedBy("Arrangement.spacedBy(8.dp)", "Arrangement.spacedBy(8.dp)"),
}

private enum class Align(val rowCode: String, val columnCode: String) {
    Start("Alignment.Top", "Alignment.Start"),
    Center("Alignment.CenterVertically", "Alignment.CenterHorizontally"),
    End("Alignment.Bottom", "Alignment.End"),
}

private fun Arr.horizontal(): Arrangement.Horizontal = when (this) {
    Arr.Start -> Arrangement.Start
    Arr.Center -> Arrangement.Center
    Arr.End -> Arrangement.End
    Arr.SpaceBetween -> Arrangement.SpaceBetween
    Arr.SpaceAround -> Arrangement.SpaceAround
    Arr.SpaceEvenly -> Arrangement.SpaceEvenly
    Arr.SpacedBy -> Arrangement.spacedBy(8.dp)
}

private fun Arr.vertical(): Arrangement.Vertical = when (this) {
    Arr.Start -> Arrangement.Top
    Arr.Center -> Arrangement.Center
    Arr.End -> Arrangement.Bottom
    Arr.SpaceBetween -> Arrangement.SpaceBetween
    Arr.SpaceAround -> Arrangement.SpaceAround
    Arr.SpaceEvenly -> Arrangement.SpaceEvenly
    Arr.SpacedBy -> Arrangement.spacedBy(8.dp)
}

private fun Align.vertical(): Alignment.Vertical = when (this) {
    Align.Start -> Alignment.Top
    Align.Center -> Alignment.CenterVertically
    Align.End -> Alignment.Bottom
}

private fun Align.horizontal(): Alignment.Horizontal = when (this) {
    Align.Start -> Alignment.Start
    Align.Center -> Alignment.CenterHorizontally
    Align.End -> Alignment.End
}

private val BoxAlignments: List<Pair<String, Alignment>> = listOf(
    "TopStart" to Alignment.TopStart, "TopCenter" to Alignment.TopCenter, "TopEnd" to Alignment.TopEnd,
    "CenterStart" to Alignment.CenterStart, "Center" to Alignment.Center, "CenterEnd" to Alignment.CenterEnd,
    "BottomStart" to Alignment.BottomStart, "BottomCenter" to Alignment.BottomCenter, "BottomEnd" to Alignment.BottomEnd,
)

@Composable
fun ArrangementAlignmentDemo() {
    var container by rememberSaveable { mutableStateOf(Container.Row) }
    var arr by rememberSaveable { mutableStateOf(Arr.SpaceBetween) }
    var align by rememberSaveable { mutableStateOf(Align.Center) }

    DemoColumn {
        DemoSection(
            "1. Пісочниця: Row / Column",
            note = "Arrangement — ВЗДОВЖ головної осі (між елементами). Alignment — ВПОПЕРЕК осі.",
        ) {
            Text("Контейнер", style = MaterialTheme.typography.labelLarge)
            ChoiceChips(Container.entries, container, { container = it }, { it.name })
            Text("Arrangement (вздовж осі)", style = MaterialTheme.typography.labelLarge)
            ChoiceChips(Arr.entries, arr, { arr = it }, { it.name })
            Text("Alignment (впоперек осі)", style = MaterialTheme.typography.labelLarge)
            ChoiceChips(Align.entries, align, { align = it }, { it.name })

            val area = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)

            if (container == Container.Row) {
                Row(
                    modifier = area.height(110.dp),
                    horizontalArrangement = arr.horizontal(),
                    verticalAlignment = align.vertical(),
                ) {
                    DemoBlock("A", size = 40.dp, index = 0)
                    DemoBlock("B", size = 64.dp, index = 1)
                    DemoBlock("C", size = 48.dp, index = 2)
                }
                CodeSnippet(
                    """
                    Row(
                        horizontalArrangement = ${arr.rowCode},
                        verticalAlignment = ${align.rowCode},
                    ) { A(); B(); C() }
                    """,
                )
            } else {
                Column(
                    modifier = area.height(260.dp),
                    verticalArrangement = arr.vertical(),
                    horizontalAlignment = align.horizontal(),
                ) {
                    DemoBlock("A", size = 40.dp, index = 0)
                    DemoBlock("B", size = 64.dp, index = 1)
                    DemoBlock("C", size = 48.dp, index = 2)
                }
                CodeSnippet(
                    """
                    Column(
                        verticalArrangement = ${arr.columnCode},
                        horizontalAlignment = ${align.columnCode},
                    ) { A(); B(); C() }
                    """,
                )
            }
            Tip("Arrangement.spacedBy(8.dp) — найпростіший спосіб рівних відступів без Spacer.")
        }

        BoxAlignmentSection()
    }
}

@Composable
private fun BoxAlignmentSection() {
    var selected by rememberSaveable { mutableStateOf(4) }
    DemoSection(
        "2. Box: contentAlignment і Modifier.align()",
        note = "contentAlignment — позиція за замовчуванням для всіх дітей. Modifier.align() — індивідуально для однієї дитини.",
    ) {
        ChoiceChips(BoxAlignments.indices.toList(), selected, { selected = it }, { BoxAlignments[it].first })
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = BoxAlignments[selected].second,
        ) {
            DemoBlock("за зам.", size = 72.dp, index = 0)
            DemoBlock("align", size = 56.dp, index = 1, modifier = Modifier.align(Alignment.TopStart))
        }
        CodeSnippet(
            """
            Box(contentAlignment = Alignment.${BoxAlignments[selected].first}) {
                Child()                                     // позиція за замовчуванням
                Other(Modifier.align(Alignment.TopStart))   // власна позиція
            }
            """,
        )
    }
}
