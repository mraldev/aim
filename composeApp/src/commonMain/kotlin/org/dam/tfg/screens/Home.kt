package org.dam.tfg.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.model.Users

class Home(private val usuario: Users): Screen {
    //? En esta ventana no vamos a usar isAdmin, pero de aqui se la podemos pasar a todas las demas ventanas
    //? lo que significa que podemos saber cuando un usuario es admin en cualquier punto de la app

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(bottom = 80.dp) //? Padding para los botones sticky
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = usuario.usuario, //? usuario (correo)
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (usuario.name != null) {
                            Text(
                                text = usuario.name, //? nombre (nombre de la persona, usado en eventos)
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bio",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                repeat(100) {
                    Text(
                        text = usuario.descripcion //? Biografia del usuario
                        //* Si hay mas texto que el de la pantalla, este se deberia de hacer scrolleable, por el verticalScroll
                    )
                }
            }
            //? Barra de botones
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //? De momento todos llevan al login (si se quiere agregar uno mas, se agrega
                Button(onClick = { navigator.push(Login()) }) { Text("Btn1") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn2") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn3") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn4") }
            }
        }
    }
}
