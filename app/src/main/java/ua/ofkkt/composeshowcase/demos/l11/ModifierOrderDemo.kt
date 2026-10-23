package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Лекція 11, слайди «Modifier: базовий принцип» і «Порядок Modifier має значення».
 * Читайте ланцюжок зліва направо як «шари»: кожен модифікатор обгортає наступні.
 */
@Composable
fun ModifierOrderDemo() {
    val c = MaterialTheme.colorScheme
    val rounded = RoundedCornerShape(24.dp)

    DemoColumn {
        DemoSection(
            "1. padding ↔ background",
            note = "Тонка рамка — межі елемента 100×100 dp.",
        ) {
            ComparePair(
                leftCode = ".padding(16.dp)\n.background(primary)",
                left = Modifier.size(100.dp).padding(16.dp).background(c.primary),
                rightCode = ".background(primary)\n.padding(16.dp)",
                right = Modifier.size(100.dp).background(c.primary).padding(16.dp),
            )
            Tip("Ліворуч фон лише у внутрішній зоні. Праворуч фон займає весь елемент, а відступ — усередині (текст зсунуто).")
        }

        DemoSection(
            "2. size ↔ background",
            note = "Перевіримо приклад зі слайда.",
        ) {
            ComparePair(
                leftCode = ".size(80.dp)\n.background(primary)",
                left = Modifier.size(80.dp).background(c.primary),
                rightCode = ".background(primary)\n.size(80.dp)",
                right = Modifier.background(c.primary).size(80.dp),
            )
            Tip(
                "Результат ОДНАКОВИЙ. background — це модифікатор малювання: він фарбує рівно ту область, " +
                    "яку отримав обгорнутий елемент після вимірювання. Різниця з'являється, коли між ними є " +
                    "модифікатор розміщення (padding, offset) — як у розділі 1.",
                kind = TipKind.Warning,
            )
        }

        DemoSection("3. clip ↔ background") {
            ComparePair(
                leftCode = ".clip(rounded)\n.background(primary)",
                left = Modifier.size(100.dp).clip(rounded).background(c.primary),
                rightCode = ".background(primary)\n.clip(rounded)",
                right = Modifier.size(100.dp).background(c.primary).clip(rounded),
            )
            Tip("clip обрізає лише те, що малюється ПІСЛЯ нього в ланцюжку. Фон, намальований раніше, лишається квадратним.")
        }

        DemoSection("4. clip ↔ clickable (ripple)", note = "Натисніть обидва елементи і подивіться на хвилю.") {
            ComparePair(
                leftCode = ".clip(rounded)\n.clickable { }",
                left = Modifier.size(100.dp).clip(rounded).clickable { }.background(c.primaryContainer),
                rightCode = ".clickable { }\n.clip(rounded)",
                right = Modifier.size(100.dp).clickable { }.clip(rounded).background(c.primaryContainer),
                contentColor = c.onPrimaryContainer,
            )
            Tip("Правильно: спочатку clip, потім clickable — щоб ripple не виходив за межі заокруглення (слайд «Практика: картка товару»).")
        }

        DemoSection("5. clickable ↔ padding (зона натискання)") {
            ComparePair(
                leftCode = ".clickable { }\n.padding(20.dp)",
                left = Modifier.size(100.dp).clickable { }.padding(20.dp).background(c.tertiary),
                rightCode = ".padding(20.dp)\n.clickable { }",
                right = Modifier.size(100.dp).padding(20.dp).clickable { }.background(c.tertiary),
                contentColor = c.onTertiary,
            )
            Tip("Ліворуч реагує вся область 100 dp (зручно для пальця), праворуч — лише внутрішній квадрат.")
        }

        DemoSection("6. border ↔ padding") {
            ComparePair(
                leftCode = ".border(3.dp)\n.padding(16.dp)",
                left = Modifier.size(100.dp).border(3.dp, c.primary).padding(16.dp),
                rightCode = ".padding(16.dp)\n.border(3.dp)",
                right = Modifier.size(100.dp).padding(16.dp).border(3.dp, c.primary),
                contentColor = c.onSurface,
            )
        }

        DemoSection("7. clip ↔ shadow", note = "Тінь малюється ЗА межами форми, тому clip перед shadow її обрізає.") {
            ComparePair(
                leftCode = ".shadow(8.dp, rounded)\n.background(surface)",
                left = Modifier.size(90.dp).shadow(8.dp, rounded).background(c.surface),
                rightCode = ".clip(rounded)\n.shadow(8.dp, rounded)",
                right = Modifier.size(90.dp).clip(rounded).shadow(8.dp, rounded).background(c.surface),
                contentColor = c.onSurface,
            )
        }

        DemoSection(
            "8. offset vs padding",
            note = "offset зсуває лише малювання і не впливає на сусідів; padding змінює розмір, і сусіди посуваються.",
        ) {
            Text("offset(x = 24.dp)", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DemoBlock("1", index = 0)
                DemoBlock("2", index = 1, modifier = Modifier.offset(x = 24.dp))
                DemoBlock("3", index = 2)
            }
            Text("padding(start = 24.dp)", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DemoBlock("1", index = 0)
                DemoBlock("2", index = 1, modifier = Modifier.padding(start = 24.dp))
                DemoBlock("3", index = 2)
            }
        }

        ChainBuilderSection()
    }
}

/** Дві «клітинки» поруч: однаковий набір модифікаторів у різному порядку. */
@Composable
private fun ComparePair(
    leftCode: String,
    left: Modifier,
    rightCode: String,
    right: Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CompareCell(leftCode, left, contentColor, Modifier.weight(1f))
        CompareCell(rightCode, right, contentColor, Modifier.weight(1f))
    }
}

@Composable
private fun CompareCell(code: String, chain: Modifier, contentColor: Color, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        ) {
            Box(chain, contentAlignment = Alignment.TopStart) {
                Text("Текст", color = contentColor, style = MaterialTheme.typography.labelMedium)
            }
        }
        Text(
            "Modifier$code",
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
    }
}

/* ---------------- Конструктор ланцюжка ---------------- */

private enum class Op(val code: String) {
    Padding(".padding(16.dp)"),
    Background(".background(primary)"),
    Border(".border(4.dp, tertiary, rounded)"),
    Clip(".clip(rounded)"),
}

@Composable
private fun ChainBuilderSection() {
    val ops = remember { mutableStateListOf(Op.Padding, Op.Background, Op.Border, Op.Clip) }
    val c = MaterialTheme.colorScheme
    val rounded = RoundedCornerShape(28.dp)

    // Ланцюжок збирається в тому порядку, у якому операції стоять у списку.
    val chain = ops.fold(Modifier.size(150.dp)) { acc, op ->
        when (op) {
            Op.Padding -> acc.padding(16.dp)
            Op.Background -> acc.background(c.primary)
            Op.Border -> acc.border(4.dp, c.tertiary, rounded)
            Op.Clip -> acc.clip(rounded)
        }
    }

    DemoSection(
        "9. Конструктор ланцюжка",
        note = "Переставляйте модифікатори стрілками й спостерігайте, як змінюється результат.",
    ) {
        ops.forEachIndexed { index, op ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${index + 1}. ${op.code}", Modifier.weight(1f), fontFamily = FontFamily.Monospace)
                IconButton(onClick = { if (index > 0) ops.add(index - 1, ops.removeAt(index)) }, enabled = index > 0) {
                    Icon(Icons.Filled.KeyboardArrowUp, "Вище")
                }
                IconButton(
                    onClick = { if (index < ops.lastIndex) ops.add(index + 1, ops.removeAt(index)) },
                    enabled = index < ops.lastIndex,
                ) { Icon(Icons.Filled.KeyboardArrowDown, "Нижче") }
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.border(1.dp, c.outline.copy(alpha = 0.6f))) {
                Box(chain, contentAlignment = Alignment.Center) {
                    Text("Вміст", color = c.onPrimary)
                }
            }
        }
        CodeSnippet("Modifier\n    .size(150.dp)\n" + ops.joinToString("\n") { "    " + it.code })
    }
}
