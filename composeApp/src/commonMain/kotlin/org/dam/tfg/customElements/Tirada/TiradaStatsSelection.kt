package org.dam.tfg.customElements.Tirada

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.dam.tfg.model.Tirada.StatsDiana
import org.dam.tfg.model.Tirada.StatsTotal
import kotlin.math.round

//? Sección de estadísticas dinámicas
@Composable
fun TiradaStatsSection(
    statsDiana: StatsDiana,
    statsTotal: StatsTotal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StatRow(label = "Puntuación diana actual", value = "${statsDiana.puntuacion} pts")
            StatRow(label = "Puntuación total", value = "${statsTotal.puntuacionTotal} pts")
            StatRow(label = "Dianas perfectas", value = "${statsTotal.dianasPerfectas}")
            StatRow(
                label = "% Dianas perfectas",
                statsTotal.porcentajePerfecta.toPercent1()
            )
            StatRow(
                label = "% Aciertos",
                value = statsTotal.porcentajeAciertos.toPercent1()
            )
        }
    }
}

//? Fila de stat individual
@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

//? Botones de navegación anterior / siguiente / finalizar
@Composable
fun NavigationButtons(
    currentDiana: Int,
    totalDianas: Int,
    currentTirada: Int,
    totalTiradas: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onFinalizar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFirst = currentTirada == 0 && currentDiana == 0
    val isLast  = currentDiana == totalDianas - 1 && currentTirada == totalTiradas - 1

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //- Anterior
        OutlinedButton(
            onClick = onPrev,
            enabled = !isFirst
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Siguiente arquero"
            )
            Spacer(Modifier.width(4.dp))
            Text("Anterior")
        }

        //- Indicador de posición
        Text(
            text = "Arquero ${currentTirada + 1} / $totalTiradas",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        //- Siguiente / Finalizar
        if (isLast) {
            Button(onClick = onFinalizar) {
                Text("Finalizar")
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Finalizar"
                )
            }
        } else {
            Button(onClick = onNext) {
                Text("Siguiente")
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Siguiente arquero"
                )
            }
        }
    }
}

private fun Float.toPercent1(): String {
    val v = round(this * 10) / 10.0
    return "$v%"
}