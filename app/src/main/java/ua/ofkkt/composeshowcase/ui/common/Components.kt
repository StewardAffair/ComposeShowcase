@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.ofkkt.composeshowcase.ui.theme.extendedColors

/** Snackbar екрана-демо доступний будь-якому вкладеному composable (ще один приклад CompositionLocal). */
val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState не надано")
}

/** Прокручувана колонка — стандартний «каркас» кожного демо-екрана. */
@Composable
fun DemoColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

/** Картка-розділ з заголовком і коротким поясненням. */
@Composable
fun DemoSection(
    title: String,
    modifier: Modifier = Modifier,
    note: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    OutlinedCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
            if (note != null) {
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}

/** Фрагмент коду моноширинним шрифтом (з горизонтальним скролом для довгих рядків). */
@Composable
fun CodeSnippet(code: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .horizontalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        Text(
            text = code.trimIndent(),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            softWrap = false,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

enum class TipKind { Info, Success, Warning }

/** Підказка в кольорах розширених токенів теми (Лекція 13: extendedColors). */
@Composable
fun Tip(text: String, kind: TipKind = TipKind.Info, modifier: Modifier = Modifier) {
    val ext = MaterialTheme.extendedColors
    val (bg, fg, icon) = when (kind) {
        TipKind.Info -> Triple(ext.infoContainer, ext.onInfoContainer, Icons.Outlined.Info)
        TipKind.Success -> Triple(ext.successContainer, ext.onSuccessContainer, Icons.Outlined.CheckCircle)
        TipKind.Warning -> Triple(ext.warningContainer, ext.onWarningContainer, Icons.Outlined.Warning)
    }
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(20.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = fg)
    }
}

/* ------------------------------------------------------------------
 * Лічильник рекомпозицій (Лекція 10).
 * Composable, що ПОВЕРТАЄ значення, не є окремою restart-групою: він
 * виконується в області (scope) того composable, який його викликав.
 * Тому число показує, скільки разів перекомпонувався саме викликач.
 * ------------------------------------------------------------------ */
class CountHolder { var value = 0 }

@Composable
fun rememberRecompositionCount(): Int {
    val holder = remember { CountHolder() }
    holder.value++
    return holder.value
}

@Composable
fun RecompositionBadge(label: String, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "🔁 $count",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/** Набір FilterChip для вибору одного варіанта. */
@Composable
fun <T> ChoiceChips(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
            )
        }
    }
}

/** Рядок «текст + Switch», уесь рядок клікабельний (доступність: Role.Switch). */
@Composable
fun LabeledSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier
            .fillMaxWidth()
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

/** Кольоровий квадрат з підписом — «цеглинка» для демо компонування. */
@Composable
fun DemoBlock(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    index: Int = 0,
) {
    val scheme = MaterialTheme.colorScheme
    val (bg, fg) = when (index % 3) {
        0 -> scheme.primary to scheme.onPrimary
        1 -> scheme.tertiary to scheme.onTertiary
        else -> scheme.secondary to scheme.onSecondary
    }
    Box(
        modifier
            .size(size)
            .clip(MaterialTheme.shapes.small)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = fg, style = MaterialTheme.typography.labelLarge)
    }
}
