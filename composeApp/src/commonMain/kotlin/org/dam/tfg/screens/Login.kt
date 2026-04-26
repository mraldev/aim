package org.dam.tfg.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.dam.tfg.exceptions.ExcepcionContrasenyaIncorrecta
import org.dam.tfg.repository.HealthCheckRepository
import org.dam.tfg.repository.LoginRepository

class Login : Screen {
    private val loginRepository = LoginRepository()
    private val healthRepository = HealthCheckRepository()

    @Composable
    override fun Content() {

        //? Utils
        val navigator = LocalNavigator.currentOrThrow
        //? Labels
        val password = "Contraseña"
        val user = "Correo electronico"
        //? remembers (animaciones y otras cosas dinamicas)
        var status by remember { mutableStateOf("Loading...") }
        var isLoading by remember { mutableStateOf(false) }
        var login by remember { mutableStateOf(true) }
        var correo by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var messageKey by remember { mutableIntStateOf(0) }
        var showMessage by remember { mutableStateOf(false) }
        var contrasenya by remember { mutableStateOf("") }
        var confirmarContrasenya by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        var passwordFocused by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var confirmPasswordError by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        //- Muestra el mensaje 1 segundo con fade, se relanza aunque el error sea el mismo
        LaunchedEffect(messageKey) {
            if (messageKey > 0) {
                showMessage = true
                delay(1000)
                showMessage = false
            }
        }

        //? Centralizado el intento de registro con validación
        fun launchRegister() {
            scope.launch {
                isLoading = true
                if (contrasenya != confirmarContrasenya) {
                    confirmPasswordError = true
                    message = "Las contraseñas no coinciden."
                    messageKey++
                } else {
                    confirmPasswordError = false
                    createAccount(correo, contrasenya, navigator) {
                        message = it
                        messageKey++
                    }
                }
                isLoading = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {

            //- Botón retroceso
            IconButton(
                onClick = { navigator.pop() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            //- Codigo de la UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(38.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                ) {
                    Text(
                        text = if (login) "Inicio de sesión" else "Crear cuenta",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text("Usuario")
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text(user) },
                    singleLine = true,
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
                Text(password)
                OutlinedTextField(
                    value = contrasenya,
                    onValueChange = {
                        contrasenya = it
                        //? Revalida en tiempo real si el campo de confirmación ya tiene texto
                        if (confirmarContrasenya.isNotEmpty()) {
                            confirmPasswordError = it != confirmarContrasenya
                        }
                    },
                    label = { Text(password) },
                    shape = RoundedCornerShape(40.dp),
                    isError = passwordError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = if (login) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (login) {
                                scope.launch {
                                    isLoading = true
                                    attemptLogin(correo, contrasenya, navigator) {
                                        message = it
                                        messageKey++
                                    }
                                    isLoading = false
                                }
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    supportingText = {
                        if (!login && passwordFocused && contrasenya.length < 8) {
                            Text("La contraseña debe tener un minimo de 8 caracteres")
                        }
                    }
                )

                //? Campo "Confirmar contraseña" (solo en modo registro)
                AnimatedVisibility(
                    visible = !login,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically(
                                animationSpec = tween(300),
                                initialOffsetY = { -it / 2 }
                            ),
                    exit = fadeOut(animationSpec = tween(300)) +
                            slideOutVertically(
                                animationSpec = tween(300),
                                targetOffsetY = { -it / 2 }
                            )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar contraseña")
                        OutlinedTextField(
                            value = confirmarContrasenya,
                            onValueChange = {
                                confirmarContrasenya = it
                                confirmPasswordError = it != contrasenya
                            },
                            label = { Text("Confirmar contraseña") },
                            shape = RoundedCornerShape(40.dp),
                            isError = confirmPasswordError,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { launchRegister() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                            supportingText = {
                                if (confirmPasswordError && confirmarContrasenya.isNotEmpty()) {
                                    Text("Las contraseñas no coinciden")
                                }
                            }
                        )
                    }
                }

                Text(
                    text = "Sin cuenta?",
                    modifier = Modifier.clickable {
                        login = !login
                        //- Limpia el campo de confirmación al cambiar de modo
                        confirmarContrasenya = ""
                        confirmPasswordError = false
                    }
                )
                Button(
                    onClick = {
                        if (login) {
                            scope.launch {
                                isLoading = true
                                attemptLogin(correo, contrasenya, navigator) {
                                    message = it
                                    messageKey++
                                }
                                isLoading = false
                            }
                        } else {
                            launchRegister()
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        val texto = if (login) "INICIAR SESION" else "CREAR CUENTA"
                        Text(text = texto)
                    }
                }
                LaunchedEffect(Unit) {
                    status = healthRepository.getHealthStatus()
                }
                Text("Server status: $status")
                AnimatedVisibility(
                    visible = showMessage,
                    enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = androidx.compose.animation.core.EaseInOut)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 400, easing = androidx.compose.animation.core.EaseInOut))
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    private suspend fun createAccount(correo: String, contrasenya: String, navigator: Navigator, function: (String) -> Unit) {
        if (!correoValido(correo)) {
            function("Formato de correo incorrecto.")
            return
        }
        val logged = loginRepository.register(correo, contrasenya)
        if (logged) navigator.push(Home())
        else function("Error al crear cuenta.")
    }

    private suspend fun attemptLogin(correo: String, contrasenya: String, navigator: Navigator, function: (String) -> Unit) {
        if (correo.isNotBlank() && contrasenya.isNotBlank()) {
            if (!correoValido(correo)) {
                function("Formato de correo incorrecto.")
                return
            }

            if (healthRepository.isServerActive()) {
                try {
                    if (loginRepository.login(correo, contrasenya)) {
                        navigator.push(Home())
                    } else {
                        function("Contraseña incorrecta.")
                    }
                } catch (exception: ExcepcionContrasenyaIncorrecta) {
                    function(exception.message ?: "Error de contraseña")
                } catch (exception: Exception) {
                    function("Error en el inicio de sesión. Intente de nuevo más tarde.")
                }
            } else {
                function("Servidor no disponible")
            }
        } else {
            function("El usuario o la contraseña son incorrectos.")
        }
    }

    private fun correoValido(email: String): Boolean {
        val regex = Regex(
            pattern = "^[A-Za-z0-9+._%\\-]{1,64}@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$",
            option = RegexOption.IGNORE_CASE
        )
        return regex.matches(email)
    }
}