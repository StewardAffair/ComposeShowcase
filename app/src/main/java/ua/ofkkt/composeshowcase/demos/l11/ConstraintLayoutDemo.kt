package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoBlock
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

/*
 * Лекція 11, слайд «ConstraintLayout у Compose».
 * Залежність: androidx.constraintlayout:constraintlayout-compose
 */
@Composable
fun ConstraintLayoutDemo() {
    DemoColumn {
        DemoSection(
            "1. Картка профілю: refs, linkTo, barrier",
            note = "Усі елементи — на одному рівні, без вкладених Row/Column. Позиції задаються відносно інших елементів.",
        ) {
            ProfileCard()
            CodeSnippet(
                """
                ConstraintLayout {
                    val (avatar, name, role, follow, stats) = createRefs()
                    Avatar(Modifier.constrainAs(avatar) {
                        start.linkTo(parent.start, 16.dp); top.linkTo(parent.top, 16.dp)
                    })
                    Text(name, Modifier.constrainAs(name) {
                        start.linkTo(avatar.end, 12.dp); end.linkTo(follow.start, 8.dp)
                        top.linkTo(avatar.top)
                        width = Dimension.fillToConstraints
                    })
                    val barrier = createBottomBarrier(avatar, role)
                    Stats(Modifier.constrainAs(stats) { top.linkTo(barrier, 16.dp) })
                }
                """,
            )
        }

        ChainsSection()

        DemoSection("Коли НЕ варто") {
            Tip(
                "Для простих карток і списків Row/Column читабельніші. ConstraintLayout — коли Row/Column потребували б 4+ рівнів вкладеності " +
                    "або при портуванні складного XML-макета.",
                kind = TipKind.Warning,
            )
        }
    }
}

@Composable
private fun ProfileCard() {
    val c = MaterialTheme.colorScheme
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(c.surfaceContainerHigh),
    ) {
        val (avatar, name, role, follow, stats) = createRefs()

        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(c.primaryContainer)
                .constrainAs(avatar) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                },
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Filled.Person, contentDescription = null, tint = c.onPrimaryContainer) }

        Text(
            "Олександра Коваленко-Ткаченко",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(name) {
                start.linkTo(avatar.end, 12.dp)
                end.linkTo(follow.start, 8.dp)
                top.linkTo(avatar.top)
                width = Dimension.fillToConstraints
            },
        )
        Text(
            "Android-розробниця · Одеса. Пише про Compose, Kotlin Multiplatform і доступність.",
            style = MaterialTheme.typography.bodySmall,
            color = c.onSurfaceVariant,
            modifier = Modifier.constrainAs(role) {
                start.linkTo(name.start)
                end.linkTo(parent.end, 16.dp)
                top.linkTo(name.bottom, 4.dp)
                width = Dimension.fillToConstraints
            },
        )
        FilledTonalButton(
            onClick = {},
            modifier = Modifier.constrainAs(follow) {
                end.linkTo(parent.end, 16.dp)
                top.linkTo(avatar.top)
            },
        ) { Text("Стежити") }

        // Бар'єр — під НИЖЧИМ з двох елементів (аватар або опис), хоч би якої довжини був текст.
        val barrier = createBottomBarrier(avatar, role)
        Text(
            "128 дописів   ·   2,4 тис. читачів   ·   310 стежить",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.constrainAs(stats) {
                top.linkTo(barrier, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, 16.dp)
            },
        )
    }
}

@Composable
private fun ChainsSection() {
    val styles = listOf("Spread", "SpreadInside", "Packed")
    var style by rememberSaveable { mutableStateOf("Spread") }
    val chainStyle = when (style) {
        "SpreadInside" -> ChainStyle.SpreadInside
        "Packed" -> ChainStyle.Packed
        else -> ChainStyle.Spread
    }
    DemoSection(
        "2. Ланцюжки (chains) і guideline",
        note = "Ланцюжок розподіляє елементи як Arrangement. Вертикальна лінія — guideline на 25% ширини.",
    ) {
        ChoiceChips(styles, style, { style = it }, { it })
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            val (a, b, c, line, d) = createRefs()
            val guide = createGuidelineFromStart(0.25f)
            createHorizontalChain(a, b, c, chainStyle = chainStyle)

            DemoBlock("A", index = 0, modifier = Modifier.constrainAs(a) { top.linkTo(parent.top, 12.dp) })
            DemoBlock("B", index = 1, modifier = Modifier.constrainAs(b) { top.linkTo(parent.top, 12.dp) })
            DemoBlock("C", index = 2, modifier = Modifier.constrainAs(c) { top.linkTo(parent.top, 12.dp) })

            VerticalDivider(
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.constrainAs(line) {
                    start.linkTo(guide)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
            )
            DemoBlock(
                "guide",
                index = 0,
                modifier = Modifier
                    .width(72.dp)
                    .constrainAs(d) {
                        start.linkTo(guide, 8.dp)
                        bottom.linkTo(parent.bottom, 12.dp)
                    },
            )
        }
        CodeSnippet("createHorizontalChain(a, b, c, chainStyle = ChainStyle.$style)\nval guide = createGuidelineFromStart(0.25f)")
    }
}
