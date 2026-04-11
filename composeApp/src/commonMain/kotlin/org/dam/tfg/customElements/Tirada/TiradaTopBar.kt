package org.dam.tfg.customElements.Tirada

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dam.tfg.model.Tirada.formatTiempo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

//? Top bar completa
@Composable
fun TiradaTopBar(
    numDianas: Int,
    currentDiana: Int,
    tiempoSegundos: Long,
    puntuacionesPorDiana: List<List<Int?>>,
    onDianaSelected: (Int) -> Unit,
    onFinalizar: () -> Unit
) {

//? Codigo de UI
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //? Barra scrolleable de dianas
            DianaNavBar(
                modifier = Modifier.weight(1f),
                numDianas = numDianas,
                currentDiana = currentDiana,
                puntuacionesPorDiana = puntuacionesPorDiana,
                onDianaSelected = onDianaSelected
            )

            Spacer(modifier = Modifier.width(8.dp))

            //? Tiempo
            Text(
                text = formatTiempo(tiempoSegundos),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            //? Botón Finalizar (icono flecha→caja)
            IconButton(onClick = {onFinalizar()}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Finalizar tirada",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

//? Barra de chips de dianas (scrolleable horizontal)
@Composable
fun DianaNavBar(
    numDianas: Int,
    currentDiana: Int,
    puntuacionesPorDiana: List<List<Int?>>,
    onDianaSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()

    //? Auto-scroll al chip activo
    LaunchedEffect(currentDiana) {
        val targetOffset = currentDiana * 44 //? aprox px por chip
        scrollState.animateScrollTo(targetOffset)
    }

    Row(
        modifier = modifier
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(numDianas) { index ->
            DianaChip(
                numero = index + 1,
                isSelected = index == currentDiana,
                tieneDatos = puntuacionesPorDiana.getOrNull(index)?.any { it != null } == true,
                esPerfecta = puntuacionesPorDiana.getOrNull(index)?.all { it != null && it >= 10 } == true,
                onClick = { onDianaSelected(index) }
            )
        }
    }
}

//? Chip individual de diana
@Composable
fun DianaChip(
    numero: Int,
    isSelected: Boolean,
    tieneDatos: Boolean = false,
    esPerfecta: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected  -> MaterialTheme.colorScheme.primary
        esPerfecta  -> Color(0xFFFFF59D)  // amarillo pastel
        tieneDatos  -> Color(0xFFBBDEFB)  // azul pastel
        else        -> Color.Transparent
    }
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        esPerfecta -> Color(0xFFF9A825)
        tieneDatos -> Color(0xFF1E88E5)
        else       -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$numero",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
