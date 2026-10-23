package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.R
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

/* Лекція 11, слайд «Row, Column, Box: базові контейнери». */
@Composable
fun ContainersDemo() {
    DemoColumn {
        DemoSection("Column — вертикально, зверху вниз") {
            Column(
                Modifier.outlined(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DemoBlock("1", index = 0)
                DemoBlock("2", index = 1)
                DemoBlock("3", index = 2)
            }
            CodeSnippet("Column { A(); B(); C() }")
        }

        DemoSection("Row — горизонтально, зліва направо") {
            Row(Modifier.outlined(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoBlock("1", index = 0)
                DemoBlock("2", index = 1)
                DemoBlock("3", index = 2)
            }
            CodeSnippet("Row { A(); B(); C() }")
        }

        DemoSection("Box — накладання по осі Z", note = "Наступна дитина малюється поверх попередньої.") {
            Box(Modifier.outlined()) {
                DemoBlock("1", size = 96.dp, index = 0)
                DemoBlock("2", size = 64.dp, index = 1, modifier = Modifier.offset(16.dp, 16.dp))
                DemoBlock("3", size = 40.dp, index = 2, modifier = Modifier.offset(40.dp, 40.dp))
            }
        }

        DemoSection("Типові сценарії для Box") {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                // 1) бейдж на іконці
                Box {
                    Icon(Icons.Filled.Email, contentDescription = "Пошта", modifier = Modifier.size(40.dp))
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error),
                    )
                }
                // 2) текст поверх зображення з градієнтом
                Box(
                    Modifier
                        .size(width = 180.dp, height = 110.dp)
                        .clip(MaterialTheme.shapes.medium),
                ) {
                    Image(
                        painterResource(R.drawable.sample_landscape),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))),
                    )
                    Text(
                        "Карпати",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                    )
                }
            }
            // 3) FAB поверх контенту
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Text("Вміст екрана", Modifier.align(Alignment.Center))
                SmallFloatingActionButton(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                ) { Icon(Icons.Filled.Add, "Додати") }
            }
            Tip("Білий текст на зображенні читабельний завдяки затемненому градієнту під ним (контраст, Лекція 13).")
        }

        DemoSection(
            "Каркас екрана: Column → Row",
            note = "Комбінація Column з вкладеними Row — типовий каркас більшості екранів.",
        ) {
            Column(
                Modifier
                    .outlined()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Олена Шевчук", style = MaterialTheme.typography.titleSmall)
                        Text("2 хв тому", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("•••")
                }
                Text("Сьогодні нарешті розібралась, чим Arrangement відрізняється від Alignment!")
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("❤ 12")
                    Text("💬 3")
                    Text("↗ Поділитися")
                }
            }
            CodeSnippet(
                """
                Column {                 // уся картка
                    Row { Avatar(); Column(Modifier.weight(1f)) { Name(); Time() }; Menu() }
                    Text(body)
                    Row { Likes(); Comments(); Share() }
                }
                """,
            )
        }
    }
}

/** Тонка межа контейнера, щоб було видно його розмір. */
@Composable
internal fun Modifier.outlined(): Modifier = this
    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
    .padding(8.dp)
