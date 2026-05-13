package org.dam.tfg.customElements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.customElements.Tirada.TiradaDialog
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.enums.UserRole
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.screens.competicion.CompeticionScreen
import org.dam.tfg.screens.Home
import org.dam.tfg.screens.Historial.HistorialScreen
import org.dam.tfg.screens.Login
import org.dam.tfg.screens.Profile
import org.dam.tfg.screens.TiradaScreen
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import org.dam.tfg.api.managers.UserManager

@Composable
fun buttonBar(
    modifier: Modifier = Modifier,
    navigator: Navigator? = null,
    userRole: UserRole? = UserManager.roles.value,
    userId: String = "",
    competiciones: List<Liga> = emptyList(),
    asociaciones: List<String> = emptyList(),
    tiradas: List<Tirada> = emptyList()
) {
    val nav = navigator ?: LocalNavigator.currentOrThrow
    var showContinueDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogNoRegistradoCompeticion by remember { mutableStateOf(false) }
    var showDialogNoAsociaciones by remember { mutableStateOf(false) }
    var showDialogNoRegistradoHistorial by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showDialog) {
        TiradaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { numDianas, flechas, participantes ->
                showDialog = false

                val tiradas = participantes.map { participante ->
                    Tirada(
                        usuario = UsuarioTiradaDTO(participante),
                        numDianas = numDianas,
                        numMaxFlechasPorDiana = flechas,
                        puntuaciones = mutableListOf(),
                        tipoCircuito = TipoCircuito.CUSTOM
                    )
                }

                val tirada = SesionEnviar(tiradas)

                SesionManager.setSesion(tirada)
                nav.pop()
                nav.push(TiradaScreen())
            }
        )
    }

    if (showDialogNoRegistradoCompeticion) {
        DialogBase(
            data = mapOf(
                "header" to "Sesión no iniciada",
                "content" to "Se necesita una cuenta para acceder a las competiciones.",
                "confirmButton" to "Iniciar sesión",
                "dismissButton" to "Mejor en otro momento"
            ),
            onConfirm = {
                nav.pop()
                nav.push(Login())
            },
            onDismiss = { showDialogNoRegistradoCompeticion = false }
        )
    }

    if (showDialogNoRegistradoHistorial) {
        DialogBase(
            data = mapOf(
                "header" to "Sesión no iniciada",
                "content" to "Se necesita una cuenta para acceder al historial.",
                "confirmButton" to "Iniciar sesión",
                "dismissButton" to "Mejor en otro momento"
            ),
            onConfirm = {
                nav.pop()
                nav.push(Login())
            },
            onDismiss = { showDialogNoRegistradoHistorial = false }
        )
    }

    if (showDialogNoAsociaciones) {
        DialogBase(
            data = mapOf(
                "header" to "Sin asociación",
                "content" to "Se necesita estar registrado en una asociación para acceder a las competiciones.",
                "confirmButton" to "Vale"
            ),
            onConfirm = {
                showDialogNoAsociaciones = false
            }
        )
    }

    if (showContinueDialog) {
        val saved = SesionManager.sesionEnviar.value
        AlertDialog(
            onDismissRequest = { showContinueDialog = false },
            title = { Text("Tirada guardada") },
            text = {
                Text(
                    "Tienes una tirada en progreso. ¿Quieres continuar?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showContinueDialog = false
                    saved?.let {
                        SesionManager.setSesion(it)
                        nav.pop()
                        nav.push(TiradaScreen())
                    }
                }) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = {
                    SesionManager.clear()
                    showContinueDialog = false
                    showDialog = true
                }) { Text("No") }
            }
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(65.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedButton(
            onClick = {
                nav.pop()
                nav.push(Home())
            }
        ) {
            Icon(Icons.Filled.Home, tint = Color.Black, contentDescription = "Home")
        }

        AnimatedButton(
            onClick = {
                scope.launch {
                    if (!TokenManager.isLoggedIn()) {
                        showDialogNoRegistradoCompeticion = true
                    //else if (UserManager.asociaciones.value.isEmpty()) showDialogNoAsociaciones = true
                    } else {
                        nav.pop()
                        nav.push(
                            CompeticionScreen(
                                userRole = UserManager.roles.value,
                                asociacionesUsuario = userId
                            )
                        )
                    }
                }

            }
        ) {
            Icon(Icons.Filled.EmojiEvents, tint = Color.Black, contentDescription = "Competición")
        }

        AnimatedButton(
            onClick = {
                if (SesionManager.sesionEnviar.value != null) {
                    showContinueDialog = true
                } else {
                    showDialog = true
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Nueva tirada",
                tint = Color.Black,
                modifier = Modifier.size(40.dp)
            )
        }

        AnimatedButton(
            onClick = {
                scope.launch {
                    if (!TokenManager.isLoggedIn()) showDialogNoRegistradoHistorial = true
                    else{
                        nav.pop()
                        nav.push(HistorialScreen())
                    }
                }
            }
        ) {
            Icon(Icons.Filled.History, tint = Color.Black, contentDescription = "Historial")
        }

        AnimatedButton(
            onClick = {
                nav.pop()
                nav.push(Profile())
            }
        ) {
            Icon(Icons.Filled.Person, tint = Color.Black, contentDescription = "Perfil")
        }
    }
}