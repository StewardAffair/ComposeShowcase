@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package ua.ofkkt.composeshowcase.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.theme.LocalThemeSettings
import ua.ofkkt.composeshowcase.ui.theme.ThemeMode
import ua.ofkkt.composeshowcase.ui.theme.isDarkTheme

/** Головний екран: перелік демо, згрупованих за лекціями, з пошуком. */
@Composable
fun HomeScreen(onOpenDemo: (String) -> Unit) {
    val settings = LocalThemeSettings.current
    val isDark = settings.isDarkTheme()
    var query by rememberSaveable { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val visibleGroups = DemoCatalog.groups.mapNotNull { group ->
        val demos = group.demos.filter {
            query.isBlank() ||
                it.title.contains(query, ignoreCase = true) ||
                it.source.contains(query, ignoreCase = true) ||
                it.explain.contains(query, ignoreCase = true)
        }
        if (demos.isEmpty()) null else DemoGroup(group.id, group.title, group.subtitle, demos)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Compose Showcase")
                        Text(
                            "Мобільна розробка: Android + KMP",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { settings.mode = if (isDark) ThemeMode.Light else ThemeMode.Dark }) {
                        Icon(
                            if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = if (isDark) "Увімкнути світлу тему" else "Увімкнути темну тему",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "intro") {
                IntroCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            item(key = "search") {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Пошук: remember, Modifier, тема…") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                )
            }
            visibleGroups.forEach { group ->
                stickyHeader(key = "header_${group.id}") {
                    GroupHeader(group)
                }
                items(group.demos, key = { it.route }) { demo ->
                    DemoListItem(demo, onClick = { onOpenDemo(demo.route) })
                }
            }
        }
    }
}

@Composable
private fun IntroCard(modifier: Modifier = Modifier) {
    ElevatedCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Демонстрація Jetpack Compose", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Кожен екран відповідає слайду лекції. Угорі екрана — картка з поясненням (кнопка ⓘ ховає її) " +
                    "і шляхом до файлу з кодом. Групи «Базові елементи» і «Додаткові теми» доповнюють презентації.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GroupHeader(group: DemoGroup) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            group.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            group.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DemoListItem(demo: Demo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(demo.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    demo.source,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
