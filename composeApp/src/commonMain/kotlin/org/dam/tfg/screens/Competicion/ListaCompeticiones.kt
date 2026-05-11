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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.enums.UserRole
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.model.competiciones.Liga

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListaCompeticiones(
    userRole: UserRole?,
    asociacionesUsuario: Map<Asociacion, Int>, //? el número de federado de las distintas asociaciones
    competiciones: List<Liga>,
    onSelect: (Liga) -> Unit,
    onAñadir: () -> Unit
) {
    val nav = LocalNavigator.currentOrThrow

    var busquedaPorNombre by remember { mutableStateOf("") }
    var asocExpanded by remember { mutableStateOf(false) }
    var asocSelec by remember { mutableStateOf<Asociacion?>(null) }
    var circuitoExpanded by remember { mutableStateOf(false) }
    var circuitoSelec by remember { mutableStateOf<TipoCircuito?>(null) }
    var fechaSelec by remember { mutableStateOf<LocalDate?>(null) }
    var soloPropias by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val listaBase = if (userRole == UserRole.ADMIN || userRole == UserRole.SUPER_ADMIN)
        competiciones.filter { asociacionesUsuario.containsValue(it.administradorId) }
    else
        competiciones

    val listaFiltrada = listaBase.filter { comp ->
        (busquedaPorNombre.isBlank() || comp.nombreLiga.contains(busquedaPorNombre, ignoreCase = true)) &&
                (asocSelec == null || comp.sesionesCompetidas[0].tiradasCompetitivas[0].asociacion == asocSelec) &&
                //Se filtra por el índice 0 ya que es el mismo para todos, además siempre va a haber al menos 1
                (circuitoSelec == null || comp.sesionesCompetidas[0].tiradasCompetitivas[0].tipoCircuito == circuitoSelec) &&
                (fechaSelec == null || comp.fecha == fechaSelec) &&
                (!soloPropias || asociacionesUsuario.values.any {
                    it == comp.sesionesCompetidas[0].tiradasCompetitivas[0].usuario.numFederado
                })
    }

    Scaffold(
        floatingActionButton = {
            if (userRole == UserRole.ADMIN || userRole == UserRole.SUPER_ADMIN) {
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
                value = busquedaPorNombre,
                onValueChange = { busquedaPorNombre = it },
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
internal fun CompeticionCard(comp: Liga, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            comp.sesionesCompetidas[0].tiradasCompetitivas[0].usuario.correo?.let {
                Text(it, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${comp.sesionesCompetidas[0].tiradasCompetitivas[0].asociacion.label}  •  ${comp.sesionesCompetidas[0].tiradasCompetitivas[0].tipoCircuito.label}  •  ${comp.fecha}",
                style = MaterialTheme.typography.bodySmall
            )
            /*
            esto habría que sacar un tiradaCompetitivaCard y meter más info ahí
            tomar como ejemplo lo que se hace en historial para tiradas y sesiones

            if (comp.cancelada) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "CANCELADA",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }*/
        }
    }
}