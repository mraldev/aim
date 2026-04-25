package org.dam.tfg.screens.competicion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.competiciones.Liga
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
internal fun FormularioCompeticion(
    competicion: Liga?,
    onGuardar: (Liga) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember {
        mutableStateOf(
            competicion?.sesionesCompetidas[0]!!.tiradasCompetitivas[0].usuario.correo!!
            //? se pone !! en sesiones porque no existe la posibilidad de una liga sin sesiones
            //? se pone !! en correo porque un usuario federado siempre va a tener nombre (recordar cambiar de correo a nombre)
        )
    }
    var numDianas by remember {
        mutableStateOf(
            competicion?.sesionesCompetidas[0]!!.tiradasCompetitivas[0].numDianas.toString()
        )
    }
    var numFlechas by remember {
        mutableStateOf(
            competicion?.sesionesCompetidas[0]!!.tiradasCompetitivas[0].numMaxFlechasPorDiana
        )
    }
    var fecha by remember { mutableStateOf(competicion?.fecha) }
    var asocSelec by remember { mutableStateOf(competicion?.sesionesCompetidas[0]!!.tiradasCompetitivas[0].asociacion) }
    var circuitoSelec by remember { mutableStateOf(competicion?.sesionesCompetidas[0]!!.tiradasCompetitivas[0].tipoCircuito) }
    var asocExpanded by remember { mutableStateOf(false) }
    var circuitoExpanded by remember { mutableStateOf(false) }
    var flExpanded by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }
    var editEnabled by remember { mutableStateOf(competicion == null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Cabecera
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancelar) {
                Icon(Icons.Default.ArrowBack, "Volver")
            }
            Text(
                if (competicion == null) "Nueva competición" else competicion.sesionesCompetidas[0].tiradasCompetitivas[0].usuario.correo!!,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.weight(1f))
            if (competicion != null && !editEnabled) {
                IconButton(onClick = { editEnabled = true }) {
                    Icon(Icons.Default.Edit, "Editar")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { if (editEnabled) nombre = it },
            label = { Text("Nombre de la competición") },
            singleLine = true,
            readOnly = !editEnabled,
            enabled = editEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Asociación
        ExposedDropdownMenuBox(
            expanded = if (editEnabled) asocExpanded else false,
            onExpandedChange = { if (editEnabled) asocExpanded = !asocExpanded }
        ) {
            OutlinedTextField(
                value = asocSelec?.label ?: "Seleccionar asociación",
                onValueChange = {},
                readOnly = true,
                enabled = editEnabled,
                label = { Text("Asociación") },
                trailingIcon = {
                    if (editEnabled) ExposedDropdownMenuDefaults.TrailingIcon(asocExpanded)
                },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = asocExpanded,
                onDismissRequest = { asocExpanded = false }
            ) {
                Asociacion.entries.forEach { asoc ->
                    DropdownMenuItem(
                        text = { Text(asoc.label) },
                        onClick = { asocSelec = asoc; asocExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tipo de circuito
        ExposedDropdownMenuBox(
            expanded = if (editEnabled) circuitoExpanded else false,
            onExpandedChange = { if (editEnabled) circuitoExpanded = !circuitoExpanded }
        ) {
            OutlinedTextField(
                value = circuitoSelec?.label ?: "Seleccionar tipo de circuito",
                onValueChange = {},
                readOnly = true,
                enabled = editEnabled,
                label = { Text("Tipo de circuito") },
                trailingIcon = {
                    if (editEnabled) ExposedDropdownMenuDefaults.TrailingIcon(circuitoExpanded)
                },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = circuitoExpanded,
                onDismissRequest = { circuitoExpanded = false }
            ) {
                TipoCircuito.entries.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.label) },
                        onClick = { circuitoSelec = tipo; circuitoExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Número de dianas
        OutlinedTextField(
            value = numDianas,
            onValueChange = { if (editEnabled && it.all { c -> c.isDigit() }) numDianas = it },
            label = { Text("Número de dianas") },
            singleLine = true,
            readOnly = !editEnabled,
            enabled = editEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Flechas por diana
        ExposedDropdownMenuBox(
            expanded = if (editEnabled) flExpanded else false,
            onExpandedChange = { if (editEnabled) flExpanded = !flExpanded }
        ) {
            OutlinedTextField(
                value = numFlechas.toString(),
                onValueChange = {},
                readOnly = true,
                enabled = editEnabled,
                label = { Text("Flechas por diana") },
                trailingIcon = {
                    if (editEnabled) ExposedDropdownMenuDefaults.TrailingIcon(flExpanded)
                },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = flExpanded,
                onDismissRequest = { flExpanded = false }
            ) {
                (1..4).forEach { op ->
                    DropdownMenuItem(
                        text = { Text("$op") },
                        onClick = { numFlechas = op; flExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Fecha
        OutlinedButton(
            onClick = { if (editEnabled) mostrarFecha = true },
            enabled = editEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(fecha?.toString() ?: "Seleccionar fecha")
        }

        Spacer(Modifier.weight(1f))

        // Guardar
        if (editEnabled) {
            val canSave = nombre.isNotBlank()
                    && numDianas.isNotBlank()
                    && asocSelec != null
                    && circuitoSelec != null

            Button(
                onClick = {
                    /*
                    Queda pendiente de hacerse

                    val base = competicion ?: TiradaCompetitivaReal(
                        usuario = "",
                        asociacion = asocSelec!!,
                        tipoCircuito = circuitoSelec!!,
                        fecha = fecha ?: LocalDate(2024, 1, 1),
                        numMaxFlechasPorDiana = numFlechas,
                        numDianas = numDianas.toInt(),
                        administradorId = 0
                    )
                    onGuardar(
                        base.copy(
                            usuario = nombre,
                            asociacion = asocSelec,
                            tipoCircuito = circuitoSelec,
                            fecha = fecha ?: LocalDate(2024, 1, 1),
                            numMaxFlechasPorDiana = numFlechas,
                            numDianas = numDianas.toInt()
                        )
                    )*/
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }

    if (mostrarFecha) {
        CompeticionDatePicker(
            onDateSelected = { fecha = it; mostrarFecha = false },
            onDismiss = { mostrarFecha = false }
        )
    }
}