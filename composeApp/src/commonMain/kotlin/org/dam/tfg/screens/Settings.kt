package org.dam.tfg.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar

class Settings : Screen {
    private fun limpiarManagers() {
        SesionManager.clear()
        UserManager.clear()
        TokenManager.clear()
    }

    @Composable
    override fun Content() {
        var contrasenyaNueva by remember { mutableStateOf("") }
        var correoNuevo by remember { mutableStateOf("") }

        val regex = remember {
            Regex(
                pattern = "^[A-Za-z0-9+._%\\-]{1,64}@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$",
                option = RegexOption.IGNORE_CASE
            )
        }

        val navigator = LocalNavigator.currentOrThrow

        var showEmailDialog by remember { mutableStateOf(false) }
        var showPasswordDialog by remember { mutableStateOf(false) }
        var showConfirmationDialog by remember { mutableStateOf(false) }

        if (showConfirmationDialog) {
            DialogBase(
                data = mapOf(
                    "header" to "Dar de baja",
                    "content" to "¿Seguro que quieres dar de baja tu cuenta?",
                    "confirmButton" to "Confirmar",
                    "dismissButton" to "Cancelar"
                ),
                onConfirm = {
                    limpiarManagers()
                    navigator.push(Home())
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }

        if (showEmailDialog) {
            AlertDialog(
                onDismissRequest = { showEmailDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (correoNuevo.isBlank() || !regex.matches(correoNuevo)) {
                                correoNuevo = ""
                            } else {
                                UserManager.setCorreo(correoNuevo)
                                showEmailDialog = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEmailDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Cambiar Email") },
                text = {
                    OutlinedTextField(
                        value = correoNuevo,
                        onValueChange = { correoNuevo = it },
                        label = { Text("Nuevo Email") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
            )
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (contrasenyaNueva.isBlank() || contrasenyaNueva.length < 8) {
                                contrasenyaNueva = ""
                            } else {
                                UserManager.setContrasenya(contrasenyaNueva)
                                showPasswordDialog = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPasswordDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Cambiar Contraseña") },
                text = {
                    OutlinedTextField(
                        value = contrasenyaNueva,
                        onValueChange = { contrasenyaNueva = it },
                        label = { Text("Nueva Contraseña") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
            ) {
                profileBar(navigator)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AJUSTES DE CUENTA",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(text = "Correo actual = " + (UserManager.correo.value ?: ""))
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Button(
                        onClick = { showPasswordDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cambiar contraseña") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Button(
                        onClick = { showEmailDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cambiar Correo") }
                }

                Row{
                    Text("Núm. Federado: "+UserManager.numFederado.value.toString())
                }
                Row{
                    Text("Asocioaciones: "+UserManager.asociaciones.value.toString())
                }
                Row{
                    Text("Nombre: "+UserManager.nombre.value.toString())
                }
                Row{
                    Text("Fecha Nacimiento: "+UserManager.fecNac.value.toString())
                }
                Row{
                    Text("Sexo: "+ UserManager.genero.value.toString())
                }
                Spacer(modifier = Modifier.height(50.dp))

                Row {
                    Button(
                        onClick = {
                            limpiarManagers()
                            navigator.push(Login())
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar Sesión")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Button(
                        onClick = { showConfirmationDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Dar la cuenta de baja") }
                }
            }
            Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator)
            }
        }
    }
}