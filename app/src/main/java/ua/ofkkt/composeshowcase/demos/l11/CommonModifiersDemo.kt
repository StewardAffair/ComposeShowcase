package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import kotlin.math.roundToInt

/* Лекція 11, слайд «Поширені модифікатори». */
@Composable
fun CommonModifiersDemo() {
    DemoColumn {
        WeightSection()
        FractionSection()
        AspectRatioSection()
        VisualSection()
        IntrinsicSection()
    }
}

@Composable
private fun WeightSection() {
    var ratio by rememberSaveable { mutableFloatStateOf(2f) }
    DemoSection(
        "1. weight() — пропорційний розподіл",
        note = "Вільна ширина Row ділиться пропорційно вагам. weight доступний лише в RowScope / ColumnScope — у Box він не скомпілюється.",
    ) {
        Text("weight(1f) і weight(${"%.1f".format(ratio)}f)", style = MaterialTheme.typography.labelLarge)
        Slider(value = ratio, onValueChange = { ratio = it }, valueRange = 0.5f..4f)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            DemoBlock("1f", index = 0, modifier = Modifier.weight(1f))
            DemoBlock("${"%.1f".format(ratio)}f", index = 1, modifier = Modifier.weight(ratio))
        }
        Text("Іконка фіксованої ширини + текст weight(1f) займає решту:", style = MaterialTheme.typography.labelLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            DemoBlock("48", index = 0)
            DemoBlock("weight(1f)", index = 1, modifier = Modifier.weight(1f))
            DemoBlock("48", index = 2)
        }
        Text("weight(1f, fill = false) — не більше, ніж треба вмісту:", style = MaterialTheme.typography.labelLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            DemoBlock("fill=false", index = 1, modifier = Modifier.weight(1f, fill = false).width(96.dp))
            DemoBlock("1f", index = 0, modifier = Modifier.weight(1f))
        }
        Tip("Типова помилка №2 зі слайда: два елементи в Row мають ділити простір порівну, але weight не задано — один витісняє інший.", kind = TipKind.Warning)
    }
}

@Composable
private fun FractionSection() {
    var fraction by rememberSaveable { mutableFloatStateOf(0.6f) }
    DemoSection("2. fillMaxWidth(fraction)") {
        Text("fillMaxWidth(${"%.2f".format(fraction)}f)", style = MaterialTheme.typography.labelLarge)
        Slider(value = fraction, onValueChange = { fraction = it })
        DemoBlock("${(fraction * 100).roundToInt()}%", index = 2, modifier = Modifier.fillMaxWidth(fraction))
    }
}

@Composable
private fun AspectRatioSection() {
    DemoSection("3. aspectRatio()", note = "Висота обчислюється з ширини — зручно для превʼю відео 16:9 і квадратних аватарів.") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DemoBlock("16:9", index = 0, modifier = Modifier.weight(1f).aspectRatio(16f / 9f))
            DemoBlock("1:1", index = 1, modifier = Modifier.weight(0.5f).aspectRatio(1f))
        }
    }
}

@Composable
private fun VisualSection() {
    var angle by rememberSaveable { mutableFloatStateOf(15f) }
    DemoSection("4. Вигляд: alpha, rotate, scale, shadow, border") {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            DemoBlock("α .4", index = 0, modifier = Modifier.alpha(0.4f))
            DemoBlock("${angle.roundToInt()}°", index = 1, modifier = Modifier.rotate(angle))
            DemoBlock("×1.3", index = 2, modifier = Modifier.scale(1.3f))
            Box(
                Modifier
                    .shadow(8.dp, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                    .padding(12.dp)
                    .testTag("shadow_box"),
            ) { Text("shadow") }
        }
        Text("rotate: ${angle.roundToInt()}°", style = MaterialTheme.typography.labelLarge)
        Slider(value = angle, onValueChange = { angle = it }, valueRange = 0f..360f)
        Tip("alpha/rotate/scale змінюють лише малювання (graphicsLayer): розмір у layout і сусіди не змінюються. testTag — мітка для UI-тестів.")
    }
}

@Composable
private fun IntrinsicSection() {
    DemoSection(
        "5. IntrinsicSize.Min",
        note = "Роздільник має бути заввишки з найвищий текст. Без IntrinsicSize його висота невідома (fillMaxHeight у Row без висоти).",
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                .padding(8.dp),
        ) {
            Text("Короткий", Modifier.weight(1f))
            VerticalDivider(Modifier.fillMaxHeight().padding(horizontal = 8.dp))
            Column(Modifier.weight(1f)) {
                Text("Довгий текст")
                Text("у кілька")
                Text("рядків")
            }
        }
        CodeSnippet(
            """
            Row(Modifier.height(IntrinsicSize.Min)) {
                Text("Короткий")
                VerticalDivider(Modifier.fillMaxHeight())
                Column { … }
            }
            """,
        )
    }
}
