package ua.ofkkt.composeshowcase.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import ua.ofkkt.composeshowcase.demos.basics.ButtonsDemo
import ua.ofkkt.composeshowcase.demos.basics.FeedbackDemo
import ua.ofkkt.composeshowcase.demos.basics.ImagesIconsDemo
import ua.ofkkt.composeshowcase.demos.basics.InputsDemo
import ua.ofkkt.composeshowcase.demos.basics.ScaffoldDemo
import ua.ofkkt.composeshowcase.demos.basics.SelectionControlsDemo
import ua.ofkkt.composeshowcase.demos.basics.TextDemo
import ua.ofkkt.composeshowcase.demos.extra.AnimationsDemo
import ua.ofkkt.composeshowcase.demos.extra.GesturesCanvasDemo
import ua.ofkkt.composeshowcase.demos.extra.GridsPagerDemo
import ua.ofkkt.composeshowcase.demos.extra.NavigationArgsDemo
import ua.ofkkt.composeshowcase.demos.extra.ViewModelDemo
import ua.ofkkt.composeshowcase.demos.l10.DerivedStateDemo
import ua.ofkkt.composeshowcase.demos.l10.RecompositionDemo
import ua.ofkkt.composeshowcase.demos.l10.RememberDemo
import ua.ofkkt.composeshowcase.demos.l10.RememberKeysDemo
import ua.ofkkt.composeshowcase.demos.l10.SideEffectsDemo
import ua.ofkkt.composeshowcase.demos.l10.StabilityDemo
import ua.ofkkt.composeshowcase.demos.l10.StateHoistingDemo
import ua.ofkkt.composeshowcase.demos.l11.ArrangementAlignmentDemo
import ua.ofkkt.composeshowcase.demos.l11.CommonModifiersDemo
import ua.ofkkt.composeshowcase.demos.l11.ConstraintLayoutDemo
import ua.ofkkt.composeshowcase.demos.l11.ConstraintsDemo
import ua.ofkkt.composeshowcase.demos.l11.ContainersDemo
import ua.ofkkt.composeshowcase.demos.l11.CustomLayoutDemo
import ua.ofkkt.composeshowcase.demos.l11.LazyListsDemo
import ua.ofkkt.composeshowcase.demos.l11.ModifierOrderDemo
import ua.ofkkt.composeshowcase.demos.l11.ProductCardDemo
import ua.ofkkt.composeshowcase.demos.l13.AccessibilityDemo
import ua.ofkkt.composeshowcase.demos.l13.ColorSchemeDemo
import ua.ofkkt.composeshowcase.demos.l13.ComponentTuningDemo
import ua.ofkkt.composeshowcase.demos.l13.CompositionLocalDemo
import ua.ofkkt.composeshowcase.demos.l13.ElevationDemo
import ua.ofkkt.composeshowcase.demos.l13.ExtendedTokensDemo
import ua.ofkkt.composeshowcase.demos.l13.ShapesDemo
import ua.ofkkt.composeshowcase.demos.l13.ThemeSwitcherDemo
import ua.ofkkt.composeshowcase.demos.l13.TypographyDemo

/** Те, що екран-демо може знадобитися від навігації (використовує лише екран навігації). */
class DemoEnv(val nav: NavHostController, val entry: NavBackStackEntry)

/**
 * Один екран-демонстрація.
 * @param source  звідки тема: лекція й слайд (або «Доповнення»).
 * @param explain що саме показує екран і на що звернути увагу студентам.
 * @param file    де лежить код екрана.
 */
class Demo(
    val route: String,
    val title: String,
    val source: String,
    val explain: String,
    val file: String,
    val content: @Composable (DemoEnv) -> Unit,
)

class DemoGroup(val id: String, val title: String, val subtitle: String, val demos: List<Demo>)

object DemoCatalog {

    val groups: List<DemoGroup> = listOf(
        DemoGroup(
            id = "basics",
            title = "Базові елементи UI",
            subtitle = "Доповнення: у презентаціях цих тем немає. Рекомендовано показати перед Лекцією 10 або разом із нею.",
            demos = listOf(
                Demo(
                    "basics_text", "Text і стилізація тексту", "Доповнення · до Лекції 10",
                    "Text з параметрами (color, fontSize, fontWeight, maxLines, overflow), стилі з MaterialTheme.typography, " +
                        "AnnotatedString з кількома стилями та посиланням, SelectionContainer і stringResource з аргументом.",
                    "demos/basics/TextDemo.kt",
                ) { TextDemo() },
                Demo(
                    "basics_buttons", "Кнопки та FAB", "Доповнення · до Лекції 10",
                    "Ієрархія акцентів Material3: Button → FilledTonal → Elevated → Outlined → Text. Кнопка з іконкою, " +
                        "IconButton та його варіанти, IconToggleButton, чотири види FAB. Лічильник натискань — найпростіший стан.",
                    "demos/basics/ButtonsDemo.kt",
                ) { ButtonsDemo() },
                Demo(
                    "basics_inputs", "Поля вводу (TextField)", "Доповнення · до Лекції 10",
                    "TextField і OutlinedTextField: label, placeholder, іконки, supportingText, isError, KeyboardOptions / KeyboardActions, " +
                        "пароль із VisualTransformation, фільтр цифр, лічильник символів, FocusRequester. Кнопка «Надіслати» " +
                        "активується через derivedStateOf (місток до Лекції 10).",
                    "demos/basics/InputsDemo.kt",
                ) { InputsDemo() },
                Demo(
                    "basics_selection", "Елементи вибору", "Доповнення · до Лекції 10",
                    "Checkbox, TriStateCheckbox, RadioButton-група з selectableGroup, Switch, Slider, RangeSlider, " +
                        "чотири типи Chip і SegmentedButton. Увесь рядок клікабельний — так правильно для доступності.",
                    "demos/basics/SelectionControlsDemo.kt",
                ) { SelectionControlsDemo() },
                Demo(
                    "basics_images", "Зображення та іконки", "Доповнення · до Лекції 11",
                    "Icon (Filled/Outlined/Rounded/Sharp), Image з painterResource, усі варіанти ContentScale, " +
                        "обрізання clip(CircleShape), colorFilter і alpha. Для зображень із мережі використовують бібліотеку Coil (AsyncImage).",
                    "demos/basics/ImagesIconsDemo.kt",
                ) { ImagesIconsDemo() },
                Demo(
                    "basics_feedback", "Діалоги, Snackbar, прогрес, меню", "Доповнення · до Лекції 10",
                    "AlertDialog, ModalBottomSheet, Snackbar з дією «Скасувати», DropdownMenu, BadgedBox, " +
                        "індикатори прогресу (визначений і невизначений). Відкриття діалогу — це теж просто стан Boolean.",
                    "demos/basics/FeedbackDemo.kt",
                ) { FeedbackDemo() },
                Demo(
                    "basics_scaffold", "Scaffold, app bars, навігаційні панелі", "Доповнення · до Лекції 11",
                    "Каркас екрана: Scaffold з TopAppBar (4 варіанти + scrollBehavior), NavigationBar, FAB, TabRow " +
                        "та ModalNavigationDrawer — усе всередині «рамки телефона».",
                    "demos/basics/ScaffoldDemo.kt",
                ) { ScaffoldDemo() },
            ),
        ),
        DemoGroup(
            id = "l10",
            title = "Лекція 10 · Composable-функції, State",
            subtitle = "Recomposition, remember, State hoisting, Side Effects, стабільність",
            demos = listOf(
                Demo(
                    "l10_recomposition", "Recomposition: хто перемальовується", "Лекція 10 · «Recomposition: що це таке», «Що запускає Recomposition»",
                    "Лічильники 🔁 показують, скільки разів перекомпонувалась кожна частина UI. Composable без змінених " +
                        "параметрів пропускається (skipping). Другий приклад: відкладене читання стану через лямбду { count } — " +
                        "батько взагалі не перекомпоновується.",
                    "demos/l10/RecompositionDemo.kt",
                ) { RecompositionDemo() },
                Demo(
                    "l10_remember", "remember vs rememberSaveable", "Лекція 10 · «remember та mutableStateOf», «remember vs rememberSaveable»",
                    "Три лічильники: без remember (завжди 0), з remember, з rememberSaveable. Поверніть екран — " +
                        "переживе лише rememberSaveable. Перемикач «прибрати з композиції» показує, що обидва втрачають стан, " +
                        "коли composable покидає дерево. Наприкінці — власний Saver (mapSaver).",
                    "demos/l10/RememberDemo.kt",
                ) { RememberDemo() },
                Demo(
                    "l10_hoisting", "State Hoisting і UDF", "Лекція 10 · «State Hoisting: детальніше», «State<T> vs MutableState<T>»",
                    "Stateful-лічильник vs stateless-лічильник. Піднятий стан дозволяє двом компонентам ділити одне значення. " +
                        "Пошук зі списком: стан тече вниз, події йдуть вгору.",
                    "demos/l10/StateHoistingDemo.kt",
                ) { StateHoistingDemo() },
                Demo(
                    "l10_derived", "derivedStateOf", "Лекція 10 · «derivedStateOf»",
                    "Порівняння двох індикаторів валідності: один читає text напряму і перекомпоновується при кожному символі, " +
                        "другий — через derivedStateOf і перекомпоновується лише при зміні true/false. Те саме для кнопки " +
                        "«Нагору» у списку (firstVisibleItemIndex > 0).",
                    "demos/l10/DerivedStateDemo.kt",
                ) { DerivedStateDemo() },
                Demo(
                    "l10_remember_keys", "remember з ключами", "Лекція 10 · «remember з ключами»",
                    "UserForm(userId) зі слайда: з remember(userId) поле скидається при зміні користувача, без ключа — " +
                        "зберігає старий текст. Плюс кешування обчислення: remember(items, ascending) { sorted() }.",
                    "demos/l10/RememberKeysDemo.kt",
                ) { RememberKeysDemo() },
                Demo(
                    "l10_effects", "Side Effects", "Лекція 10 · «LaunchedEffect», «DisposableEffect та SideEffect»",
                    "Журнал подій у реальному часі: LaunchedEffect(userId) з автоматичним скасуванням, DisposableEffect з " +
                        "Lifecycle-спостерігачем (згорніть застосунок і поверніться), SideEffect. Доповнення: rememberCoroutineScope " +
                        "для обробників натискань, rememberUpdatedState і snapshotFlow.",
                    "demos/l10/SideEffectsDemo.kt",
                ) { SideEffectsDemo() },
                Demo(
                    "l10_stability", "Стабільність типів", "Лекція 10 · «Стабільність типів», «Типові пастки продуктивності»",
                    "List<T> vs ImmutableList<T> vs remember { list }. Видно, який дочірній composable пропускається, " +
                        "а який — ні. Пояснено режим Strong Skipping (увімкнений за замовчуванням з Kotlin 2.0.20).",
                    "demos/l10/StabilityDemo.kt",
                ) { StabilityDemo() },
            ),
        ),
        DemoGroup(
            id = "l11",
            title = "Лекція 11 · Layout, Modifier",
            subtitle = "Row/Column/Box, Arrangement/Alignment, порядок Modifier, Lazy-списки, Custom Layout",
            demos = listOf(
                Demo(
                    "l11_containers", "Row, Column, Box", "Лекція 11 · «Row, Column, Box: базові контейнери»",
                    "Три контейнери окремо та типові комбінації: бейдж на іконці (Box), текст поверх зображення, " +
                        "FAB поверх контенту, каркас екрана Column → Row.",
                    "demos/l11/ContainersDemo.kt",
                ) { ContainersDemo() },
                Demo(
                    "l11_arrangement", "Arrangement та Alignment", "Лекція 11 · «Arrangement», «Alignment»",
                    "Інтерактивний «пісочник»: оберіть Row/Column, Arrangement і Alignment — побачите результат і згенерований код. " +
                        "Окремо — contentAlignment у Box та Modifier.align() для кожної дитини.",
                    "demos/l11/ArrangementAlignmentDemo.kt",
                ) { ArrangementAlignmentDemo() },
                Demo(
                    "l11_modifier_order", "Порядок Modifier", "Лекція 11 · «Modifier: базовий принцип», «Порядок Modifier має значення»",
                    "Пари «до / після»: padding↔background, size↔background (увага: результат однаковий!), clip↔background, " +
                        "clip↔clickable (ripple), border↔padding, offset vs padding. Конструктор ланцюжка: переставляйте модифікатори стрілками.",
                    "demos/l11/ModifierOrderDemo.kt",
                ) { ModifierOrderDemo() },
                Demo(
                    "l11_common_modifiers", "Поширені модифікатори", "Лекція 11 · «Поширені модифікатори»",
                    "weight() (у т.ч. fill = false), fillMaxWidth(fraction), wrapContentSize, requiredSize, aspectRatio, offset, " +
                        "alpha/rotate/scale, shadow, border, IntrinsicSize.Min.",
                    "demos/l11/CommonModifiersDemo.kt",
                ) { CommonModifiersDemo() },
                Demo(
                    "l11_constraints", "Constraints і адаптивність", "Лекція 11 · «Constraints: як передається розмір»",
                    "BoxWithConstraints показує min/max, які батько передає дитині. Повзунок змінює ширину контейнера — " +
                        "макет перемикається Row ↔ Column. Різниця size / requiredSize / wrapContentSize.",
                    "demos/l11/ConstraintsDemo.kt",
                ) { ConstraintsDemo() },
                Demo(
                    "l11_lazy", "LazyColumn / LazyRow і key", "Лекція 11 · «LazyColumn / LazyRow», «Типові помилки layout» №1, №4",
                    "Перемішайте список з key і без key: у кожного елемента свій стан (чекбокс). Без key стан «прилипає» до позиції. " +
                        "Також stickyHeader, animateItem(), кнопка «Нагору» з rememberLazyListState і LazyRow.",
                    "demos/l11/LazyListsDemo.kt",
                ) { LazyListsDemo() },
                Demo(
                    "l11_custom_layout", "Custom Layout", "Лекція 11 · «Custom Layout: вступ»",
                    "Власні Layout: «сходи» (measure → place) і спрощений FlowRow з переносом рядків. Modifier.layout " +
                        "для відступу від базової лінії тексту.",
                    "demos/l11/CustomLayoutDemo.kt",
                ) { CustomLayoutDemo() },
                Demo(
                    "l11_constraint_layout", "ConstraintLayout", "Лекція 11 · «ConstraintLayout у Compose»",
                    "Картка профілю на ConstraintLayout: createRefs, linkTo, Dimension.fillToConstraints, guideline, barrier. " +
                        "Ланцюжки (chains) зі стилями Spread / SpreadInside / Packed.",
                    "demos/l11/ConstraintLayoutDemo.kt",
                ) { ConstraintLayoutDemo() },
                Demo(
                    "l11_product_card", "Практика: картка товару", "Лекція 11 · «Практика: картка товару проєкту» (Пр4)",
                    "Готовий ProductCard за 4 кроками зі слайда: Row/Column, weight(1f), ланцюжок clip → background → clickable → padding, " +
                        "LazyColumn з key. Клік показує Snackbar, «серце» — піднятий стан.",
                    "demos/l11/ProductCardDemo.kt",
                ) { ProductCardDemo() },
            ),
        ),
        DemoGroup(
            id = "l13",
            title = "Лекція 13 · Теми, Material3",
            subtitle = "CompositionLocal, ColorScheme, токени, Dynamic Color, Typography, Shapes, Accessibility",
            demos = listOf(
                Demo(
                    "l13_composition_local", "CompositionLocal", "Лекція 13 · «CompositionLocal: механізм», «MaterialTheme зсередини»",
                    "Власний Local з вкладеним перевизначенням, LocalContentColor і ProvideTextStyle, вкладений MaterialTheme для " +
                        "піддерева. Порівняння compositionLocalOf і staticCompositionLocalOf за лічильниками рекомпозицій.",
                    "demos/l13/CompositionLocalDemo.kt",
                ) { CompositionLocalDemo() },
                Demo(
                    "l13_color_scheme", "ColorScheme: усі ролі", "Лекція 13 · «Від базової теми до системи дизайну»",
                    "Усі кольорові ролі поточної схеми парами color / onColor з HEX-значеннями. Перемкніть тему або Dynamic Color " +
                        "і поверніться — палітра зміниться.",
                    "demos/l13/ColorSchemeDemo.kt",
                ) { ColorSchemeDemo() },
                Demo(
                    "l13_extended", "Власні токени (success, warning, info)", "Лекція 13 · «Розширення теми власними токенами»",
                    "Статусні банери та бейджі на ExtendedColors, окремі значення для світлої й темної теми, 4 кроки з коду.",
                    "demos/l13/ExtendedTokensDemo.kt",
                ) { ExtendedTokensDemo() },
                Demo(
                    "l13_elevation", "Elevation і tonal color", "Лекція 13 · «Elevation та tonal color»",
                    "Surface з tonalElevation 0…12 dp поруч із shadowElevation. Повзунок для власного значення. " +
                        "Сучасна альтернатива — ролі surfaceContainerLowest…Highest.",
                    "demos/l13/ElevationDemo.kt",
                ) { ElevationDemo() },
                Demo(
                    "l13_tuning", "Компонентне тюнінгування", "Лекція 13 · «Компонентне тюнінгування», «Рівні кастомізації теми»",
                    "ButtonDefaults.buttonColors, CardDefaults.cardElevation, TextFieldDefaults.colors, SwitchDefaults.colors. " +
                        "Чотири рівні кастомізації: глобальний, секційний, компонентний, екземпляра.",
                    "demos/l13/ComponentTuningDemo.kt",
                ) { ComponentTuningDemo() },
                Demo(
                    "l13_typography", "Типографіка", "Лекція 13 · «Типографіка: кастомні шрифти»",
                    "Усі 15 стилів Typography з розмірами. Заголовки — «бренд-шрифтом» (Serif). Повзунок fontScale імітує " +
                        "системне налаштування великого шрифту.",
                    "demos/l13/TypographyDemo.kt",
                ) { TypographyDemo() },
                Demo(
                    "l13_shapes", "Shapes", "Лекція 13 · «Shapes: поглиблено»",
                    "П'ять рівнів форм теми, компоненти на них, «пігулка» RoundedCornerShape(50), CutCornerShape, " +
                        "асиметричні chat-бульбашки і власна GenericShape.",
                    "demos/l13/ShapesDemo.kt",
                ) { ShapesDemo() },
                Demo(
                    "l13_theme_switch", "Перемикач теми і Dynamic Color", "Лекція 13 · «Перемикання світлої/темної теми», «Dynamic Color», «Анімований перехід»",
                    "Системна / Світла / Темна, Dynamic Color з перевіркою API 31 і fallback, анімований перехід 300 мс. " +
                        "Налаштування діють на весь застосунок.",
                    "demos/l13/ThemeSwitcherDemo.kt",
                ) { ThemeSwitcherDemo() },
                Demo(
                    "l13_a11y", "Accessibility та контраст", "Лекція 13 · «Accessibility та контраст»",
                    "Живий розрахунок контрасту WCAG для пар кольорів поточної теми (AA ≥ 4.5, AAA ≥ 7). " +
                        "contentDescription, mergeDescendants, heading(), мінімальна зона дотику 48 dp.",
                    "demos/l13/AccessibilityDemo.kt",
                ) { AccessibilityDemo() },
            ),
        ),
        DemoGroup(
            id = "extra",
            title = "Додаткові теми Compose",
            subtitle = "Доповнення: анімації, жести й Canvas, сітки й Pager, Navigation з аргументами, ViewModel",
            demos = listOf(
                Demo(
                    "extra_animations", "Анімації", "Доповнення · після Лекції 13",
                    "AnimatedVisibility, animateContentSize, animate*AsState, Crossfade, AnimatedContent, updateTransition, " +
                        "rememberInfiniteTransition.",
                    "demos/extra/AnimationsDemo.kt",
                ) { AnimationsDemo() },
                Demo(
                    "extra_gestures", "Жести та Canvas", "Доповнення · після Лекції 11",
                    "detectTapGestures (tap / double / long), перетягування з Modifier.offset { } (відкладене читання), " +
                        "transformable (zoom, rotate), малювання пальцем, Canvas з drawArc, drawBehind.",
                    "demos/extra/GesturesCanvasDemo.kt",
                ) { GesturesCanvasDemo() },
                Demo(
                    "extra_grids", "Сітки, Pager, FlowRow", "Доповнення · до Лекції 11",
                    "LazyVerticalGrid з адаптивними колонками і заголовком на всю ширину, LazyVerticalStaggeredGrid, " +
                        "HorizontalPager з індикатором і вкладками, FlowRow.",
                    "demos/extra/GridsPagerDemo.kt",
                ) { GridsPagerDemo() },
                Demo(
                    "extra_navigation", "Navigation Compose: аргументи й результат", "Доповнення · до лекцій про навігацію",
                    "Увесь застосунок побудований на NavHost. Тут: передача аргументів у маршрут (обов'язковий і опційний), " +
                        "повернення результату через savedStateHandle, launchSingleTop.",
                    "demos/extra/NavigationArgsDemo.kt",
                ) { env -> NavigationArgsDemo(env) },
                Demo(
                    "extra_viewmodel", "ViewModel + StateFlow (UDF)", "Доповнення · місток до блоку «Архітектура»",
                    "UiState у StateFlow, collectAsStateWithLifecycle, події як виклики функцій ViewModel. " +
                        "Поверніть екран — стан переживе, бо живе у ViewModel (див. таблицю зі слайда remember vs rememberSaveable).",
                    "demos/extra/ViewModelDemo.kt",
                ) { ViewModelDemo() },
            ),
        ),
    )

    val allDemos: List<Demo> = groups.flatMap { it.demos }
}
