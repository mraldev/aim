package org.dam.tfg.customElements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogContent(
    header: String,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var numDianas by remember { mutableStateOf("") }
    var flechas   by remember { mutableStateOf(1) }
    var expanded  by remember { mutableStateOf(false) }

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
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        //? Spinner de flechas (1–4)
        ExposedDropdownMenuBox(
            expanded        = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value        = flechas.toString(),
                onValueChange = {},
                readOnly     = true,
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

        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick  = { if (numDianas.isNotEmpty()) onConfirm(numDianas.toInt(), flechas) },
                enabled  = numDianas.isNotEmpty()
            ) {
                Text("Aceptar")
            }
        }
    }
}