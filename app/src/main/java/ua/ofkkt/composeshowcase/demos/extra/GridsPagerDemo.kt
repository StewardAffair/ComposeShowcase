@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.demos.extra

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

/* Доповнення: сітки, Pager і FlowRow — продовження теми Lazy-списків Лекції 11. */
@Composable
fun GridsPagerDemo() {
    DemoColumn {
        GridSection()
        StaggeredSection()
        PagerSection()
        FlowRowSection()
    }
}

@Composable
private fun tileColor(i: Int): Pair<Color, Color> {
    val c = MaterialTheme.colorScheme
    return when (i % 3) {
        0 -> c.primaryContainer to c.onPrimaryContainer
        1 -> c.secondaryContainer to c.onSecondaryContainer
        else -> c.tertiaryContainer to c.onTertiaryContainer
    }
}

@Composable
private fun GridSection() {
    DemoSection(
        "1. LazyVerticalGrid",
        note = "GridCells.Adaptive(96.dp) — кількість колонок залежить від ширини екрана. Заголовок займає всю ширину (span).",
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 96.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "header") {
                Text("Галерея · 24 фото", style = MaterialTheme.typography.titleMedium)
            }
            items((1..24).toList(), key = { it }) { n ->
                val (bg, fg) = tileColor(n)
                Box(
                    Modifier
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.small)
                        .background(bg),
                    contentAlignment = Alignment.Center,
                ) { Text("$n", color = fg) }
            }
        }
        CodeSnippet("LazyVerticalGrid(columns = GridCells.Adaptive(96.dp)) { items(photos, key = { it.id }) { … } }")
    }
}

@Composable
private fun StaggeredSection() {
    val heights = listOf(120, 80, 150, 100, 90, 140, 70, 130, 110, 95)
    DemoSection("2. LazyVerticalStaggeredGrid", note = "Плитки різної висоти, як у Pinterest.") {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
        ) {
            items(heights.indices.toList(), key = { it }) { i ->
                val (bg, fg) = tileColor(i)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(heights[i].dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(bg),
                    contentAlignment = Alignment.Center,
                ) { Text("${heights[i]} dp", color = fg) }
            }
        }
    }
}

@Composable
private fun PagerSection() {
    val pages = listOf("Вітаємо!" to "👋", "Налаштуйте тему" to "🎨", "Додайте друзів" to "🤝", "Готово" to "🚀")
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    DemoSection("3. HorizontalPager + TabRow", note = "Типовий онбординг: свайп між сторінками або вибір вкладки.") {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, _ ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text("${index + 1}") },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) { page ->
            val (bg, fg) = tileColor(page)
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(bg),
                contentAlignment = Alignment.Center,
            ) {
                Text("${pages[page].second}  ${pages[page].first}", style = MaterialTheme.typography.titleLarge, color = fg)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { i ->
                Box(
                    Modifier
                        .padding(4.dp)
                        .size(if (pagerState.currentPage == i) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        ),
                )
            }
        }
    }
}

@Composable
private fun FlowRowSection() {
    val skills = listOf("Kotlin", "Coroutines", "Flow", "Compose", "Material3", "Navigation", "Room", "Retrofit", "Hilt", "Koin", "KMP", "Git")
    DemoSection("4. FlowRow", note = "Не Lazy: елементи переносяться на новий рядок, якщо не вміщаються. Для тегів і чіпів.") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            skills.forEach { InputChip(selected = false, onClick = {}, label = { Text(it) }) }
        }
        Tip("Порівняйте з власною реалізацією SimpleFlowRow на екрані «Custom Layout» (Лекція 11).")
    }
}
