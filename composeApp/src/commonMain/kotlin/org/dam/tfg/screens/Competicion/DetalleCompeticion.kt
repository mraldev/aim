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
import org.dam.tfg.customElements.DialogContent
import org.dam.tfg.model.Competicion
import org.dam.tfg.enums.UserRole

@Composable
internal fun DetalleCompeticion(
    competicion: Competicion,
    userRole: UserRole,
    userId: String,
    onBack: () -> Unit,
    onParticipar: () -> Unit,
    onDejarParticipar: () -> Unit,
    onEditar: () -> Unit,
    onCancelarCompeticion: () -> Unit,
    onEliminarParticipante: (String) -> Unit,
    onApuntarTirada: (Int, Int, List<String>) -> Unit
) {
    val esParticipante = userId in competicion.participantes
    var mostrarDialog by remember { mutableStateOf(false) }
    var mostrarConfirmCancelar by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Volver")
            }
            Text(competicion.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            if (userRole == UserRole.ADMIN) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        InfoRow(label = "Asociación",    value = competicion.asociacion.label)
        InfoRow(label = "Tipo circuito", value = competicion.tipoCircuito.label)
        InfoRow(label = "Fecha",         value = competicion.fecha.toString())
        InfoRow(label = "Dianas",        value = competicion.numDianas.toString())
        InfoRow(label = "Flechas / diana", value = competicion.numFlechas.toString())

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
                    "Participantes (${competicion.participantes.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (competicion.participantes.isEmpty()) {
                        item {
                            Text(
                                "Sin participantes inscritos",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    items(competicion.participantes) { participante ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = participante,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = { onEliminarParticipante(participante) }) {
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

                if (!competicion.cancelada) {
                    OutlinedButton(
                        onClick = { mostrarConfirmCancelar = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Cancelar competición") }
                }
            }
            else -> {}
        }
    }

    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            text = {
                DialogContent(
                    header = "Apuntar tirada – ${competicion.nombre}",
                    numDianasFixed = competicion.numDianas,
                    flechasFixed = competicion.numFlechas,
                    participantesFixed = competicion.participantes,
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