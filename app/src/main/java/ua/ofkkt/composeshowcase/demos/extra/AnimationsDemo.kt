package ua.ofkkt.composeshowcase.demos.extra

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

/* Доповнення: анімації в Compose. Анімація — це теж функція від стану: змінюється стан → анімується значення. */
@Composable
fun AnimationsDemo() {
    DemoColumn {
        VisibilitySection()
        ContentSizeSection()
        AsStateSection()
        CrossfadeSection()
        AnimatedContentSection()
        TransitionSection()
        InfiniteSection()
    }
}

@Composable
private fun VisibilitySection() {
    var visible by rememberSaveable { mutableStateOf(true) }
    DemoSection("1. AnimatedVisibility", note = "Поява/зникнення з комбінацією ефектів enter / exit.") {
        Button(onClick = { visible = !visible }) { Text(if (visible) "Сховати" else "Показати") }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(
                "Привіт! Я з'являюсь і зникаю плавно.",
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        CodeSnippet("AnimatedVisibility(visible, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) { … }")
    }
}

@Composable
private fun ContentSizeSection() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    DemoSection("2. animateContentSize", note = "Один модифікатор — і зміна розміру анімується автоматично.") {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable { expanded = !expanded }
                .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Що таке recomposition?", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }
            if (expanded) {
                Text(
                    "Повторний виклик composable-функцій, які читали змінений стан. Compose оновлює лише ці частини дерева, " +
                        "а решту пропускає. Натисніть ще раз, щоб згорнути.",
                    Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun AsStateSection() {
    var big by rememberSaveable { mutableStateOf(false) }
    val size by animateDpAsState(if (big) 120.dp else 56.dp, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "size")
    val color by animateColorAsState(
        if (big) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        animationSpec = tween(500),
        label = "color",
    )
    DemoSection("3. animate*AsState", note = "Найпростіша анімація: замість значення — animateDpAsState / animateColorAsState / animateFloatAsState.") {
        Box(Modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { big = !big },
            )
        }
        Text("Натисніть на коло", style = MaterialTheme.typography.bodySmall)
        CodeSnippet("val size by animateDpAsState(if (big) 120.dp else 56.dp, label = \"size\")")
    }
}

private enum class Tab(val label: String) { Photos("Фото"), Music("Музика"), Notes("Нотатки") }

@Composable
private fun CrossfadeSection() {
    var tab by rememberSaveable { mutableStateOf(Tab.Photos) }
    DemoSection("4. Crossfade", note = "Плавна зміна вмісту між станами.") {
        ChoiceChips(Tab.entries, tab, { tab = it }, { it.label })
        Crossfade(targetState = tab, animationSpec = tween(400), label = "tab") { current ->
            val (emoji, text) = when (current) {
                Tab.Photos -> "📷" to "124 фото"
                Tab.Music -> "🎵" to "36 треків"
                Tab.Notes -> "📝" to "8 нотаток"
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(emoji, style = MaterialTheme.typography.headlineMedium)
                Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
private fun AnimatedContentSection() {
    var count by rememberSaveable { mutableIntStateOf(0) }
    DemoSection("5. AnimatedContent", note = "Напрямок анімації залежить від того, збільшилось чи зменшилось значення.") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilledTonalButton(onClick = { count-- }) { Text("−") }
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    val up = targetState > initialState
                    val enter = slideInVertically { height -> if (up) height else -height } + fadeIn()
                    val exit = slideOutVertically { height -> if (up) -height else height } + fadeOut()
                    (enter togetherWith exit).using(SizeTransform(clip = false))
                },
                label = "counter",
            ) { value ->
                Text("$value", style = MaterialTheme.typography.displayMedium)
            }
            FilledTonalButton(onClick = { count++ }) { Text("+") }
        }
    }
}

@Composable
private fun TransitionSection() {
    var selected by rememberSaveable { mutableStateOf(false) }
    val transition = updateTransition(targetState = selected, label = "card")
    val corner by transition.animateDp(label = "corner") { if (it) 32.dp else 8.dp }
    val padding by transition.animateDp(label = "padding") { if (it) 28.dp else 12.dp }
    val bg by transition.animateColor(label = "bg") {
        if (it) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    }
    DemoSection("6. updateTransition", note = "Кілька властивостей анімуються синхронно від одного стану.") {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(corner))
                .background(bg)
                .clickable { selected = !selected }
                .padding(padding),
        ) {
            Text(if (selected) "Обрано ✓ (натисніть ще раз)" else "Натисніть, щоб обрати", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun InfiniteSection() {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale",
    )
    DemoSection("7. rememberInfiniteTransition", note = "Нескінченна анімація — індикатори запису, «пульс» сповіщень, скелетони завантаження.") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                Modifier
                    .size(24.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
            )
            Text("Йде запис…", style = MaterialTheme.typography.titleMedium)
        }
        Tip("Анімації поважають системне налаштування «Вимкнути анімації» — перевірте це для доступності.")
    }
}
