package ua.ofkkt.composeshowcase.demos.l11

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LocalSnackbarHostState
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.theme.ShowcaseTheme
import ua.ofkkt.composeshowcase.ui.theme.extendedColors

/*
 * Лекція 11, слайд «Практика: картка товару проєкту» — результат для Практичної роботи №4.
 * Крок 1: Row (зображення + текст) і Column (назва, опис, ціна)
 * Крок 2: verticalAlignment = CenterVertically, Modifier.weight(1f) для текстового блоку
 * Крок 3: clip → background → clickable → padding (ripple не виходить за межі)
 * Крок 4: LazyColumn з key = { it.id }
 */

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val priceUah: Int,
    val emoji: String,
    val inStock: Boolean = true,
)

private val Products: ImmutableList<Product> = persistentListOf(
    Product(1, "Навушники Air Lite", "Бездротові, шумозаглушення, 30 год роботи від одного заряду", 2499, "🎧"),
    Product(2, "Смарт-годинник Pulse 3", "Пульсометр, GPS, захист від води 5 ATM", 4299, "⌚"),
    Product(3, "Павербанк 20 000 мА·год", "Швидка зарядка 65 Вт, два USB-C порти", 1599, "🔋", inStock = false),
    Product(4, "Механічна клавіатура K87", "Гаряча заміна перемикачів, RGB-підсвітка, компактний формат TKL", 3199, "⌨️"),
    Product(5, "Вебкамера HD Pro", "1080p, 60 fps, автофокус і два мікрофони", 1899, "📷"),
    Product(6, "Портативна колонка Wave", "IP67, 12 год музики, стереопара", 2199, "🔊"),
)

@Composable
fun ProductCardDemo() {
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    // Піднятий стан: картка лише показує isFavorite і повідомляє про натискання.
    var favorites by remember { mutableStateOf(setOf<Int>()) }

    DemoColumn {
        DemoSection(
            "Список товарів",
            note = "Натисніть картку — «перехід» до товару (Snackbar). Серце — додати в обране.",
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(440.dp),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(Products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = product.id in favorites,
                            onItemClick = { id -> scope.launch { snackbar.showSnackbar("Відкрити товар #$id") } },
                            onFavoriteClick = { id ->
                                favorites = if (id in favorites) favorites - id else favorites + id
                            },
                        )
                    }
                }
            }
            Text("В обраному: ${favorites.size}", style = MaterialTheme.typography.labelLarge)
        }

        DemoSection("Ключовий ланцюжок Modifier") {
            CodeSnippet(
                """
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)          // 1. форма
                        .background(colorScheme.surfaceContainer)    // 2. фон у межах форми
                        .clickable { onItemClick(product.id) }       // 3. ripple теж у межах форми
                        .padding(12.dp),                             // 4. внутрішній відступ
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Thumbnail()
                    Column(Modifier.weight(1f)) { Title(); Description(); Price() }
                    FavoriteButton()
                }
                """,
            )
            Tip("Домашнє завдання 3 (бонус): поміняйте місцями padding і background — і зробіть скріншот різниці.")
        }
    }
}

/** Stateless-картка товару: лише відображає дані й повідомляє про події. */
@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onItemClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(c.surfaceContainer)
            .clickable { onItemClick(product.id) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(72.dp)
                .clip(MaterialTheme.shapes.small)
                .background(c.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(product.emoji, style = MaterialTheme.typography.headlineMedium)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                product.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                product.description,
                style = MaterialTheme.typography.bodySmall,
                color = c.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${product.priceUah} ₴", style = MaterialTheme.typography.labelLarge, color = c.primary)
                if (!product.inStock) {
                    Text(
                        "немає в наявності",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.extendedColors.onWarningContainer,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(MaterialTheme.extendedColors.warningContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
        }
        IconButton(onClick = { onFavoriteClick(product.id) }) {
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Прибрати з обраного" else "Додати в обране",
                tint = if (isFavorite) c.tertiary else c.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    ShowcaseTheme {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProductCard(Products[0], isFavorite = false, onItemClick = {}, onFavoriteClick = {})
            ProductCard(Products[2], isFavorite = true, onItemClick = {}, onFavoriteClick = {})
        }
    }
}
