package org.dam.tfg.screens.Historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.dam.tfg.model.Tirada.SesionHistorial

@Composable
fun SesionDetalleDialog(sesion: SesionHistorial, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
        title = {
            Text(
                text = "Sesión del ${sesion.fecha}",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sesion.tiradas) { tirada ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = tirada.usuario.correo ?: "Usuario desconocido",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(2.dp))

                        Text("Dianas: ${tirada.numDianas}")
                        Text("Flechas por diana: ${tirada.numMaxFlechasPorDiana}")
                        Text("Puntuación total: ${tirada.getPuntuacionTotal()} pts")

                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(4.dp))

                        tirada.puntuaciones.forEachIndexed { indexDiana, puntuacion ->
                            Text(
                                text = "Diana ${indexDiana + 1}:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                puntuacion.valores.forEach { pts ->
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = pts?.toString() ?: "-",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}