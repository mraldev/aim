package org.dam.tfg.screens.competicion

import androidx.compose.foundation.clickable
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
import kotlinx.datetime.LocalDate
import org.dam.tfg.model.Competicion
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.enums.UserRole
import org.dam.tfg.customElements.buttonBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListaCompeticiones(
    userRole: UserRole,
    userId: String,
    competiciones: List<Competicion>,
    onSelect: (Competicion) -> Unit,
    onAñadir: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var asocExpanded by remember { mutableStateOf(false) }
    var asocSelec by remember { mutableStateOf<Asociacion?>(null) }
    var circuitoExpanded by remember { mutableStateOf(false) }
    var circuitoSelec by remember { mutableStateOf<TipoCircuito?>(null) }
    var fechaSelec by remember { mutableStateOf<LocalDate?>(null) }
    var soloPropias by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val listaBase = if (userRole == UserRole.ADMIN)
        competiciones.filter { it.administradorId == userId }
    else
        competiciones

    val listaFiltrada = listaBase.filter { comp ->
        (busqueda.isBlank() || comp.nombre.contains(busqueda, ignoreCase = true)) &&
                (asocSelec == null || comp.asociacion == asocSelec) &&
                (circuitoSelec == null || comp.tipoCircuito == circuitoSelec) &&
                (fechaSelec == null || comp.fecha == fechaSelec) &&
                (!soloPropias || userId in comp.participantes)
    }

    Scaffold(
        floatingActionButton = {
            if (userRole == UserRole.ADMIN) {
                FloatingActionButton(onClick = onAñadir) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir competición")
                }
            }
        },
        bottomBar = { buttonBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar competición") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Fila 1: Asociación + Circuito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = asocExpanded,
                    onExpandedChange = { asocExpanded = !asocExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = asocSelec?.label ?: "Asociación",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(asocExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = asocExpanded,
                        onDismissRequest = { asocExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = { asocSelec = null; asocExpanded = false }
                        )
                        Asociacion.entries.forEach { asoc ->
                            DropdownMenuItem(
                                text = { Text(asoc.label) },
                                onClick = { asocSelec = asoc; asocExpanded = false }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = circuitoExpanded,
                    onExpandedChange = { circuitoExpanded = !circuitoExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = circuitoSelec?.label ?: "Circuito",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(circuitoExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = circuitoExpanded,
                        onDismissRequest = { circuitoExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = { circuitoSelec = null; circuitoExpanded = false }
                        )
                        TipoCircuito.entries.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.label) },
                                onClick = { circuitoSelec = tipo; circuitoExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Fila 2: Fecha
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
            }

            if (userRole == UserRole.FEDERADO) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { soloPropias = !soloPropias }
                ) {
                    Checkbox(checked = soloPropias, onCheckedChange = { soloPropias = it })
                    Text("Mostrar solo en las que participo")
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaFiltrada) { comp ->
                    CompeticionCard(comp = comp, onClick = { onSelect(comp) })
                }
                if (listaFiltrada.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("No se encontraron competiciones") }
                    }
                }
            }
        }
    }

    if (mostrarFecha) {
        CompeticionDatePicker(
            onDateSelected = { fechaSelec = it; mostrarFecha = false },
            onDismiss = { mostrarFecha = false }
        )
    }
}

@Composable
internal fun CompeticionCard(comp: Competicion, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(comp.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "${comp.asociacion.label}  •  ${comp.tipoCircuito.label}  •  ${comp.fecha}",
                style = MaterialTheme.typography.bodySmall
            )
            if (comp.cancelada) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "CANCELADA",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}