package org.dam.tfg.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch
import org.dam.tfg.exceptions.ExcepcionContrasenyaIncorrecta
import org.dam.tfg.repository.HealthCheckRepository
import org.dam.tfg.repository.LoginRepository

private var isLoading = false
private val loginRepository = LoginRepository()
private val healthRepository = HealthCheckRepository()

class Login: Screen {
    @Composable
    override fun Content() {

        //? Utils
        val navigator = LocalNavigator.currentOrThrow
        //? Labels
        val password = "Contraseña"
        val user = "Correo electronico"
        //? remembers (animaciones y otras cosas dinamicas)
        var status by remember { mutableStateOf("Loading...") }
        var login by remember { mutableStateOf(true) } //? Para poder generar componentes al hacer login y cambiar la peticion en caso necesario
        var correo: String by remember { mutableStateOf("") }
        val frase = remember { mutableStateOf("") }
        var contrasenya: String by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        var passwordFocused by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) { //? Focus dinamico, al iniciar la app, hace focus en el campo de usuario
            focusRequester.requestFocus()
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(38.dp))
            AnimatedVisibility (
                visible = true,
                enter = fadeIn() + expandVertically(),
            ) {Text(
                text = if (login) "Inicio de sesión" else "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium
            )}
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
                onValueChange = { contrasenya = it },
                label = { Text(password) },
                shape = RoundedCornerShape(40.dp),
                isError = passwordError,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        scope.launch {
                            isLoading = true
                            if (login) {
                                attemptLogin(correo, contrasenya, navigator) {
                                    frase.value = it
                                }
                            } else {
                                createAccount(correo, contrasenya, navigator) {
                                    frase.value = it
                                }
                            }
                            isLoading = false
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
            Text(
                text = "Sin cuenta?",
                modifier = Modifier.clickable {
                    login = !login
                }
            )
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        if (login) {
                            attemptLogin(correo, contrasenya, navigator) {
                                frase.value = it
                            }
                        } else {
                            createAccount(correo, contrasenya, navigator) {
                                frase.value = it
                            }
                        }
                        isLoading = false
                    }
            }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    val texto = if (login) "INICIAR SESION" else "CREAR CUENTA"
                    Text( text=texto )
                }
            }
            LaunchedEffect(Unit) {
                status = healthRepository.getHealthStatus()
            }
            Text("Server status: $status")
            Text(frase.value)
        }

    }

    private suspend fun createAccount(correo: String, contrasenya: String, navigator: Navigator, function: (String) -> Unit) {
        //TODO mejorar!!!
        val logged = loginRepository.register(correo, contrasenya)

        if (logged) navigator.push(Home())
        else function("Error al crear cuenta.")
    }

    private suspend fun attemptLogin(correo: String, contrasenya: String, navigator: Navigator, function: (String) -> Unit) {
        //? devuelve el usuario, con todos sus datos, se los pasa a home
        //* esta funcion se podria eliminar si isAccValid devolviese el usuario

        if (correo.isNotBlank() && contrasenya.isNotBlank()) {
            if(!correoValido(correo)){
                function("Formato de correo incorrecto.")
                return
            }

            if(healthRepository.isServerActive()){
                try{
                    if(loginRepository.login(correo, contrasenya)){
                        navigator.push(Home())
                    } else {
                        //TODO mejorar, podría fallar por más cosas
                        function("Contraseña incorrecta.")
                    }
                } catch (exception: ExcepcionContrasenyaIncorrecta){
                    function(exception.message!!)
                } catch (exception: Exception){
                    function("Error en el inicio de sesión. Intente de nuevo más tarde.")
                }
            }
        } else {
            function("El usuario o la contraseña son incorrectos.") //? Como ponemos  { frase.value = it } esto es una funcion, al hacer esto, hacemos que dicha funcion le ponga este valor a frase
        }
    }

    private fun isAccValid(correo: String): Boolean {
        //! Mirar si dicho usuario existe (ya que va por correo, estos son unicos)
        //* SELECT correo FROM users WHERE correo = ?
        return true
    }

    private fun correoValido(email: String): Boolean {
        val regex = Regex(
            pattern = "^[A-Za-z0-9+._%\\-]{1,64}@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$",
            option = RegexOption.IGNORE_CASE
        )
        return regex.matches(email)
    }
}
