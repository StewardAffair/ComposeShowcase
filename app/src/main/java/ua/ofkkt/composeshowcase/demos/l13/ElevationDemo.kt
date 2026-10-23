@file:OptIn(ExperimentalLayoutApi::class)

package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import kotlin.math.roundToInt

/* Лекція 13, слайд «Elevation та tonal color». */
@Composable
fun ElevationDemo() {
    var custom by rememberSaveable { mutableFloatStateOf(6f) }
    DemoColumn {
        DemoSection(
            "1. tonalElevation",
            note = "Чим «вище» поверхня, тим більше до її кольору домішується primary (через surfaceTint). Особливо помітно в темній темі.",
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0, 1, 3, 6, 8, 12).forEach { level ->
                    Surface(
                        tonalElevation = level.dp,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.size(width = 96.dp, height = 64.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) { Text("$level dp") }
                    }
                }
            }
            Text("Власне значення: ${custom.roundToInt()} dp", style = MaterialTheme.typography.labelLarge)
            Slider(value = custom, onValueChange = { custom = it }, valueRange = 0f..24f)
            Surface(
                tonalElevation = custom.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            ) { Box(contentAlignment = Alignment.Center) { Text("Surface(tonalElevation = ${custom.roundToInt()}.dp)") } }
        }

        DemoSection(
            "2. shadowElevation — класична тінь (Material 2)",
            note = "На світлій темі тінь помітна, на темній (особливо AMOLED) — майже ні.",
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(0, 2, 6, 12).forEach { level ->
                    Surface(
                        shadowElevation = level.dp,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(width = 96.dp, height = 64.dp),
                    ) { Box(contentAlignment = Alignment.Center) { Text("тінь $level") } }
                }
            }
        }

        DemoSection(
            "3. Сучасний підхід: ролі surfaceContainer*",
            note = "Нові рекомендації Material3: замість обчислення відтінку від elevation брати готові ролі поверхонь.",
        ) {
            val c = MaterialTheme.colorScheme
            listOf(
                "surfaceContainerLowest" to c.surfaceContainerLowest,
                "surfaceContainerLow" to c.surfaceContainerLow,
                "surfaceContainer" to c.surfaceContainer,
                "surfaceContainerHigh" to c.surfaceContainerHigh,
                "surfaceContainerHighest" to c.surfaceContainerHighest,
            ).forEach { (name, color) -> SurfaceRow(name, color) }
            Tip("Card за замовчуванням використовує surfaceContainerHighest, ElevatedCard — surfaceContainerLow + тінь, діалоги — surfaceContainerHigh.")
        }

        DemoSection("4. Три види карток") {
            Card(Modifier.fillMaxWidth()) { Text("Card (filled)", Modifier.padding(16.dp)) }
            ElevatedCard(Modifier.fillMaxWidth()) { Text("ElevatedCard", Modifier.padding(16.dp)) }
            OutlinedCard(Modifier.fillMaxWidth()) { Text("OutlinedCard", Modifier.padding(16.dp)) }
            Card(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
            ) { Text("Card з cardElevation(8.dp) — натисніть", Modifier.padding(16.dp)) }
            CodeSnippet(
                """
                Card(
                    onClick = { },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
                ) { … }
                """,
            )
        }
    }
}

@Composable
private fun SurfaceRow(name: String, color: Color) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(color, MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }
}
