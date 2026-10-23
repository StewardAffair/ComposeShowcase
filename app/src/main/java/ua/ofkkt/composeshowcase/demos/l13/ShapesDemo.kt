@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.demos.l13

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

/* Лекція 13, слайд «Shapes: поглиблено». Налаштування — ui/theme/Shape.kt */

private val SentBubble = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
private val ReceivedBubble = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)

/** Власна форма: «квиток» з трикутним вирізом зліва та справа. */
private val TicketShape = GenericShape { size, _ ->
    val notch = size.height / 4
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height / 2 - notch / 2)
    lineTo(size.width - notch / 2, size.height / 2)
    lineTo(size.width, size.height / 2 + notch / 2)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    lineTo(0f, size.height / 2 + notch / 2)
    lineTo(notch / 2, size.height / 2)
    lineTo(0f, size.height / 2 - notch / 2)
    close()
}

@Composable
fun ShapesDemo() {
    val s = MaterialTheme.shapes
    DemoColumn {
        DemoSection("1. П'ять рівнів форм теми") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ShapeTile("extraSmall\n4dp", s.extraSmall)
                ShapeTile("small\n8dp", s.small)
                ShapeTile("medium\n12dp", s.medium)
                ShapeTile("large\n16dp", s.large)
                ShapeTile("extraLarge\n28dp", s.extraLarge)
            }
        }

        DemoSection("2. Компоненти беруть форму з теми автоматично") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {}) { Text("Button: full") }
                FilterChip(selected = true, onClick = {}, label = { Text("Chip: small") })
            }
            Card(Modifier.fillMaxWidth()) { Text("Card: medium", Modifier.padding(16.dp)) }
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("TextField: extraSmall") }, modifier = Modifier.fillMaxWidth())
        }

        DemoSection("3. Власні форми") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ShapeTile("Пігулка\n50%", RoundedCornerShape(50))
                ShapeTile("CutCorner\n16dp", CutCornerShape(16.dp))
                ShapeTile("Асиметрія", RoundedCornerShape(topStart = 32.dp, bottomEnd = 32.dp))
                ShapeTile("Generic\nквиток", TicketShape)
            }
            CodeSnippet(
                """
                val TicketShape = GenericShape { size, _ ->
                    moveTo(0f, 0f); lineTo(size.width, 0f); /* … */ close()
                }
                Box(Modifier.clip(TicketShape).background(primaryContainer))
                """,
            )
        }

        DemoSection("4. Chat-бульбашки: асиметричне заокруглення") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                Bubble("Привіт! Як працює clip?", ReceivedBubble, sent = false)
                Bubble("Обрізає вміст за формою — і фон, і ripple 🙂", SentBubble, sent = true)
                Bubble("Дякую!", ReceivedBubble, sent = false)
            }
            Tip("Визначте форми один раз у темі — непослідовне заокруглення виглядає непрофесійно.")
        }
    }
}

@Composable
private fun ShapeTile(label: String, shape: Shape) {
    Box(
        Modifier
            .size(width = 96.dp, height = 72.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun Bubble(text: String, shape: Shape, sent: Boolean) {
    val c = MaterialTheme.colorScheme
    Box(Modifier.fillMaxWidth(), contentAlignment = if (sent) Alignment.CenterEnd else Alignment.CenterStart) {
        Text(
            text,
            color = if (sent) c.onPrimary else c.onSurface,
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(shape)
                .background(if (sent) c.primary else c.surfaceContainerHighest)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}
