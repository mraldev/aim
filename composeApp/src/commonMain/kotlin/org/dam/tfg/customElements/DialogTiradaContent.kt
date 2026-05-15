package org.dam.tfg.customElements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.helpers.HelperCargadorDeTipoDeTirada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogContent(
    header: String,
    numDianasFixed: Int? = null,
    flechasFixed: Int? = null,
    participantesFixed: List<String>? = null,
    onConfirm: (Int, Int, List<String>, TipoCircuito) -> Unit,
    onDismiss: () -> Unit
) {
    var numDianas by remember { mutableStateOf(numDianasFixed?.toString() ?: "") }
    var flechas by remember { mutableStateOf(flechasFixed ?: 1) }
    var expanded by remember { mutableStateOf(false) }
    var nuevoParticipante by remember { mutableStateOf("") }
    var tipoCircuito by remember { mutableStateOf(TipoCircuito.CUSTOM) }
    var expandedCircuito by remember { mutableStateOf(false)}
    val participantes = remember(participantesFixed) {
        mutableStateListOf<String>().apply {
            if (participantesFixed != null) {
                addAll(participantesFixed)
            } else {
                add(UserManager.nombre.value ?: UserManager.correo.value ?: "Usuario no registrado")
            }
        }
    }

    var participantesDuplicado by remember { mutableStateOf<String?>(null) }

    fun siguienteNombre(nombre: String): String {
        val count = participantes.count { it == nombre || it.startsWith("$nombre (") }
        return "$nombre (${count + 1})"
    }

    fun onTipoCircuitoSelected(tipo: TipoCircuito) {
        val datos = HelperCargadorDeTipoDeTirada.cargarDatos(tipo)

        flechas = datos.numFlechasPorDiana
        numDianas = datos.numDianas.toString()
    }

    //- Diálogo de duplicado
    participantesDuplicado?.let { nombre ->
        DialogBase(
            data = mapOf(
                "header"        to "Nombre duplicado",
                "content"       to "\"$nombre\" ya existe, ¿seguro que quieres agregarlo?",
                "confirmButton" to "Aceptar",
                "dismissButton" to "Cancelar"
            ),
            onConfirm = {
                participantes.add(siguienteNombre(nombre))
                nuevoParticipante = ""
                participantesDuplicado = null
            },
            onDismiss = { participantesDuplicado = null }
        )
    }

    var participanteAEliminar by remember { mutableStateOf<String?>(null) }

    //- Diálogo de confirmación de borrado
    participanteAEliminar?.let { nombre ->
        DialogBase(
            data = mapOf(
                "header"        to "Eliminar participante",
                "content"       to "¿Desea eliminar el usuario $nombre?",
                "confirmButton" to "Aceptar",
                "dismissButton" to "Cancelar"
            ),
            onConfirm = {
                participantes.remove(nombre)
                participanteAEliminar = null
            },
            onDismiss = { participanteAEliminar = null }
        )
    }

    Column(modifier = Modifier.padding(20.dp)) {

        Text(
            text = header,
            style = MaterialTheme.typography.titleLarge,
            color = AppColors.Black                                      //- Color Black para el título del dialog
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numDianas,
            onValueChange = { if (it.all { c -> c.isDigit() }) numDianas = it },
            label = { Text("Dianas") },
            singleLine = true,
            readOnly = numDianasFixed != null,
            enabled = numDianasFixed == null && tipoCircuito == TipoCircuito.CUSTOM,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColors.Lavender,              //- Color Lavender para el borde al enfocar
                unfocusedBorderColor = AppColors.Lavender,              //- Color Lavender para el borde sin enfocar
                focusedLabelColor    = AppColors.Black,                 //- Color Black para la etiqueta al enfocar
                unfocusedLabelColor  = AppColors.Black,                  //- Color Black para la etiqueta sin enfocar
                focusedContainerColor = AppColors.Eggshell
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = flechas.toString(),
                onValueChange = {},
                readOnly = true,
                enabled = flechasFixed == null && tipoCircuito == TipoCircuito.CUSTOM,
                label = { Text("Flechas por diana") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AppColors.Lavender,          //- Color Lavender para el borde al enfocar
                    unfocusedBorderColor = AppColors.Lavender,          //- Color Lavender para el borde sin enfocar
                    focusedLabelColor    = AppColors.Black,             //- Color Black para la etiqueta al enfocar
                    unfocusedLabelColor  = AppColors.Black,              //- Color Black para la etiqueta sin enfocar
                    focusedContainerColor = AppColors.Eggshell
                )
            )
            ExposedDropdownMenu(
                expanded = expanded && tipoCircuito == TipoCircuito.CUSTOM,
                onDismissRequest = { expanded = false }
            ) {
                (1..4).forEach { option ->
                    DropdownMenuItem(
                        text = { Text("$option") },
                        onClick = { flechas = option; expanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expandedCircuito,
            onExpandedChange = { expandedCircuito = !expandedCircuito }
        ) {
            OutlinedTextField(
                value = tipoCircuito.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de circuito") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCircuito) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor    = AppColors.Lavender,
                    unfocusedBorderColor  = AppColors.Lavender,
                    focusedLabelColor     = AppColors.Black,
                    unfocusedLabelColor   = AppColors.Black,
                    focusedContainerColor = AppColors.Eggshell
                )
            )
            ExposedDropdownMenu(
                expanded = expandedCircuito,
                onDismissRequest = { expandedCircuito = false }
            ) {
                TipoCircuito.entries.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.name) },
                        onClick = {
                            tipoCircuito = tipo
                            expandedCircuito = false
                            if (tipo != TipoCircuito.CUSTOM) onTipoCircuitoSelected(tipo)
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Participantes",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.Black                                      //- Color Black para el subtítulo de participantes
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically             //- Centrado vertical del botón + respecto al TextField
        ) {
            OutlinedTextField(
                value = nuevoParticipante,
                onValueChange = { nuevoParticipante = it },
                label = { Text("Nombre o correo") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AppColors.Lavender,          //- Color Lavender para el borde al enfocar
                    unfocusedBorderColor = AppColors.Lavender,          //- Color Lavender para el borde sin enfocar
                    focusedLabelColor    = AppColors.Black,             //- Color Black para la etiqueta al enfocar
                    unfocusedLabelColor  = AppColors.Black,              //- Color Black para la etiqueta sin enfocar
                    focusedContainerColor = AppColors.Eggshell
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (nuevoParticipante.isNotBlank() && participantes.size < 6) {
                        val nombre = nuevoParticipante.trim()
                        if (participantes.contains(nombre)) {
                            participantesDuplicado = nombre
                        } else {
                            participantes.add(nombre)
                            nuevoParticipante = ""
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Amethyst,               //- Color Champagne para el fondo del botón +
                    contentColor   = AppColors.Eggshell                    //- Color Black para el texto del botón +
                ),
                border = BorderStroke(2.dp, AppColors.Lavender)        //- Color Lavender para el borde del botón +
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //- Lista de participantes con fondo Amethyst y texto Eggshell
        participantes.forEach { nombre ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.Amethyst)                    //- Color Amethyst para el fondo de cada participante
                    .clickable { participanteAEliminar = nombre }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "• $nombre",
                    color = AppColors.Eggshell                         //- Color Eggshell para el texto de cada participante
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = AppColors.Black                             //- Color Black para el texto del botón Cancelar
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (numDianas.isNotEmpty()) onConfirm(numDianas.toInt(), flechas, participantes, tipoCircuito)
                },
                enabled = numDianas.isNotEmpty() && participantes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Amethyst,               //- Color Champagne para el fondo del botón Aceptar
                    contentColor   = AppColors.Eggshell                    //- Color Black para el texto del botón Aceptar
                ),
                border = BorderStroke(2.dp, AppColors.Lavender)        //- Color Lavender para el borde del botón Aceptar
            ) {
                Text("Aceptar")
            }
        }
    }
}

@Composable
fun RoundedCornerShape(x0: Dp) {
    TODO("Not yet implemented")
}