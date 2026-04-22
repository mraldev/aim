package org.dam.tfg.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.dam.tfg.model.Tirada.Tirada
import cafe.adriel.voyager.core.screen.Screen
import org.dam.tfg.customElements.buttonBar

class HistorialScreen(val tiradas: List<Tirada> = emptyList()) : Screen {
    @Composable
    override fun Content() {
        HistorialContent(tiradas)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistorialContent(tiradas: List<Tirada>) {

    var busqCompeticion by remember { mutableStateOf("") }
    var fechaSelec      by remember { mutableStateOf<LocalDate?>(null) }
    var puntajeMin      by remember { mutableStateOf("") }
    var mostrarFecha    by remember { mutableStateOf(false) }

    val listaFiltrada = tiradas.filter { t ->
        (busqCompeticion.isBlank() ||
                t.usuario.correo?.contains(busqCompeticion, ignoreCase = true) == true) &&
                (fechaSelec == null || t.fecha == fechaSelec) &&
                (puntajeMin.isBlank() || t.getPuntuacionTotal() >= (puntajeMin.toIntOrNull() ?: 0))
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
                text  = "Has realizado ${tiradas.size} tirada${if (tiradas.size != 1) "s" else ""}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value         = busqCompeticion,
                onValueChange = { busqCompeticion = it },
                label         = { Text("Buscar por usuario") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick  = { mostrarFecha = true },
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
                    value         = puntajeMin,
                    onValueChange = { if (it.all { c -> c.isDigit() }) puntajeMin = it },
                    label         = { Text("Punt. mínima") },
                    singleLine    = true,
                    modifier      = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            if (tiradas.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Aún no tienes tiradas registradas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tus tiradas aparecerán aquí una vez las registres.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaFiltrada) { tirada ->
                        TiradaCard(tirada = tirada)
                    }
                    if (listaFiltrada.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay tiradas con estos filtros")
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
            onDismiss      = { mostrarFecha = false }
        )
    }
}

@Composable
private fun TiradaCard(tirada: Tirada) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(tirada.fecha.toString(), style = MaterialTheme.typography.labelLarge)
                Text("Total: ${tirada.getPuntuacionTotal()} pts",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
            }

            tirada.usuario.correo?.let { correo ->
                Spacer(Modifier.height(4.dp))
                Text(correo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            Text("Dianas: ${tirada.numDianas} | Flechas/diana: ${tirada.numMaxFlechasPorDiana}")
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