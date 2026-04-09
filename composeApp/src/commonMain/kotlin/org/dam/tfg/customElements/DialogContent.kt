package org.dam.tfg.customElements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogContent(
    header: String,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var numDianas by remember { mutableStateOf("") }
    var flechas by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            text = header,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numDianas,
            onValueChange = {
                if (it.all { c -> c.isDigit() }) numDianas = it
            },
            label = { Text("Dianas") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = flechas,
            onValueChange = {
                if (it.all { c -> c.isDigit() }) flechas = it
            },
            label = { Text("Flechas por diana") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (numDianas.isNotEmpty() && flechas.isNotEmpty()) {
                    onConfirm(numDianas.toInt(), flechas.toInt())
                }
            }) {
                Text("Aceptar")
            }
        }
    }
}