package org.dam.tfg.customElements

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.customElements.Tirada.TiradaDialog
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.enums.UserRole
import org.dam.tfg.model.Competicion
import org.dam.tfg.screens.competicion.CompeticionScreen
import org.dam.tfg.screens.Home
import org.dam.tfg.screens.HistorialScreen
import org.dam.tfg.screens.Profile
import org.dam.tfg.screens.TiradaScreen

@Composable
fun buttonBar(
    modifier: Modifier = Modifier,
    navigator: Navigator? = null,
    userRole: UserRole = UserRole.ADMIN, //! Cambiar
    userId: String = "",
    competiciones: List<Competicion> = emptyList(),
    asociaciones: List<String> = emptyList(),
    tiradas: List<Tirada> = emptyList(),
    onParticipar: (String) -> Unit = {},
    onDejarParticipar: (String) -> Unit = {},
    onEliminarParticipante: (String, String) -> Unit = { _, _ -> },
    onGuardarCompeticion: (Competicion) -> Unit = {},
    onCancelarCompeticion: (String) -> Unit = {},
    onApuntarTirada: (Int, Int, List<String>) -> Unit = { _, _, _ -> },
    onTiradasUpdated: (List<Tirada>) -> Unit = {}
) {
    val nav = navigator ?: LocalNavigator.currentOrThrow
    var showContinueDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

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
                        tipoCircuito = TipoCircuito.CUSTOM,
                        fecha = LocalDate(2024, 1, 1)
                    )
                }

                val sesion = Sesion(tiradas)
                nav.push(TiradaScreen(sesion))
            }
        )
    }

    if (showContinueDialog) {
        val saved = SesionManager.sesion.value
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
                    saved?.let { sesion -> nav.push(TiradaScreen(sesion)) }
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
        Button(onClick = { nav.push(Home()) }) {
            Icon(Icons.Filled.Home, contentDescription = "Home")
        }

        Button(onClick = {
            nav.push(
                CompeticionScreen(
                    userRole = userRole,
                    userId = userId,
                    competiciones = emptyList<Competicion>(),
                    onParticipar = onParticipar,
                    onDejarParticipar = onDejarParticipar,
                    onEliminarParticipante = onEliminarParticipante,
                    onGuardarCompeticion = onGuardarCompeticion,
                    onCancelarCompeticion = onCancelarCompeticion,
                    onApuntarTirada = onApuntarTirada
                )
            )
        }) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = "Competición")
        }

        AnimatedButton(
            text = "+",
            onClick = {
                if (SesionManager.sesion.value != null) {
                    showContinueDialog = true
                } else {
                    showDialog = true
                }
            }
        )

        Button(onClick = { nav.push(HistorialScreen()) }) {
            Icon(Icons.Filled.History, contentDescription = "Historial")
        }

        Button(onClick = { nav.push(Profile()) }) {
            Icon(Icons.Filled.Person, contentDescription = "Perfil")
        }
    }
}