package org.dam.tfg.customElements.Tirada

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.dam.tfg.model.Tirada.PUNTUACIONES_VALIDAS

private val ColorFlechaVerde    = Color(0xFF90EE90)
private val ColorFlechaRojo     = Color(0xFFFF9999)
private val ColorFlechaAmarillo = Color(0xFFFFFFCC)

@Composable
fun FlechasSection(
    numFlechas: Int,
    currentDiana: Int,
    puntuaciones: List<Int?>,
    onPuntuacionChanged: (index: Int, puntuacion: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var flechaSeleccionada by remember { mutableStateOf<Int?>(null) }

    //? Resetea la selección al cambiar de diana
    LaunchedEffect(currentDiana) { flechaSeleccionada = null }

    Column(modifier = modifier.fillMaxWidth()) {

        //? Lista de flechas
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(numFlechas) { index ->
                FlechaRow(
                    numero     = index + 1,
                    puntuacion = puntuaciones.getOrNull(index),
                    isSelected = index == flechaSeleccionada,
                    onClick    = { flechaSeleccionada = index }
                )
            }
        }

        HorizontalDivider()

        //? Botonera de puntuación
        PuntuacionButtons(
            enabled = flechaSeleccionada != null,
            onPuntuacionSelected = { score ->
                flechaSeleccionada?.let { idx ->
                    onPuntuacionChanged(idx, score)
                    // Auto-avance a la siguiente flecha sin puntuar
                    flechaSeleccionada = (idx + 1 until numFlechas)
                        .firstOrNull { puntuaciones.getOrNull(it) == null }
                }
            }
        )
    }
}

//? Fila individual de flecha
@Composable
private fun FlechaRow(
    numero: Int,
    puntuacion: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected         -> MaterialTheme.colorScheme.primaryContainer
        puntuacion == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        puntuacion == 0    -> ColorFlechaRojo
        puntuacion >= 10   -> ColorFlechaAmarillo
        else               -> ColorFlechaVerde
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = bgColor,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (puntuacion != null) "Flecha $numero — $puntuacion" else "Flecha $numero",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                Text(
                    text = "◀ selecciona",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

//? Botonera 3 + 2
@Composable
private fun PuntuacionButtons(
    enabled: Boolean,
    onPuntuacionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Fila 1: 0 · 5 · 8
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0, 5, 8).forEach { score ->
                PuntuacionButton(
                    score   = score,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    onClick = { onPuntuacionSelected(score) }
                )
            }
        }
        // Fila 2: 10 · 11 (estirados)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10, 11).forEach { score ->
                PuntuacionButton(
                    score   = score,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    onClick = { onPuntuacionSelected(score) }
                )
            }
        }
    }
}

@Composable
private fun PuntuacionButton(
    score: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = when {
        !enabled   -> MaterialTheme.colorScheme.surfaceVariant
        score == 0 -> ColorFlechaRojo
        score >= 10 -> ColorFlechaAmarillo
        else        -> ColorFlechaVerde
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor  = containerColor,
            contentColor    = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text  = score.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}