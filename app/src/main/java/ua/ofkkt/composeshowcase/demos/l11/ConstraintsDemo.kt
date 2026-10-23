package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Лекція 11, слайд «Constraints: як передається розмір».
 * Батько дає обмеження (min/max) → дитина вимірюється і обирає розмір у цих межах →
 * повертає розмір → батько розміщує дитину.
 */
@Composable
fun ConstraintsDemo() {
    var widthFraction by rememberSaveable { mutableFloatStateOf(1f) }

    DemoColumn {
        DemoSection(
            "1. BoxWithConstraints: що отримує дитина",
            note = "Змініть ширину контейнера. При maxWidth < 300 dp макет перемикається з Row на Column.",
        ) {
            Text("Ширина батька: ${(widthFraction * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
            Slider(value = widthFraction, onValueChange = { widthFraction = it }, valueRange = 0.4f..1f)

            BoxWithConstraints(
                Modifier
                    .fillMaxWidth(widthFraction)
                    .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small),
            ) {
                // maxWidth, minWidth, maxHeight, minHeight — у dp, вже доступні тут.
                val compact = maxWidth < 300.dp
                val boundedHeight = constraints.hasBoundedHeight
                val maxH = maxHeight
                val minW = minWidth
                val maxW = maxWidth
                val minH = minHeight
                Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
                    Text(
                        "minWidth = ${minW.fmt()}  maxWidth = ${maxW.fmt()}\n" +
                            "minHeight = ${minH.fmt()}  maxHeight = ${if (boundedHeight) maxH.fmt() else "∞"}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (compact) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { Blocks() }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Blocks() }
                    }
                    Text(if (compact) "Компактний макет: Column" else "Широкий макет: Row", style = MaterialTheme.typography.labelLarge)
                }
            }
            Tip(
                "maxHeight = ∞, бо контейнер лежить у вертикально прокручуваному списку: прокрутка дає дитині " +
                    "необмежену висоту. Тому fillMaxHeight() всередині скролу нічого не робить.",
            )
        }

        DemoSection(
            "2. size vs requiredSize",
            note = "Батько 100×100 dp. Дитина просить 140×140 dp.",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Parent100 { DemoBlock("size", index = 0, modifier = Modifier.size(140.dp), size = 140.dp) }
                    Text("size(140) → 100", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Parent100 { DemoBlock("required", index = 1, modifier = Modifier.requiredSize(140.dp), size = 140.dp) }
                    Text("requiredSize → 140", style = MaterialTheme.typography.labelSmall)
                }
            }
            Tip(
                "size() поважає обмеження батька і «стискається». requiredSize() ігнорує їх і вилазить за межі — " +
                    "використовуйте рідко й свідомо.",
                kind = TipKind.Warning,
            )
        }

        DemoSection(
            "3. wrapContentSize — «не розтягуйся»",
            note = "Батько дає жорсткі обмеження 100% × 80 dp. wrapContentSize дозволяє дитині бути меншою і вирівнятися.",
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                DemoBlock("wrap", index = 2, modifier = Modifier.fillMaxWidth().height(80.dp).wrapContentSize(Alignment.CenterEnd))
            }
            CodeSnippet("Modifier.fillMaxWidth().height(80.dp).wrapContentSize(Alignment.CenterEnd).size(48.dp)")
        }
    }
}

@Composable
private fun Blocks() {
    DemoBlock("A", index = 0)
    DemoBlock("B", index = 1)
    DemoBlock("C", index = 2)
}

@Composable
private fun Parent100(content: @Composable () -> Unit) {
    Box(
        Modifier
            .size(100.dp)
            .border(2.dp, MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.Center,
    ) { content() }
}

private fun Dp.fmt(): String = "${value.toInt()}dp"
