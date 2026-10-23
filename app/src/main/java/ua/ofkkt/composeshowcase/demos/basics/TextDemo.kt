package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.ofkkt.composeshowcase.R
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.theme.ShowcaseTheme

private const val LOREM =
    "Jetpack Compose — сучасний декларативний інструментарій для побудови нативного UI в Android. " +
        "Ви описуєте, ЯК має виглядати інтерфейс для поточного стану, а Compose сам оновлює екран, " +
        "коли стан змінюється. Цей текст навмисно довгий, щоб показати обрізання через maxLines."

@Composable
fun TextDemo() {
    DemoColumn {
        DemoSection(
            "1. Стилі з теми",
            note = "Не задавайте розмір шрифту вручну — беріть стиль з MaterialTheme.typography.",
        ) {
            Text("headlineSmall", style = MaterialTheme.typography.headlineSmall)
            Text("titleMedium", style = MaterialTheme.typography.titleMedium)
            Text("bodyLarge — основний текст", style = MaterialTheme.typography.bodyLarge)
            Text("labelSmall — підписи", style = MaterialTheme.typography.labelSmall)
            CodeSnippet("""Text("Заголовок", style = MaterialTheme.typography.titleMedium)""")
        }

        DemoSection("2. Параметри Text") {
            Text("Колір primary", color = MaterialTheme.colorScheme.primary)
            Text("fontSize = 20.sp, Bold", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Курсив", fontStyle = FontStyle.Italic)
            Text("Підкреслений", textDecoration = TextDecoration.Underline)
            Text("Закреслений", textDecoration = TextDecoration.LineThrough)
            Text("Моноширинний шрифт", fontFamily = FontFamily.Monospace)
            Text("letterSpacing = 4.sp", letterSpacing = 4.sp)
            Text("По центру", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }

        DemoSection("3. maxLines та overflow") {
            var expanded by rememberSaveable { mutableStateOf(false) }
            Text(
                text = LOREM,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Згорнути" else "Читати далі")
            }
            Tip("Стан expanded — звичайний Boolean у rememberSaveable. Текст перемальовується при зміні стану (Лекція 10).")
        }

        DemoSection(
            "4. AnnotatedString",
            note = "Кілька стилів і посилання в одному Text.",
        ) {
            val primary = MaterialTheme.colorScheme.primary
            val tertiary = MaterialTheme.colorScheme.tertiary
            val annotated = buildAnnotatedString {
                append("У Compose можна поєднувати ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primary)) { append("жирний") }
                append(", ")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = tertiary)) { append("курсив") }
                append(" і навіть ")
                withLink(
                    LinkAnnotation.Url(
                        url = "https://developer.android.com/develop/ui/compose/text",
                        styles = TextLinkStyles(
                            style = SpanStyle(color = primary, textDecoration = TextDecoration.Underline),
                        ),
                    ),
                ) { append("посилання на документацію") }
                append(".")
            }
            Text(annotated, style = MaterialTheme.typography.bodyLarge)
            CodeSnippet(
                """
                buildAnnotatedString {
                    append("Звичайний ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("жирний") }
                    withLink(LinkAnnotation.Url("https://…")) { append("посилання") }
                }
                """,
            )
        }

        DemoSection("5. Виділення тексту та ресурси") {
            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Цей текст можна виділити та скопіювати (довге натискання).")
                    Text(stringResource(R.string.greeting, "студенте"))
                }
            }
            CodeSnippet("""Text(stringResource(R.string.greeting, "студенте"))""")
            Tip("Рядки інтерфейсу тримайте в res/values/strings.xml — так їх можна перекласти іншими мовами.")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextDemoPreview() {
    ShowcaseTheme { TextDemo() }
}
