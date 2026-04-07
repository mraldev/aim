package org.dam.tfg.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dam.tfg.api.ApiController
import org.dam.tfg.model.Users
import org.dam.tfg.repository.HealthCheckRepository


private val usuarios = listOf(
    Users("admin", "admin", null,"Usuario admin demo",true),
    Users("demo", "1234", "DEMO","Usuario normal demo", false)
)
//* Agregados usuarios basicos (eliminar en cuanto se haya logrado la conexion a la api)
val apiController = ApiController()

class Login: Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val repository = HealthCheckRepository()
        var status by remember { mutableStateOf("Loading...") }

        var usuario: String by remember { mutableStateOf("") }
        val frase = remember { mutableStateOf("") }
        var contrasenya: String by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() } //? Focus dinamico

        LaunchedEffect(Unit) { //? Focus dinamico, al iniciar la app, hace focus en el campo de usuario
            focusRequester.requestFocus()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(38.dp))
            Text("Usuario")
            TextField(
                value = usuario,
                onValueChange = { usuario = it },
                modifier = Modifier.focusRequester(focusRequester)
            )
            Text("Contraseña")
            TextField(
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { attemptLogin(usuario, contrasenya, navigator) { frase.value = it } }
                ),
                value = contrasenya,
                onValueChange = { contrasenya = it },
            )
            Text(frase.value)
            Button(onClick = {
                if (usuario.isNotEmpty() && contrasenya.isNotEmpty()) {
                    attemptLogin(usuario, contrasenya, navigator) { frase.value = it }
                } else {
                    frase.value = "Faltan datos a introducir."
                }
            }) {
                Text("LOGIN")
            }
            LaunchedEffect(Unit) {
                status = repository.getHealthStatus()
            }
            Text("Server status: $status")
        }

    }

    fun attemptLogin(usuario: String, contrasenya: String, navigator: Navigator, function: (String) -> Unit) {
        val user = isAccValid(usuario, contrasenya)
        if (user != null) {
            navigator.push(Home(user)) //! Decimos si el usuario es admin o no
        } else {
            function("Faltan datos a introducir.") //? Como ponemos  { frase.value = it } esto es una funcion, al hacer esto, hacemos que dicha funcion le ponga este valor a frase
        }
    }

    fun isAccValid(usuario: String, contrasenya: String): Users? {
        return usuarios.find { it.usuario == usuario && it.contrasenya == contrasenya } //? Unida la logica de login anterior, devolvemos el usuario (en bbdd, se sacara de alli, ya que este atributo de admin es un boolean alli)
    }
}
