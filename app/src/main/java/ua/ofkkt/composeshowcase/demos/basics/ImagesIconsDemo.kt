@file:OptIn(ExperimentalLayoutApi::class)

package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.R
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

private val scales: List<Pair<String, ContentScale>> = listOf(
    "Crop" to ContentScale.Crop,
    "Fit" to ContentScale.Fit,
    "FillBounds" to ContentScale.FillBounds,
    "FillWidth" to ContentScale.FillWidth,
    "FillHeight" to ContentScale.FillHeight,
    "Inside" to ContentScale.Inside,
    "None" to ContentScale.None,
)

@Composable
fun ImagesIconsDemo() {
    DemoColumn {
        DemoSection(
            "1. Icon і набори Material Icons",
            note = "Icon за замовчуванням фарбується в LocalContentColor. Колір можна змінити параметром tint.",
        ) {
            val variants: List<Pair<String, ImageVector>> = listOf(
                "Filled" to Icons.Filled.Home,
                "Outlined" to Icons.Outlined.Home,
                "Rounded" to Icons.Rounded.Home,
                "Sharp" to Icons.Sharp.Home,
                "TwoTone" to Icons.TwoTone.Home,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                variants.forEach { (name, icon) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, contentDescription = name, modifier = Modifier.size(32.dp))
                        Text(name, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(Icons.Filled.Home, null, tint = MaterialTheme.colorScheme.primary)
                Icon(Icons.Filled.Home, null, tint = MaterialTheme.colorScheme.tertiary)
                Icon(Icons.Filled.Home, null, tint = MaterialTheme.colorScheme.error)
            }
            Tip("Повний набір іконок — залежність material-icons-extended. У продакшні його часто замінюють власними векторними ресурсами, щоб зменшити розмір APK.")
        }

        DemoSection(
            "2. Image і ContentScale",
            note = "Зображення 3:2 у квадратному контейнері 160×160 dp. Оберіть, як воно масштабується.",
        ) {
            var scaleIndex by rememberSaveable { mutableStateOf(0) }
            ChoiceChips(
                options = scales.indices.toList(),
                selected = scaleIndex,
                onSelect = { scaleIndex = it },
                label = { scales[it].first },
            )
            Image(
                painter = painterResource(R.drawable.sample_landscape),
                contentDescription = "Пейзаж: гори і сонце",
                contentScale = scales[scaleIndex].second,
                modifier = Modifier
                    .size(160.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outline),
            )
            CodeSnippet(
                """
                Image(
                    painter = painterResource(R.drawable.sample_landscape),
                    contentDescription = "Пейзаж",
                    contentScale = ContentScale.${scales[scaleIndex].first},
                    modifier = Modifier.size(160.dp),
                )
                """,
            )
        }

        DemoSection("3. Обрізання, фільтри, прозорість") {
            var alpha by rememberSaveable { mutableFloatStateOf(1f) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painterResource(R.drawable.sample_landscape),
                    contentDescription = "Аватар",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                )
                Image(
                    painterResource(R.drawable.sample_landscape),
                    contentDescription = "Чорно-біле зображення",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
                    modifier = Modifier
                        .size(72.dp)
                        .clip(MaterialTheme.shapes.medium),
                )
                Image(
                    painterResource(R.drawable.sample_landscape),
                    contentDescription = "Напівпрозоре зображення",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .alpha(alpha),
                )
            }
            Text("alpha = ${"%.2f".format(alpha)}")
            Slider(value = alpha, onValueChange = { alpha = it })
            Tip("Зображення з інтернету: бібліотека Coil — AsyncImage(model = url, contentDescription = …). Потрібен дозвіл INTERNET у маніфесті.")
        }
    }
}
