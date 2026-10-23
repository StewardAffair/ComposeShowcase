@file:OptIn(ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ua.ofkkt.composeshowcase.demos.extra.NavDetailScreen
import ua.ofkkt.composeshowcase.ui.common.LocalSnackbarHostState

const val ROUTE_HOME = "home"
const val ROUTE_NAV_DETAIL = "nav_detail/{itemId}?highlight={highlight}"

/** Повертаємось назад лише якщо є куди: захист від подвійного натискання «Назад». */
private fun NavHostController.safePopBack() {
    if (previousBackStackEntry != null) popBackStack()
}

/**
 * Кореневий граф навігації. Кожне демо з DemoCatalog — окремий маршрут.
 * Маршрут ROUTE_NAV_DETAIL демонструє аргументи (екран «Navigation Compose»).
 */
@Composable
fun ShowcaseApp() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(onOpenDemo = { route -> nav.navigate(route) { launchSingleTop = true } })
        }
        DemoCatalog.allDemos.forEach { demo ->
            composable(demo.route) { entry ->
                DemoScreen(demo = demo, env = DemoEnv(nav, entry), onBack = { nav.safePopBack() })
            }
        }
        composable(
            route = ROUTE_NAV_DETAIL,
            arguments = listOf(
                navArgument("itemId") { type = NavType.IntType },
                navArgument("highlight") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { entry ->
            val itemId = entry.arguments?.getInt("itemId") ?: 0
            val highlight = entry.arguments?.getBoolean("highlight") ?: false
            NavDetailScreen(
                itemId = itemId,
                highlight = highlight,
                onBack = { nav.safePopBack() },
                onBackWithResult = { result ->
                    nav.previousBackStackEntry?.savedStateHandle?.set("result", result)
                    nav.safePopBack()
                },
            )
        }
    }
}

/** Спільний каркас екрана-демо: заголовок, джерело (лекція/слайд), пояснення, Snackbar. */
@Composable
fun DemoScreen(demo: Demo, env: DemoEnv, onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showInfo by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(demo.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            demo.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(Icons.Outlined.Info, contentDescription = "Показати або сховати пояснення")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                AnimatedVisibility(visible = showInfo) {
                    ExplanationCard(demo)
                }
                Box(Modifier.weight(1f)) {
                    demo.content(env)
                }
            }
        }
    }
}

@Composable
private fun ExplanationCard(demo: Demo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(demo.source, style = MaterialTheme.typography.labelLarge)
            Text(demo.explain, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Код: app/src/main/java/ua/ofkkt/composeshowcase/${demo.file}",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}
