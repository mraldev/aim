package org.dam.tfg.customElements.Tirada

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.dam.tfg.model.Tirada.PUNTUACIONES_VALIDAS

//- Colores pastel para los botones de flecha
private val ColorFlechaVerde = Color(0xFF90EE90)
private val ColorFlechaRojo  = Color(0xFFFF9999)
private val ColorFlechaAmarillo = Color(0xFFFFFFCC)

@Composable
fun FlechasSection(
    numFlechas: Int,
    puntuaciones: List<Int?>,
    onPuntuacionChanged: (index: Int, puntuacion: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var flechaDialogIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(numFlechas) { index ->
            FlechaButton(
                numero = index + 1,
                puntuacion = puntuaciones.getOrNull(index),
                onClick = { flechaDialogIndex = index }
            )
        }
    }

    // Dialog de puntuación
    flechaDialogIndex?.let { idx ->
        PuntuacionDialog(
            flechaNumero = idx + 1,
            puntuacionActual = puntuaciones.getOrNull(idx),
            onConfirm = { nuevaPuntuacion ->
                onPuntuacionChanged(idx, nuevaPuntuacion)
                flechaDialogIndex = null
            },
            onDismiss = { flechaDialogIndex = null }
        )
    }
}

// ─── Botón individual de flecha ───────────────────────────────────────────────
@Composable
fun FlechaButton(
    numero: Int,
    puntuacion: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        puntuacion == null -> MaterialTheme.colorScheme.surfaceVariant
        puntuacion == 0    -> ColorFlechaRojo
        puntuacion >= 10   -> ColorFlechaAmarillo
        else               -> ColorFlechaVerde
    }

    val label = if (puntuacion == null) "Flecha $numero" else "Flecha $numero — $puntuacion pts"

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─── Dialog de puntuación con spinner de valores válidos ──────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuntuacionDialog(
    flechaNumero: Int,
    puntuacionActual: Int?,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var seleccion by remember { mutableStateOf(puntuacionActual ?: PUNTUACIONES_VALIDAS[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Puntuación — Flecha $flechaNumero") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selecciona la puntuación obtenida:",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Spinner (ExposedDropdownMenu)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = seleccion.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Puntuación") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PUNTUACIONES_VALIDAS.forEach { valor ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = buildString {
                                            append(valor.toString())
                                        }
                                    )
                                },
                                onClick = {
                                    seleccion = valor
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(seleccion) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}