package org.dam.tfg.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.recalculateWindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import kotlinx.coroutines.launch
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.Tirada.TiradaDialog
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.model.Tirada.Tirada

class Settings() : Screen {
    private fun limpiarManagers() {
        SesionManager.clear()
        UserManager.clear()
        TokenManager.clear()
    }

    @Composable
    override fun Content() {
        var contrasenyaNueva: String by remember { mutableStateOf("") }
        var correoNuevo: String by remember { mutableStateOf("") }

        val regex = Regex(
            pattern = "^[A-Za-z0-9+._%\\-]{1,64}@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$",
            option = RegexOption.IGNORE_CASE
        )

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
                    suspend {
                        ApiClient.client.post(
                            "${ApiConfig.BASE_URL}/baja/" + UserManager.correo.value
                        )
                    }
                    limpiarManagers()
                    navigator.push(Home());
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
                            if (correoNuevo.isNullOrEmpty() || !regex.matches(correoNuevo)) {
                                correoNuevo = "Correo no valido."
                            } else {
                                UserManager.setCorreo(correoNuevo)
                                showEmailDialog = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton =
                    {
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
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                },
                shape = MaterialTheme.shapes.large,
            )
        }
        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (correoNuevo.isNullOrEmpty()) {
                                contrasenyaNueva = "Correo no valido."
                            } else {
                                UserManager.setCorreo(correoNuevo)
                                showPasswordDialog = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton =
                    {
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
                        shape = RoundedCornerShape(40.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                },
                shape = MaterialTheme.shapes.large,
            )
        }


        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp)
                    .fillMaxSize().recalculateWindowInsets(),
                horizontalAlignment = Alignment.Start,
            ) {
//.*[@].*[.].* TOMA PUTO REGEX
                //El regex ya ha sido implementado en LogIn, por favor que se use el mismo formato
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
                    Button(
                        onClick = { showPasswordDialog = true },
                        modifier = Modifier.width(200.dp)
                    ) { Text("Cambiar contraseña") }

                }
                //TODO : Funcionalidad para confirmación de cambiar de correo de forma segura.
                Row {
                    Button(
                        onClick = { showEmailDialog = true },
                        modifier = Modifier.width(200.dp)
                    ) { Text("Cambiar Correo") }
                }
                Spacer(modifier = Modifier.height(50.dp))

                Row {
                    Button(
                        onClick = {
                            limpiarManagers()
                            navigator.push(Home());
                        },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
                //por el amor de dios no la utiliceis sin cuentas mock solo para probarlo
                Row {
                    Button(onClick = {
                        showConfirmationDialog = true;

                    }, modifier = Modifier.width(200.dp)) { Text("Dar la cuenta de baja") }
                }
            }
            Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator)
            }
        }
    }
}

