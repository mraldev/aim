package org.dam.tfg.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.client.request.post
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar

class Settings(): Screen {
    @Composable
    override fun Content() {
        var contrasenyaNueva: String by remember { mutableStateOf("") }
        var correoNuevo: String by remember { mutableStateOf("") }

        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp)
                    .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                profileBar(navigator)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AJUSTES DE CUENTA",
                    style = MaterialTheme.typography.titleLarge
                )
                //De momento se guarda tan solo en el manager local hasta que pueda guardar de forma completamente
                //segura mediante la API.
                //TODO : AÑADIR FUNCIONALIDAD DE LOS DIFERENTES BOTONES
                Row {
                    OutlinedTextField(
                        value = contrasenyaNueva,
                        onValueChange = { contrasenyaNueva = it },
                        label = { Text("Nueva Contraseña") },
                        singleLine = true,
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }

                Row {
                    Button(onClick = { UserManager.setContrasenya(contrasenyaNueva) } , modifier = Modifier.width(200.dp)) { Text("Cambiar contraseña") }
                }
                Row {
                    OutlinedTextField(
                        value = correoNuevo,
                        onValueChange = { correoNuevo = it },
                        label = { Text("Nuevo Correo") },
                        singleLine = true,
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
                //TODO : Funcionalidad para confirmación de cambiar de correo de forma segura.
                Row {
                    Button(onClick = { UserManager.setCorreo(correoNuevo) }, modifier = Modifier.width(200.dp)) { Text("Cambiar Correo") }
                }
                Spacer(modifier = Modifier.height(50.dp))

                Row {
                    Button(onClick = { navigator.push(Login()); } , modifier = Modifier.width(200.dp)) { Text("Cerrar Sesión") }
                }
                //por el amor de dios no la utiliceis sin cuentas mock solo para probarlo
                Row {
                    Button(onClick = {
                        suspend {
                            ApiClient.client.post(
                                "${ApiConfig.BASE_URL}/baja/" + UserManager.correo.toString()
                            )
                        }
                    } , modifier = Modifier.width(200.dp)) { Text("Dar la cuenta de baja") }
                }
                }
            Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator)
            }
        }
    }
}
