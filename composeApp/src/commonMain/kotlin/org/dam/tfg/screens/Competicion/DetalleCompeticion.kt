package org.dam.tfg.screens.competicion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.DialogContent
import org.dam.tfg.dto.DatosCompeticionDto
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.Estilo
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.Posicion
import org.dam.tfg.enums.RangoDeEdad
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.enums.UserRole
import org.dam.tfg.helpers.HelperCargadorDeTipoDeTirada
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.screens.Historial.HistorialScreen
import org.dam.tfg.screens.TiradaScreen

@Composable
internal fun DetalleCompeticion(
    competicion: org.dam.tfg.dto.LigaPreview,
    userRole: org.dam.tfg.enums.UserRole?,
    userId: kotlin.String,
    onBack: () -> kotlin.Unit,
    onParticipar: () -> kotlin.Unit,
    onDejarParticipar: () -> kotlin.Unit,
    onEditar: () -> kotlin.Unit,
    onCancelarCompeticion: () -> kotlin.Unit,
    onEliminarParticipante: (kotlin.String) -> kotlin.Unit,
    onApuntarTirada: (kotlin.Int, kotlin.Int, kotlin.collections.List<kotlin.String>) -> kotlin.Unit
) {
}
/*
{

    val esParticipante = competicion.sesionesCompetidas.any { sesion ->
        sesion.tiradasCompetitivas.any { tirada ->
            tirada.usuario.correo == userId
        }
    }
    var mostrarDialog by remember { mutableStateOf(false) }
    var mostrarConfirmCancelar by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver")
            }
            Text(userId, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            if (userRole == UserRole.ADMIN) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        InfoRow(label = "Asociación",    value = competicion.sesionesCompetidas[0].tiradasCompetitivas[0].asociacion.label)
        InfoRow(label = "Tipo circuito", value = competicion.sesionesCompetidas[0].tiradasCompetitivas[0].tipoCircuito.label)
        InfoRow(label = "Fecha",         value = competicion.fecha.toString())
        InfoRow(label = "Dianas",        value = competicion.sesionesCompetidas[0].tiradasCompetitivas[0].numDianas.toString())
        InfoRow(label = "Flechas / diana", value = competicion.sesionesCompetidas[0].tiradasCompetitivas[0].numMaxFlechasPorDiana.toString())

        Spacer(Modifier.height(20.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        when (userRole) {
            UserRole.FEDERADO -> {
                if (esParticipante) {
                    OutlinedButton(
                        onClick = onDejarParticipar,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Dejar de participar") }
                } else {
                    Button(
                        onClick = onParticipar,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Participar") }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { mostrarDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Apuntar tiradas") }
            }
            UserRole.ADMIN -> {
                Text(
                    "Participantes (${competicion.competidores.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (competicion.competidores.isEmpty()) {
                        item {
                            Text(
                                "Sin participantes inscritos",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    items(competicion.competidores) { participante ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = participante.correo!!, //? se pone el !! porque es seguro que va a estar registrado
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = { onEliminarParticipante(participante.correo) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Eliminar participante",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                /*
                Cambiar para cancelar solo tiradas concretas, tiene más sentido que echar por tierra tod o

                if (!competicion.cancelada) {
                    OutlinedButton(
                        onClick = { mostrarConfirmCancelar = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Cancelar competición") }
                }*/
            }
            else -> {}
        }
    }

    //Esto actualmente no hace nada, cambiar en un futuro
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            text = {
                DialogContent(
                    header = "Apuntar tirada – ${userId}",
                    numDianasFixed = competicion.sesionesCompetidas[0].tiradasCompetitivas[0].numDianas,
                    flechasFixed = competicion.sesionesCompetidas[0].tiradasCompetitivas[0].numMaxFlechasPorDiana,
                    //participantesFixed = competicion.competidores,
                    participantesFixed = emptyList(),
                    onConfirm = { d, f, p ->
                        onApuntarTirada(d, f, p)
                        mostrarDialog = false
                    },
                    onDismiss = { mostrarDialog = false }
                )
            },
            confirmButton = {}
        )
    }

    if (mostrarConfirmCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmCancelar = false },
            title = { Text("Cancelar competición") },
            text = { Text("¿Seguro que quieres cancelar la competición?") },
            confirmButton = {
                Button(
                    onClick = { onCancelarCompeticion(); mostrarConfirmCancelar = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmCancelar = false }) { Text("Volver") }
            }
        )
    }
}
*/
@Composable
internal fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}