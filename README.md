# Compose Showcase — демо-застосунок до блоку Jetpack Compose

Курс «Мобільна розробка: Android + Kotlin Multiplatform».
Застосунок демонструє **кожен аспект** трьох лекцій (Лекція 10 «Composable-функції, State»,
Лекція 11 «Layout, Modifier», Лекція 13 «Теми, Material3 (поглиблено)») і доповнює їх темами,
яких немає в презентаціях: базові елементи UI, анімації, жести, сітки, навігація з аргументами, ViewModel.

> Номери лекцій узято з презентацій. У новій програмі курсу (вересень 2026) цим темам відповідають
> Л7 (2.3 composable/state), Л8 (2.4 layout/Modifier) і Л10 (2.6 Navigation Compose / Material3).
> Група «Базові елементи» добре підходить до Пр3 («composable-компоненти + збірка першого екрана»).

---

## Як запустити

1. Android Studio Ladybug (2024.2) або новіша, JDK 17.
2. *File → Open* → папка `ComposeShowcase` → дочекатися Gradle Sync.
3. Запустити конфігурацію `app` на емуляторі або пристрої (minSdk 24).
   Для Dynamic Color потрібен пристрій/емулятор з Android 12+ (API 31).

Версії (у `gradle/libs.versions.toml`): AGP 8.7.3, Kotlin 2.1.0, Compose BOM 2025.01.00,
Navigation 2.8.5, ConstraintLayout-Compose 1.1.0, kotlinx-collections-immutable 0.3.8.
Android Studio може запропонувати оновити версії — це безпечно.

## Як користуватися

* Головний екран — перелік демо, згрупованих за лекціями, з пошуком.
* Угорі кожного демо — картка: **лекція і слайд**, що саме показує екран, і **шлях до файлу з кодом**.
  Кнопка ⓘ ховає/показує картку.
* Кнопка ☀/☾ на головному екрані перемикає тему для всього застосунку.
* Лічильники 🔁 N показують, скільки разів перекомпонувалась відповідна функція (Лекція 10).

## Структура

```
app/src/main/java/ua/ofkkt/composeshowcase/
├─ MainActivity.kt              setContent, стан теми (rememberSaveable + Saver), edge-to-edge
├─ navigation/
│  ├─ DemoCatalog.kt            ← СПИСОК УСІХ ЕКРАНІВ: лекція, слайд, пояснення, файл
│  ├─ AppNavHost.kt             NavHost, каркас екрана-демо, маршрут з аргументами
│  └─ HomeScreen.kt             головний екран (stickyHeader, пошук)
├─ ui/theme/                    Color, ExtendedColors, Type, Shape, Theme, ThemeSettings
├─ ui/common/Components.kt      DemoSection, CodeSnippet, Tip, лічильник рекомпозицій…
└─ demos/
   ├─ basics/   базові елементи (доповнення)
   ├─ l10/      Лекція 10
   ├─ l11/      Лекція 11
   ├─ l13/      Лекція 13
   └─ extra/    додаткові теми (доповнення)
```

---

## Карта «лекція → екран → файл»

### Лекція 10 · Composable-функції, State

| Слайд(и) | Екран у застосунку | Файл |
|---|---|---|
| «Recomposition: що це таке», «Що запускає Recomposition», «Composable-функції: поглиблено» | Recomposition: хто перемальовується | `demos/l10/RecompositionDemo.kt` |
| «remember та mutableStateOf», «remember vs rememberSaveable» | remember vs rememberSaveable (+ власний Saver) | `demos/l10/RememberDemo.kt` |
| «State Hoisting: детальніше», «State\<T\> vs MutableState\<T\>» | State Hoisting і UDF | `demos/l10/StateHoistingDemo.kt` |
| «derivedStateOf» | derivedStateOf | `demos/l10/DerivedStateDemo.kt` |
| «remember з ключами» | remember з ключами | `demos/l10/RememberKeysDemo.kt` |
| «LaunchedEffect», «DisposableEffect та SideEffect» | Side Effects (журнал подій) | `demos/l10/SideEffectsDemo.kt` |
| «Стабільність типів», «Типові пастки продуктивності», «Debugging: метрики Compose Compiler» | Стабільність типів | `demos/l10/StabilityDemo.kt`, `app/build.gradle.kts` (блок `composeCompiler`) |
| «Практика: інтерактивний компонент» (кроки 1–4) | Кроки 1–2 — State Hoisting; 3 — Side Effects; 4 — remember vs rememberSaveable | див. вище |

### Лекція 11 · Layout, Modifier

| Слайд(и) | Екран у застосунку | Файл |
|---|---|---|
| «Row, Column, Box: базові контейнери» | Row, Column, Box | `demos/l11/ContainersDemo.kt` |
| «Arrangement», «Alignment» | Arrangement та Alignment (інтерактивна пісочниця) | `demos/l11/ArrangementAlignmentDemo.kt` |
| «Modifier: базовий принцип», «Порядок Modifier має значення» | Порядок Modifier (+ конструктор ланцюжка) | `demos/l11/ModifierOrderDemo.kt` |
| «Поширені модифікатори» | Поширені модифікатори (weight, IntrinsicSize…) | `demos/l11/CommonModifiersDemo.kt` |
| «Constraints: як передається розмір» | Constraints і адаптивність | `demos/l11/ConstraintsDemo.kt` |
| «LazyColumn / LazyRow», «Типові помилки layout» №1, №4, «Продуктивність layout» | LazyColumn / LazyRow і key | `demos/l11/LazyListsDemo.kt` |
| «Custom Layout: вступ» | Custom Layout | `demos/l11/CustomLayoutDemo.kt` |
| «ConstraintLayout у Compose» | ConstraintLayout | `demos/l11/ConstraintLayoutDemo.kt` |
| «Порівняння контейнерів» | огляд — екрани Containers, Lazy, ConstraintLayout, Custom Layout | — |
| «Практика: картка товару проєкту» (Пр4) | Практика: картка товару | `demos/l11/ProductCardDemo.kt` |

### Лекція 13 · Теми, Material3 (поглиблено)

| Слайд(и) | Екран у застосунку | Файл |
|---|---|---|
| «CompositionLocal: механізм», «MaterialTheme зсередини» | CompositionLocal | `demos/l13/CompositionLocalDemo.kt`, `ui/theme/Theme.kt` |
| «Від базової теми до системи дизайну» | ColorScheme: усі ролі | `demos/l13/ColorSchemeDemo.kt`, `ui/theme/Color.kt` |
| «Розширення теми власними токенами» | Власні токени (success, warning, info) | `demos/l13/ExtendedTokensDemo.kt`, `ui/theme/ExtendedColors.kt` |
| «Elevation та tonal color» | Elevation і tonal color | `demos/l13/ElevationDemo.kt` |
| «Компонентне тюнінгування», «Рівні кастомізації теми» | Компонентне тюнінгування | `demos/l13/ComponentTuningDemo.kt` |
| «Типографіка: кастомні шрифти» | Типографіка (+ fontScale) | `demos/l13/TypographyDemo.kt`, `ui/theme/Type.kt` |
| «Shapes: поглиблено» | Shapes | `demos/l13/ShapesDemo.kt`, `ui/theme/Shape.kt` |
| «Перемикання світлої/темної теми», «Dynamic Color», «Анімований перехід теми» | Перемикач теми і Dynamic Color | `demos/l13/ThemeSwitcherDemo.kt`, `ui/theme/ThemeSettings.kt`, `ui/theme/Theme.kt` |
| «Accessibility та контраст», «Типові помилки теми» №3 | Accessibility та контраст (живий розрахунок WCAG) | `demos/l13/AccessibilityDemo.kt` |
| «Тестування теми» | `@Preview` у `TextDemo.kt`, `StateHoistingDemo.kt`, `ProductCardDemo.kt` | — |
| «Практика: фінальна тема проєкту» | уся папка `ui/theme/` — готовий зразок | `ui/theme/*` |

---

## Доповнення: чого немає в презентаціях

| Тема | Екран | Файл | Куди рекомендовано вставити |
|---|---|---|---|
| Text, стилі, AnnotatedString, посилання, stringResource | Text і стилізація тексту | `demos/basics/TextDemo.kt` | перед / на початку Л10 |
| Button (5 видів), IconButton, IconToggleButton, FAB | Кнопки та FAB | `demos/basics/ButtonsDemo.kt` | Л10 |
| TextField / OutlinedTextField, клавіатура, пароль, валідація, фокус | Поля вводу | `demos/basics/InputsDemo.kt` | Л10 (місток до state hoisting) |
| Checkbox, TriState, Radio, Switch, Slider, RangeSlider, Chips, SegmentedButton | Елементи вибору | `demos/basics/SelectionControlsDemo.kt` | Л10 |
| Icon, Image, ContentScale, ColorFilter | Зображення та іконки | `demos/basics/ImagesIconsDemo.kt` | Л11 |
| AlertDialog, ModalBottomSheet, Snackbar, DropdownMenu, Badge, Progress | Діалоги, Snackbar, прогрес, меню | `demos/basics/FeedbackDemo.kt` | Л10 / Л11 |
| Scaffold, TopAppBar (4 види + scrollBehavior), NavigationBar, TabRow, Drawer | Scaffold, app bars | `demos/basics/ScaffoldDemo.kt` | Л11 |
| rememberCoroutineScope, rememberUpdatedState, snapshotFlow | (у Side Effects) | `demos/l10/SideEffectsDemo.kt` | Л10 |
| Анімації: AnimatedVisibility, animateContentSize, animate*AsState, Crossfade, AnimatedContent, updateTransition, InfiniteTransition | Анімації | `demos/extra/AnimationsDemo.kt` | окрема тема після Л13 |
| Жести (pointerInput, transformable), Canvas, drawBehind | Жести та Canvas | `demos/extra/GesturesCanvasDemo.kt` | після Л11 |
| LazyVerticalGrid, StaggeredGrid, HorizontalPager, FlowRow | Сітки, Pager, FlowRow | `demos/extra/GridsPagerDemo.kt` | Л11 |
| Navigation Compose: аргументи, результат, опції navigate | Navigation Compose | `demos/extra/NavigationArgsDemo.kt`, `navigation/AppNavHost.kt` | лекція про навігацію |
| ViewModel + StateFlow + collectAsStateWithLifecycle | ViewModel + StateFlow (UDF) | `demos/extra/ViewModelDemo.kt` | місток до блоку «Архітектура» |
| Семантика: mergeDescendants, clearAndSetSemantics, зона дотику 48 dp | (у Accessibility) | `demos/l13/AccessibilityDemo.kt` | Л13 |

---

## Зауваження до слайдів (знайдено під час реалізації)

1. **Л11, «Порядок Modifier», приклад 2** — `Modifier.background(Color.Blue).size(100.dp)` дає **такий самий** результат,
   як `size(100.dp).background(...)`: `background` малює рівно в межах обгорнутого елемента.
   Різниця з'являється лише коли між ними стоїть модифікатор розміщення (`padding`, `offset`). Демонстрація — екран «Порядок Modifier», розділ 2.
2. **Л10, «Стабільність типів»** — з Kotlin 2.0.20 за замовчуванням увімкнено **Strong Skipping**: composable з нестабільними
   параметрами теж може пропускатися (нестабільні порівнюються за `===`, стабільні — через `equals()`), а лямбди
   запам'ятовуються автоматично. Тому пастка «нестабільні лямбди» майже зникла, а `List<T>` шкодить лише коли щоразу створюється новий екземпляр.
3. **Л10, «Debugging: метрики Compose Compiler»** — синтаксис через `kotlinOptions.freeCompilerArgs` застарів для Kotlin 2.0+.
   Актуальний блок `composeCompiler { reportsDestination = …; metricsDestination = … }` — див. `app/build.gradle.kts`.
4. **Л10, «Типові пастки продуктивності»** — слайд незавершений (лише пункт 1 і порожній блок коду `//`).
   Пропоновані пункти 2–4: нестабільні / щоразу нові параметри-колекції; читання часто змінюваного стану високо в дереві
   (рішення — лямбда-провайдер або `derivedStateOf`); важкі обчислення в тілі composable без `remember(key)`.
5. **Л10, «State\<T\> vs MutableState\<T\>»** — рядок «Ключова особливість: читання» обірваний.
6. **Л11, «LazyColumn / LazyRow»** і **«Порівняння контейнерів»** — підсумкові виноски обірвані («🛠 Завжди задавайте», «📊 Використання»).
7. **Л11, «Constraints»** — `fillMaxSize()` заповнює *максимум* обмежень. Якщо максимум необмежений (наприклад, у вертикальному скролі),
   `fillMaxHeight()` нічого не робить. Демонстрація — екран «Constraints і адаптивність».
8. **Л13, «Dynamic Color»** — на одному слайді наслідок відсутнього fallback названо `NullPointerException`, на іншому — `NoSuchMethodError`.
   Варто уніфікувати: застосунок впаде з винятком на Android 11 і старіших, а Lint ще під час збирання показує помилку `NewApi`.
9. **Л13, «Elevation та tonal color»** — актуальні рекомендації Material3 радять замість розрахунку відтінку від elevation
   використовувати готові ролі `surfaceContainerLowest … surfaceContainerHighest` (показано на екрані «Elevation», розділ 3).

---

## Ідеї для практичних / домашніх завдань на основі застосунку

* Додати новий екран-демо: створити файл у `demos/`, додати один `Demo(...)` у `DemoCatalog.kt` — і він з'явиться на головному екрані.
* Зберегти вибір теми між запусками через DataStore Preferences (ДЗ 2 Лекції 13).
* Знайти в коді всі `@Preview` і додати варіант `uiMode = Configuration.UI_MODE_NIGHT_YES`.
* Увімкнути звіти Compose Compiler (`./gradlew :app:assembleRelease -PcomposeReports=true`) і знайти нестабільні класи.
