@file:OptIn(ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.ChoiceChips
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip

private enum class BarKind(val label: String) { Small("TopAppBar"), Center("CenterAligned"), Medium("Medium"), Large("Large") }

private data class BottomTab(val label: String, val selected: ImageVector, val unselected: ImageVector)

private val bottomTabs = listOf(
    BottomTab("Стрічка", Icons.Filled.Home, Icons.Outlined.Home),
    BottomTab("Вкладки", Icons.Filled.Star, Icons.Outlined.Star),
    BottomTab("Профіль", Icons.Filled.Person, Icons.Outlined.Person),
)

/** Інсети вже враховано зовнішнім Scaffold, тому всередині «рамки» вони нульові. */
private val NoInsets = WindowInsets(0, 0, 0, 0)

@Composable
fun ScaffoldDemo() {
    var barKind by rememberSaveable { mutableStateOf(BarKind.Large) }
    DemoColumn {
        DemoSection(
            "Scaffold — каркас екрана",
            note = "Scaffold розставляє слоти: topBar, bottomBar, floatingActionButton, snackbarHost і передає " +
                "в content відступи (PaddingValues), щоб вміст не залазив під панелі.",
        ) {
            ChoiceChips(BarKind.entries, barKind, { barKind = it }, { it.label })
            PhoneFrame(barKind)
            Tip("Прокрутіть «Стрічку»: Medium/Large-панель згортається завдяки scrollBehavior + Modifier.nestedScroll. Кнопка ☰ відкриває Navigation Drawer.")
            CodeSnippet(
                """
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = { LargeTopAppBar(title = { Text("Назва") }, scrollBehavior = scrollBehavior) },
                    bottomBar = { NavigationBar { /* NavigationBarItem… */ } },
                    floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(…) } },
                ) { innerPadding ->
                    LazyColumn(contentPadding = innerPadding) { … }
                }
                """,
            )
        }
    }
}

@Composable
private fun PhoneFrame(barKind: BarKind) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var fabClicks by rememberSaveable { mutableIntStateOf(0) }
    var drawerItem by rememberSaveable { mutableStateOf("Головна") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val pinned = TopAppBarDefaults.pinnedScrollBehavior()
    val collapsing = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollBehavior = if (barKind == BarKind.Medium || barKind == BarKind.Large) collapsing else pinned

    Box(
        Modifier
            .fillMaxWidth()
            .height(560.dp)
            .clip(MaterialTheme.shapes.large)
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(windowInsets = NoInsets) {
                    Text("Меню", Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()
                    listOf("Головна", "Обране", "Налаштування").forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item) },
                            selected = item == drawerItem,
                            onClick = {
                                drawerItem = item
                                scope.launch { drawerState.close() }
                            },
                            icon = {
                                Icon(
                                    when (item) {
                                        "Головна" -> Icons.Filled.Home
                                        "Обране" -> Icons.Filled.Star
                                        else -> Icons.Filled.Settings
                                    },
                                    contentDescription = null,
                                )
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            },
        ) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentWindowInsets = NoInsets,
                topBar = {
                    val title: @Composable () -> Unit = { Text("$drawerItem · ${bottomTabs[selectedTab].label}") }
                    val navIcon: @Composable () -> Unit = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Відкрити меню")
                        }
                    }
                    val actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {
                        IconButton(onClick = {}) { Icon(Icons.Filled.Search, contentDescription = "Пошук") }
                    }
                    when (barKind) {
                        BarKind.Small -> TopAppBar(title, navigationIcon = navIcon, actions = actions, windowInsets = NoInsets, scrollBehavior = scrollBehavior)
                        BarKind.Center -> CenterAlignedTopAppBar(title, navigationIcon = navIcon, actions = actions, windowInsets = NoInsets, scrollBehavior = scrollBehavior)
                        BarKind.Medium -> MediumTopAppBar(title, navigationIcon = navIcon, actions = actions, windowInsets = NoInsets, scrollBehavior = scrollBehavior)
                        BarKind.Large -> LargeTopAppBar(title, navigationIcon = navIcon, actions = actions, windowInsets = NoInsets, scrollBehavior = scrollBehavior)
                    }
                },
                bottomBar = {
                    NavigationBar(windowInsets = NoInsets) {
                        bottomTabs.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = index == selectedTab,
                                onClick = { selectedTab = index },
                                icon = { Icon(if (index == selectedTab) tab.selected else tab.unselected, contentDescription = null) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { fabClicks++ }) { Icon(Icons.Filled.Add, contentDescription = "Додати") }
                },
            ) { innerPadding ->
                when (selectedTab) {
                    0 -> LazyColumn(Modifier.fillMaxSize(), contentPadding = innerPadding) {
                        items((1..30).toList()) { n ->
                            ListItem(
                                headlineContent = { Text("Допис №$n") },
                                supportingContent = { Text("Прокрутіть, щоб згорнути верхню панель") },
                            )
                        }
                    }
                    1 -> TabsContent(Modifier.padding(innerPadding))
                    else -> Column(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Профіль", style = MaterialTheme.typography.headlineSmall)
                        Text("FAB натиснуто: $fabClicks", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabsContent(modifier: Modifier = Modifier) {
    val titles = listOf("Нові", "Популярні", "Збережені")
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Column(modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { index, title ->
                Tab(selected = index == tab, onClick = { tab = index }, text = { Text(title) })
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Вміст вкладки «${titles[tab]}»", style = MaterialTheme.typography.titleMedium)
        }
    }
}
