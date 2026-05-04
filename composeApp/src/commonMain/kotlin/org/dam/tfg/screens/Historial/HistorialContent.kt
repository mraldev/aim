package org.dam.tfg.screens.Historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.model.Tirada.SesionHistorial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialContent(sesiones: List<SesionHistorial>) {

    var busqCompeticion by remember { mutableStateOf("") }
    var fechaSelec by remember { mutableStateOf<LocalDate?>(null) }
    var puntajeMin by remember { mutableStateOf("") }
    var mostrarFecha by remember { mutableStateOf(false) }
    var ordenReciente by remember { mutableStateOf(true) }

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

    val listaOrdenada = if (ordenReciente)
        listaFiltrada.sortedWith(compareByDescending { it.fecha.toEpochDays() })
    else
        listaFiltrada.sortedWith(compareBy { it.fecha.toEpochDays() })

    Scaffold(
        bottomBar = { buttonBar() }
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

            Spacer(Modifier.height(8.dp))

            FilterChip(
                selected = ordenReciente,
                onClick = { ordenReciente = !ordenReciente },
                label = { Text(if (ordenReciente) "Más recientes primero" else "Más antiguas primero") },
                leadingIcon = {
                    Icon(Icons.Default.DateRange, null, Modifier.size(16.dp))
                }
            )

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
                    items(listaOrdenada) { sesion ->
                        SesionCard(sesion = sesion)
                    }
                    if (listaOrdenada.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
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
        DatePicker(
            onDateSelected = { fechaSelec = it; mostrarFecha = false },
            onDismiss = { mostrarFecha = false }
        )
    }
}