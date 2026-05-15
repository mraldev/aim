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
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.enums.UserRole
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.dto.DatosCompeticionDto
import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.Estilo
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.Posicion
import org.dam.tfg.enums.RangoDeEdad
import org.dam.tfg.helpers.HelperCargadorDeTipoDeTirada
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.screens.Historial.DatePicker
import org.dam.tfg.screens.TiradaScreen
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ListaCompeticiones(
    userRole: UserRole?,
    asociacionesUsuario: Map<Asociacion, Int>, //? el número de federado de las distintas asociaciones
    competiciones: List<LigaPreview>,
    onSelect: (LigaPreview) -> Unit,
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
    var showDialog by remember { mutableStateOf(false) }
    var competicion by remember { mutableStateOf<LigaPreview?>(null) }

    val listaBase = if (userRole == UserRole.ADMIN)
        competiciones.filter { asociacionesUsuario.containsValue(it.administradorId) }
    else
        competiciones

    val listaFiltrada = listaBase.filter { comp ->
        (busquedaPorNombre.isBlank() || comp.nombreLiga.contains(busquedaPorNombre, ignoreCase = true)) &&
                (asocSelec == null || comp.asociacion == asocSelec) &&
                //Se filtra por el índice 0 ya que es el mismo para todos, además siempre va a haber al menos 1
                (circuitoSelec == null || comp.tipoCircuito == circuitoSelec) &&
                (fechaSelec == null || comp.fecha == fechaSelec) &&
                (!soloPropias || comp.competidores.any { competidor ->
                    competidor.numFederado in asociacionesUsuario.values
                })
    }

    if (showDialog) {
        DialogBase(
            data = mapOf(
                "header" to "Competir",
                "content" to "Al confirmar este mensaje, estarás participando en la competición ${competicion!!.nombreLiga}.",
                "confirmButton" to "Confirmar",
                "dismissButton" to "Cancelar"
            ),
            onConfirm = {
                SesionManager.setDatosLiga(
                    DatosCompeticionDto(
                        competicion!!.nombreLiga,
                        1,
                        Posicion.A,
                        1,
                        Estilo.BOWHUNTER_COMPOUND,
                        RangoDeEdad.ADULTO,
                        Genero.MASCULINO
                    )
                )

                val infoTipoCircuito = HelperCargadorDeTipoDeTirada.cargarDatos(
                    competicion!!.tipoCircuito
                )

                val tiradas = listOf<Tirada>(
                    Tirada(
                        //? como ya es costumbre, se asegura (!!) porque para acceder aquí hay que estar logeado
                        usuario = UsuarioTiradaDTO(UserManager.correo.value!!),
                        numDianas = infoTipoCircuito.numDianas,
                        numMaxFlechasPorDiana = infoTipoCircuito.numFlechasPorDiana,
                        puntuaciones = mutableListOf(),
                        tipoCircuito = competicion!!.tipoCircuito,
                        null
                    )
                )

                val sesion = SesionEnviar(tiradas)

                SesionManager.setSesion(sesion)
                nav.pop()
                nav.push(TiradaScreen())
            },
            onDismiss = { showDialog = false }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
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
                    CompeticionCard(comp = comp, onClick = {
                        //onSelect(comp)

                        competicion = comp

                        showDialog = true
                    })
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
        DatePicker(
            onDateSelected = { fechaSelec = it; mostrarFecha = false },
            onDismiss = { mostrarFecha = false }
        )
    }
}

@Composable
internal fun CompeticionCard(comp: LigaPreview, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(comp.nombreLiga, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            Text(
                "${comp.asociacion.label}  •  ${comp.tipoCircuito.label}  •  ${comp.fecha}",
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