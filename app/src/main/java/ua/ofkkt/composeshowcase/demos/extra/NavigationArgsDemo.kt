@file:OptIn(ExperimentalMaterial3Api::class)

package ua.ofkkt.composeshowcase.demos.extra

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ua.ofkkt.composeshowcase.navigation.DemoEnv
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LabeledSwitch
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.theme.extendedColors

/*
 * Доповнення: Navigation Compose. Увесь застосунок — один NavHost (navigation/AppNavHost.kt):
 * кожен екран-демо — окремий маршрут. Тут — аргументи маршруту і повернення результату.
 */

internal val Courses = listOf(
    "Kotlin: основи", "Jetpack Compose", "Архітектура MVVM", "Room і DataStore", "Ktor-клієнт", "Kotlin Multiplatform",
)

@Composable
fun NavigationArgsDemo(env: DemoEnv) {
    var highlight by rememberSaveable { mutableStateOf(false) }
    // Результат, який поверне екран деталей (через savedStateHandle цього запису back stack).
    val result by env.entry.savedStateHandle
        .getStateFlow("result", "—")
        .collectAsStateWithLifecycle()

    DemoColumn {
        DemoSection(
            "1. Перехід з аргументами",
            note = "Маршрут: nav_detail/{itemId}?highlight={highlight}. itemId — обов'язковий Int, highlight — опційний Boolean.",
        ) {
            LabeledSwitch("Передати highlight = true", checked = highlight, onCheckedChange = { highlight = it })
            Courses.forEachIndexed { index, title ->
                ListItem(
                    headlineContent = { Text(title) },
                    supportingContent = { Text("nav_detail/$index?highlight=$highlight") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { env.nav.navigate("nav_detail/$index?highlight=$highlight") },
                )
            }
            CodeSnippet(
                """
                composable(
                    route = "nav_detail/{itemId}?highlight={highlight}",
                    arguments = listOf(
                        navArgument("itemId") { type = NavType.IntType },
                        navArgument("highlight") { type = NavType.BoolType; defaultValue = false },
                    ),
                ) { entry ->
                    val id = entry.arguments?.getInt("itemId") ?: 0
                    DetailScreen(id)
                }
                navController.navigate("nav_detail/3?highlight=true")
                """,
            )
        }

        DemoSection(
            "2. Повернення результату",
            note = "Екран деталей записує значення у savedStateHandle попереднього запису і повертається назад.",
        ) {
            Text("Отриманий результат: $result", style = MaterialTheme.typography.titleMedium)
            CodeSnippet(
                """
                // на екрані деталей
                navController.previousBackStackEntry?.savedStateHandle?.set("result", "Записався")
                navController.popBackStack()

                // на цьому екрані
                val result by entry.savedStateHandle.getStateFlow("result", "—").collectAsStateWithLifecycle()
                """,
            )
        }

        DemoSection("3. Корисні опції navigate") {
            CodeSnippet(
                """
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }  // очистити стек до home
                    launchSingleTop = true                  // не створювати дублікат
                    restoreState = true                     // відновити стан вкладки
                }
                """,
            )
            Tip("З Navigation 2.8 можна описувати маршрути типобезпечно через @Serializable-класи замість рядків.", kind = TipKind.Info)
        }
    }
}

/** Екран деталей — окремий маршрут у NavHost (див. ROUTE_NAV_DETAIL). */
@Composable
fun NavDetailScreen(
    itemId: Int,
    highlight: Boolean,
    onBack: () -> Unit,
    onBackWithResult: (String) -> Unit,
) {
    val title = Courses.getOrElse(itemId) { "Курс #$itemId" }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Деталі курсу") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val ext = MaterialTheme.extendedColors
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = if (highlight) {
                    CardDefaults.cardColors(containerColor = ext.warningContainer, contentColor = ext.onWarningContainer)
                } else {
                    CardDefaults.cardColors()
                },
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(title, style = MaterialTheme.typography.headlineSmall)
                    Text("itemId = $itemId", style = MaterialTheme.typography.bodyMedium)
                    Text("highlight = $highlight", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Button(onClick = { onBackWithResult("Записався на «$title»") }, modifier = Modifier.fillMaxWidth()) {
                Text("Записатися і повернутися")
            }
            OutlinedButton(onClick = { onBackWithResult("Відмовився від «$title»") }, modifier = Modifier.fillMaxWidth()) {
                Text("Відмовитися і повернутися")
            }
        }
    }
}
