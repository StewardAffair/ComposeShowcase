package ua.ofkkt.composeshowcase.demos.l10

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.RecompositionBadge
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import ua.ofkkt.composeshowcase.ui.common.rememberRecompositionCount

/*
 * Лекція 10, слайди «Стабільність типів (Stability)» і «Типові пастки продуктивності».
 *
 * ВАЖЛИВО (оновлення до слайда): з Kotlin 2.0.20 режим Strong Skipping увімкнений
 * за замовчуванням. Тепер composable з НЕСТАБІЛЬНИМИ параметрами теж може пропускатися,
 * але нестабільні параметри порівнюються за посиланням (===), а стабільні — через equals().
 * Лямбди автоматично «запам'ятовуються», тож пастка «нестабільні лямбди» майже зникла.
 */

/** Стабільний клас: усі поля val стабільних типів. */
private data class StableUser(val name: String, val age: Int)

/** Нестабільний клас: var-поле — Compose не може гарантувати, що об'єкт не змінився «тихо». */
private class MutableUser(var name: String)

/** Анотація @Immutable — обіцянка компілятору, що об'єкт ніколи не зміниться. */
@Immutable
private data class Tags(val items: List<String>)

@Composable
fun StabilityDemo() {
    var tick by remember { mutableIntStateOf(0) }
    val rememberedList = remember { listOf("Аня", "Богдан", "Віра") }

    DemoColumn {
        DemoSection(
            "1. Колекції: List vs ImmutableList",
            note = "Кнопка перекомпоновує батька. Кожна дитина отримує список з однаковим вмістом.",
        ) {
            Text("Батька перекомпоновано: tick = $tick", style = MaterialTheme.typography.titleSmall)
            Button(onClick = { tick++ }) { Text("Перекомпонувати батька") }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Новий екземпляр List щоразу → порівняння за === → завжди «змінився».
                NamesUnstable(listOf("Аня", "Богдан", "Віра"))
                // Новий екземпляр, але тип стабільний → порівняння через equals() → пропуск.
                NamesImmutable(persistentListOf("Аня", "Богдан", "Віра"))
                // Той самий екземпляр (remember) → навіть нестабільний тип пропускається.
                NamesUnstable(rememberedList, label = "List<String> з remember { }")
            }
            Tip(
                "Перший лічильник росте з кожним натисканням, два інші залишаються на 1. " +
                    "Висновок: передавайте ImmutableList або тримайте список у remember / ViewModel.",
                kind = TipKind.Success,
            )
        }

        DemoSection(
            "2. Власні класи",
            note = "data class з val-полями — стабільний; клас з var-полем — нестабільний.",
        ) {
            // Цей блок теж читає tick, тому перекомпоновується разом із кнопкою.
            Text("tick = $tick", style = MaterialTheme.typography.titleSmall)
            Button(onClick = { tick++ }) { Text("Перекомпонувати") }
            UserStable(StableUser("Олена", 19))
            UserMutable(MutableUser("Олена"))
            TagsView(Tags(listOf("kotlin", "compose")))
            Tip("Натисніть кнопку: StableUser і Tags пропускаються, MutableUser — ні (новий екземпляр, порівняння ===).")
        }

        DemoSection("3. Як перевірити стабільність у своєму проєкті") {
            CodeSnippet(
                """
                // app/build.gradle.kts  (Kotlin 2.0+)
                composeCompiler {
                    reportsDestination = layout.buildDirectory.dir("compose_compiler")
                    metricsDestination = layout.buildDirectory.dir("compose_compiler")
                }
                // ./gradlew :app:assembleRelease -PcomposeReports=true
                // див. app/build/compose_compiler/app_release-classes.txt
                """,
            )
            Tip(
                "У цьому проєкті блок уже є в app/build.gradle.kts. Синтаксис зі слайда (kotlinOptions.freeCompilerArgs) " +
                    "застарів для Kotlin 2.0+.",
                kind = TipKind.Warning,
            )
        }
    }
}

@Composable
private fun NamesUnstable(names: List<String>, label: String = "List<String> (новий щоразу)") {
    val n = rememberRecompositionCount()
    RecompositionBadge("$label: ${names.size}", n)
}

@Composable
private fun NamesImmutable(names: ImmutableList<String>) {
    val n = rememberRecompositionCount()
    RecompositionBadge("ImmutableList<String>: ${names.size}", n)
}

@Composable
private fun UserStable(user: StableUser) {
    val n = rememberRecompositionCount()
    RecompositionBadge("StableUser(${user.name}) — data class з val", n)
}

@Composable
private fun UserMutable(user: MutableUser) {
    val n = rememberRecompositionCount()
    RecompositionBadge("MutableUser(${user.name}) — клас з var", n)
}

@Composable
private fun TagsView(tags: Tags) {
    val n = rememberRecompositionCount()
    RecompositionBadge("@Immutable Tags(${tags.items.size})", n)
}
