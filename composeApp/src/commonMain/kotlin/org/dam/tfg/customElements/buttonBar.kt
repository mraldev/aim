package org.dam.tfg.customElements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.first
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.Tirada.TiradaDialog
import org.dam.tfg.dto.FederadoTiradaDto
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.enums.UserRole
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.model.competiciones.TiradaCompetitiva
import org.dam.tfg.screens.competicion.CompeticionScreen
import org.dam.tfg.screens.Home
import org.dam.tfg.screens.HistorialScreen
import org.dam.tfg.screens.Login
import org.dam.tfg.screens.Profile
import org.dam.tfg.screens.TiradaScreen

@Composable
fun buttonBar(
    modifier: Modifier = Modifier,
    navigator: Navigator? = null,
    userRole: UserRole = UserRole.ADMIN, //! Cambiar
    userId: String = "",
    competiciones: List<Liga> = emptyList(),
    asociaciones: List<String> = emptyList(),
    tiradas: List<Tirada> = emptyList(),
    onParticipar: (Liga) -> Unit = {},
    onDejarParticipar: (Liga) -> Unit = {},
    onEliminarParticipante: (Liga, String) -> Unit = { _, _ -> },
    onGuardarCompeticion: (Liga) -> Unit = {},
    onCancelarCompeticion: (Liga) -> Unit = {},
    onApuntarTirada: (Int, Int, List<String>) -> Unit = { _, _, _ -> },
    onTiradasUpdated: (List<Tirada>) -> Unit = {}
) {
    val nav = navigator ?: LocalNavigator.currentOrThrow
    var showContinueDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogNoRegistradoCompeticion by remember { mutableStateOf(false) }
    var showDialogNoAsociaciones by remember { mutableStateOf(false) }

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
        Button(onClick = {
            nav.pop()
            nav.push(Home())
        }) {
            Icon(Icons.Filled.Home, contentDescription = "Home")
        }

        Button(onClick = {
            if (TokenManager.isLoggedIn) showDialogNoRegistradoCompeticion = true;
            //! agregar un not (!) para que se haga bien la validación
            else if(!UserManager.asociaciones.value.isEmpty()) showDialogNoAsociaciones = true
            //! quitar el not para que se haga bien la validación
            //! las validaciones están así para que se pueda probar directamente la app
            else {
                nav.pop()
                nav.push(
                    CompeticionScreen(
                        userRole = userRole,
                        asociacionesUsuario = userId,
                        competiciones = emptyList<Liga>(),
                        onParticipar = onParticipar,
                        onDejarParticipar = onDejarParticipar,
                        onEliminarParticipante = onEliminarParticipante,
                        onGuardarCompeticion = onGuardarCompeticion,
                        onCancelarCompeticion = onCancelarCompeticion,
                        onApuntarTirada = onApuntarTirada
                    )
                )
            }
        }) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = "Competición")
        }

        AnimatedButton(
            text = "+",
            onClick = {
                if (SesionManager.sesionEnviar.value != null) {
                    showContinueDialog = true
                } else {
                    showDialog = true
                }
            }
        )

        Button(onClick = {
            nav.pop()
            nav.push(HistorialScreen())
        }) {
            Icon(Icons.Filled.History, contentDescription = "Historial")
        }

        Button(onClick = {
            nav.pop()
            nav.push(Profile())
        }) {
            Icon(Icons.Filled.Person, contentDescription = "Perfil")
        }
    }
}