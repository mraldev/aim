package org.dam.tfg.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.border
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.dam.tfg.api.enumerados.RolesUsuario
import org.dam.tfg.model.Users
import org.dam.tfg.repository.HealthCheckRepository
import org.dam.tfg.repository.LoginRepository


private val usuarios = listOf(
    Users("admin", "admin", null,"Usuario admin demo", listOf(RolesUsuario.ADMIN)),
    Users("demo", "1234", "DEMO","Usuario normal demo", listOf(RolesUsuario.ADMIN))
)
//* Agregados usuarios basicos (eliminar en cuanto se haya logrado la conexion a la api)

var isLoading = false
val loginRepository = LoginRepository()

class Login: Screen {
    @Composable
    override fun Content() {

        //? Utils
        val navigator = LocalNavigator.currentOrThrow
        val healthRepository = HealthCheckRepository()
        //? Labels
        val password = "Contraseña"
        val user = "Nombre de usuario o correo electronico"
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
//                        if (isAccValid(correo)) {
//                            attemptLogin(correo, navigator) {
//                                frase.value = it
//                            }
//                        }
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
//                        if (isAccValid(correo)) {
//                            attemptLogin(correo, navigator) {
//                                frase.value = it
//                            }
//                        } else {
//                            createAccount(correo, contrasenya, navigator)
//                        }
                        navigator.push(Home())
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

    private suspend fun createAccount(correo: String, contrasenya: String, navigator: Navigator) {
        val logged = loginRepository.register(correo, contrasenya)

        if (logged) navigator.push(Home())
    }

    private fun getUser(correo: String): Users {
        //? funcion para devolver el usuario
        return usuarios[0]
    }

    private fun attemptLogin(user: Users, navigator: Navigator, function: (String) -> Unit) {
        //? devuelve el usuario, con todos sus datos, se los pasa a home
        //* esta funcion se podria eliminar si isAccValid devolviese el usuario
        if (user != null) {
            navigator.push(Home()) //! Decimos si el usuario es admin o no
        } else {
            function("Faltan datos a introducir.") //? Como ponemos  { frase.value = it } esto es una funcion, al hacer esto, hacemos que dicha funcion le ponga este valor a frase
        }
    }

    fun isAccValid(correo: String): Boolean? {
        //! Mirar si dicho usuario existe (ya que va por correo, estos son unicos)
        //* SELECT correo FROM users WHERE correo = ?
        return true
    }
}
