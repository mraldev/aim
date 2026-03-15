package org.dam.tfg

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun App() {
    MaterialTheme {
        Navigator(MainScreen())
    }
}

class MainScreen : Screen {
    @Composable
    override fun Content() {

        var usuario: String by remember { mutableStateOf("") }
        var frase: String by remember { mutableStateOf("") }
        var contrasenya : String by remember { mutableStateOf("") }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,) {
            Spacer(modifier = Modifier.height(38.dp))
            Text("Usuario")
            TextField(value = usuario, onValueChange = { usuario = it })
            Text("Contraseña")
            TextField(value = contrasenya, onValueChange = { contrasenya = it })
            Text(frase)
            Button(  onClick = {
                if (usuario.isNotEmpty() && contrasenya.isNotEmpty()) {
                    var usuarioValido = (verificarUsuario(usuario) && verificarContrasenya(contrasenya))
                    if (usuarioValido) {
                        frase = "Bienvenido a AIM: $usuario"
                    }
                    else {
                        frase = "Datos introducidos no validos."
                    }
                    } else {
                        frase = "Faltan datos a introducir."
                    }
            }) {
                Text("LOGIN")
            }
        }
    }

    fun verificarUsuario(usuario:String): Boolean {
        if (usuario == "admin") {
            return true
        }
        return false
    }

    fun verificarContrasenya(contrasenya:String): Boolean {
        if (contrasenya == "admin") {
            return true
        }
        return false
    }
}