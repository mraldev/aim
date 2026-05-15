package org.dam.tfg.screens.competicion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import kotlin.uuid.ExperimentalUuidApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.dto.FederadoTiradaDto
import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.model.competiciones.LigaEnviar
import org.dam.tfg.screens.Historial.DatePicker

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
internal fun FormularioCompeticion(
    competicion: LigaPreview?,
    onGuardar: (LigaEnviar) -> Unit,
    onCancelar: () -> Unit
) {

    var nombre by remember { mutableStateOf(competicion?.nombreLiga) }
    var fecha by remember { mutableStateOf(competicion?.fecha) }
    var asocSelec by remember { mutableStateOf<Asociacion?>(competicion?.asociacion) }
    var circuitoSelec by remember { mutableStateOf<TipoCircuito?>(competicion?.tipoCircuito) }

    var asocExpanded by remember { mutableStateOf(false) }
    var circuitoExpanded by remember { mutableStateOf(false) }
    var flExpanded by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }

    // En creación el formulario arranca ya editable; en edición hay que pulsar el lápiz.
    var editEnabled by remember { mutableStateOf(competicion == null) }

    val titulo = if (competicion == null) {
        "Nueva competición"
    } else {
        "Editar competición"
    }

    var participanteInput by remember { mutableStateOf("") }
    val participantes = remember { mutableStateListOf<Int>() }

    var participanteAEliminar by remember { mutableStateOf<Int?>(null) }

    participanteAEliminar?.let { numero ->
        DialogBase(
            data = mapOf(
                "header"        to "Eliminar competidor",
                "content"       to "¿Desea eliminar el competidor con número de federado: $numero?",
                "confirmButton" to "Aceptar",
                "dismissButton" to "Cancelar"
            ),
            onConfirm = {
                participantes.remove(numero)
                participanteAEliminar = null
            },
            onDismiss = { participanteAEliminar = null }
        )
    }

    fun anyadirParticipante() {
        val numero = participanteInput.toIntOrNull()
        if (numero != null && numero !in participantes) {
            participantes.add(numero)
        }
        participanteInput = ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
        ) {
            // Cabecera
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancelar) {
                    Icon(Icons.Default.ArrowBack, "Volver")
                }
                Text(
                    if (competicion == null) "Nueva competición" else "Editar competición",
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
                value = nombre ?: "",
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

            // Fecha
            OutlinedButton(
                onClick = { if (editEnabled) mostrarFecha = true },
                enabled = editEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(fecha?.toString() ?: "Seleccionar fecha")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = participanteInput,
                onValueChange = {
                    if (editEnabled) {
                        participanteInput = it.filter { ch -> ch.isDigit() }
                    }
                },
                label = { Text("Número de participante") },
                singleLine = true,
                enabled = editEnabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (editEnabled) anyadirParticipante() }
                ),
                trailingIcon = {
                    if (editEnabled) {
                        IconButton(onClick = { anyadirParticipante() }) {
                            Icon(Icons.Default.Add, "Añadir participante")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(participantes, key = { it }) { numero ->
                    ListItem(
                        headlineContent = { Text(numero.toString()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                participanteAEliminar = numero
                            }
                    )
                    HorizontalDivider()
                }
            }

            // Guardar
            if (editEnabled) {
                val canSave = asocSelec != null
                        && circuitoSelec != null
                        && participantes.isNotEmpty()
                        && fecha != null

                Button(
                    onClick = {
                        val fechaActual = fecha ?: return@Button
                        val asocActual = asocSelec ?: return@Button
                        val circuitoActual = circuitoSelec ?: return@Button

                        val ligaGuardar = LigaEnviar(
                            participantes.map { competidorId ->
                                FederadoTiradaDto(
                                    //? Para registrar la liga, nos sirve con no ponerle correo
                                    "",
                                    competidorId
                                )
                            },
                            asocActual,
                            circuitoActual,
                            nombre ?: "prueba",
                            fechaActual,
                            UserManager.asociaciones.value.getValue(asocActual)
                            //! Como solo se puede acceder a esta pantalla siendo admin, se pre supone que haya valor en numFederado
                        )

                        onGuardar(
                            ligaGuardar
                        )
                    },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar") }
            }
        }
    }

    if (mostrarFecha) {
        DatePicker(
            onDateSelected = { date ->
                fecha = date
                mostrarFecha = false
            },
            onDismiss = {
                mostrarFecha = false
            }
        )
    }
}