package org.dam.tfg.customElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dam.tfg.api.managers.UserManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogContent(
    header: String,
    numDianasFixed: Int? = null,
    flechasFixed: Int? = null,
    participantesFixed: List<String>? = null,
    onConfirm: (Int, Int, List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var numDianas by remember {
        mutableStateOf(numDianasFixed?.toString() ?: "")
    }
    var flechas   by remember { mutableStateOf(flechasFixed ?: 1) }
    var expanded  by remember { mutableStateOf(false) }
    var nuevoParticipante by remember { mutableStateOf("") }
    val participantes = remember(participantesFixed) {
        mutableStateListOf<String>().apply {
            if (participantesFixed != null) {
                addAll(participantesFixed)
            } else {
                add(UserManager.correo.value ?: "Usuario no registrado")
            }
        }
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            text  = header,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value         = numDianas,
            onValueChange = { if (it.all { c -> c.isDigit() }) numDianas = it },
            label         = { Text("Dianas") },
            singleLine    = true,
            readOnly     = numDianasFixed != null,
            enabled     = numDianasFixed == null,
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded        = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value        = flechas.toString(),
                onValueChange = {},
                readOnly     = true,
                enabled     = flechasFixed == null,
                label        = { Text("Flechas por diana") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier     = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded        = expanded,
                onDismissRequest = { expanded = false }
            ) {
                (1..4).forEach { option ->
                    DropdownMenuItem(
                        text    = { Text("$option") },
                        onClick = {
                            flechas = option
                            expanded  = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Participantes", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = nuevoParticipante,
                onValueChange = { nuevoParticipante = it },
                label = { Text("Nombre o correo") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (nuevoParticipante.isNotBlank()
                        && !participantes.contains(nuevoParticipante.trim())) {
                        participantes.add(nuevoParticipante.trim())
                        nuevoParticipante = ""
                    }
                }
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        participantes.forEach { nombre ->
            Text(
                text = "• $nombre",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        participantes.remove(nombre)
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick  = { if (numDianas.isNotEmpty()) onConfirm(numDianas.toInt(), flechas, participantes) },
                enabled  = numDianas.isNotEmpty() && participantes.isNotEmpty()
            ) {
                Text("Aceptar")
            }
        }
    }
}