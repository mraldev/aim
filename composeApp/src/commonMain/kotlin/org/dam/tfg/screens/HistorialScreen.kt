package org.dam.tfg.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.dam.tfg.model.Tirada.Tirada
import cafe.adriel.voyager.core.screen.Screen
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.dto.PuntuacionTiradaDTO
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.SesionHistorial

class HistorialScreen(val sesiones: List<SesionHistorial> = emptyList()) : Screen {
    @Composable
    override fun Content() {
        /*
        Borrar más adelante
        val sesionesEjemplo = listOf(
            // -------- SESIÓN 1 --------
            SesionHistorial(
                fecha = LocalDate(2026, 4, 20),
                tiradas = listOf(

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero1@email.com"),
                        numDianas = 3,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(10, 10, 11)), // perfecta
                            PuntuacionTiradaDTO(mutableListOf(8, 5, 0)),
                            PuntuacionTiradaDTO(mutableListOf(10, 11, 10))  // perfecta
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    ),

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero2@email.com"),
                        numDianas = 2,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(5, 8, 10)),
                            PuntuacionTiradaDTO(mutableListOf(0, 5, 8))
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    )
                )
            ),

            // -------- SESIÓN 2 --------
            SesionHistorial(
                fecha = LocalDate(2026, 4, 22),
                tiradas = listOf(

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero1@email.com"),
                        numDianas = 4,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(10, 11, 10)), // perfecta
                            PuntuacionTiradaDTO(mutableListOf(10, 10, 10)), // perfecta
                            PuntuacionTiradaDTO(mutableListOf(8, 8, 5)),
                            PuntuacionTiradaDTO(mutableListOf(0, 5, 5))
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    ),

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero3@email.com"),
                        numDianas = 3,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(0, 0, 5)),
                            PuntuacionTiradaDTO(mutableListOf(8, 10, 11)),
                            PuntuacionTiradaDTO(mutableListOf(null, null, null)) // incompleta
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    )
                )
            )
        )
        */
        HistorialContent(sesiones)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistorialContent(sesiones: List<SesionHistorial>) {

    var busqCompeticion by remember { mutableStateOf("") }
    var fechaSelec by remember { mutableStateOf<LocalDate?>(null) }
    var puntajeMin by remember { mutableStateOf("") }
    var mostrarFecha by remember { mutableStateOf(false) }

    val listaFiltrada = sesiones.filter { sesion ->
        (busqCompeticion.isBlank() ||
                sesion.tiradas.any { tirada ->
                    tirada.usuario.correo?.contains(busqCompeticion, ignoreCase = true) == true
                }) &&
                (fechaSelec == null || sesion.fecha == fechaSelec) &&
                (puntajeMin.isBlank() || sesion.tiradas.any { tirada ->
                    tirada.getPuntuacionTotal() >= (puntajeMin.toIntOrNull() ?: 0)
                })
    }

    Scaffold(
        bottomBar = {
            buttonBar()
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Has realizado ${sesiones.size} ${if (sesiones.size != 1) "sesiones" else "sesión"}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = busqCompeticion,
                onValueChange = { busqCompeticion = it },
                label = { Text("Buscar por usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { mostrarFecha = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DateRange, null)
                    Spacer(Modifier.width(4.dp))
                    Text(fechaSelec?.toString() ?: "Fecha")
                }
                if (fechaSelec != null) {
                    IconButton(onClick = { fechaSelec = null }) {
                        Icon(Icons.Default.Clear, "Quitar fecha")
                    }
                }

                OutlinedTextField(
                    value = puntajeMin,
                    onValueChange = { if (it.all { c -> c.isDigit() }) puntajeMin = it },
                    label = { Text("Punt. mínima") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            if (sesiones.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Aún no tienes sesiones registradas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tus sesiones aparecerán aquí una vez las registres.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaFiltrada) { sesion ->
                        SesionCard(sesion = sesion)
                    }
                    if (listaFiltrada.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay sesiones con estos filtros")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFecha) {
        DatePickerDialogWrapper(
            onDateSelected = { fechaSelec = it; mostrarFecha = false },
            onDismiss = { mostrarFecha = false }
        )
    }
}

@Composable
fun SesionCard(sesion: SesionHistorial) {
    val corner = RoundedCornerShape(24.dp)
    val tipoCircuito = sesion.tiradas.firstOrNull()?.tipoCircuito

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        shape = corner,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Event, contentDescription = null)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sesion.fecha.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${sesion.tiradas.size} tiradas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val dianasTotales = sesion.tiradas.firstOrNull()?.numDianas ?: 0

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.TrackChanges, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("$dianasTotales dianas")
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            )

            tipoCircuito?.let {
                AssistChip(
                    onClick = {
                        //? podría estar bien sacar un toast que te diga "Tipo de tirada = x"
                    },
                    label = {
                        Text(it.name.replace("_", " "))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sesion.tiradas.forEach { tirada ->
                    TiradaCard(tirada = tirada)
                }
            }
        }
    }
}

@Composable
private fun TiradaCard(tirada: Tirada) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = "Puntuación",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${tirada.getPuntuacionTotal()} pts",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        tirada.usuario.correo?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogWrapper(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(LocalDate(2024, 1, 1))
            }) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) { DatePicker(state = state) }
}