package ua.ofkkt.composeshowcase.demos.basics

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.launch
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.LocalSnackbarHostState
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind

private const val BIO_LIMIT = 120

@Composable
fun InputsDemo() {
    val focusManager = LocalFocusManager.current
    val snackbar = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var age by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }

    val emailFocus = remember { FocusRequester() }

    // Помилку показуємо лише коли щось уже введено — так UX дружніший.
    val emailError = email.isNotEmpty() && !email.contains('@')
    // derivedStateOf: кнопка «перераховується» лише коли змінюється результат true/false.
    val formValid by remember {
        derivedStateOf {
            name.isNotBlank() && email.contains('@') && password.length >= 6 && age.isNotEmpty()
        }
    }

    DemoColumn {
        DemoSection(
            "1. TextField — керований компонент",
            note = "Поле не зберігає текст саме: value приходить ззовні, onValueChange повідомляє про зміну (State hoisting).",
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ім'я") },
                placeholder = { Text("Наприклад, Олена") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                trailingIcon = {
                    if (name.isNotEmpty()) {
                        IconButton(onClick = { name = "" }) { Icon(Icons.Filled.Clear, "Очистити") }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() }),
            )
            CodeSnippet(
                """
                var name by rememberSaveable { mutableStateOf("") }
                TextField(value = name, onValueChange = { name = it }, label = { Text("Ім'я") })
                """,
            )
        }

        DemoSection("2. OutlinedTextField з валідацією") {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocus),
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                isError = emailError,
                supportingText = {
                    Text(if (emailError) "Email має містити символ @" else "Ми не розсилаємо спам")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
            )
        }

        DemoSection("3. Пароль: VisualTransformation") {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Пароль") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Сховати пароль" else "Показати пароль",
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                supportingText = { Text("Мінімум 6 символів · зараз ${password.length}") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            )
        }

        DemoSection("4. Лише цифри", note = "Клавіатура Number — лише підказка. Фільтрувати введення треба в onValueChange.") {
            OutlinedTextField(
                value = age,
                onValueChange = { input -> age = input.filter { it.isDigit() }.take(3) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Вік") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            )
        }

        DemoSection("5. Багаторядкове поле з лічильником") {
            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= BIO_LIMIT) bio = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Про себе") },
                minLines = 3,
                maxLines = 5,
                supportingText = { Text("${bio.length} / $BIO_LIMIT") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            )
        }

        DemoSection("6. Надсилання форми") {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    scope.launch { snackbar.showSnackbar("Форму надіслано: $name, $email") }
                },
                enabled = formValid,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Надіслати") }
            Tip(
                if (formValid) "Усі поля валідні — кнопка активна." else "Заповніть ім'я, email з @, пароль ≥ 6 символів і вік.",
                kind = if (formValid) TipKind.Success else TipKind.Warning,
            )
        }
    }
}
