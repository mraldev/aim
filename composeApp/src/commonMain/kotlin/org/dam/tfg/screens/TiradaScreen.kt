package org.dam.tfg.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.dam.tfg.api.managers.TiradaManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.Tirada.FlechasSection
import org.dam.tfg.customElements.Tirada.NavigationButtons
import org.dam.tfg.customElements.Tirada.TiradaStatsSection
import org.dam.tfg.customElements.Tirada.TiradaTopBar
import org.dam.tfg.dto.PuntuacionTiradaDTO
import org.dam.tfg.model.Tirada.StatsDiana
import org.dam.tfg.model.Tirada.StatsTotal
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.model.Tirada.calcularStatsDiana
import org.dam.tfg.model.Tirada.calcularStatsTotal
import org.dam.tfg.repository.HealthCheckRepository
import org.dam.tfg.repository.TiradaRepository

/**
 * Pantalla principal de una tirada.
 *
 * Estado mantenido en memoria mientras el composable esté vivo.
 * Cada vez que cambia de diana, los datos de las demás dianas se conservan
 * gracias a [puntuaciones] que es una SnapshotStateList anidada.
 *
 * @param tirada        Datos de la sesión (numDianas, flechasPorDiana, etc.)
 * @param onFinalizar   Callback al terminar; recibe la matriz de puntuaciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
class TiradaScreen(
    private val tirada: Tirada,
    private val onFinalizar: (puntuaciones: List<List<Int?>>) -> Unit = {}
) : Screen {

    private val repository = TiradaRepository()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        //? Estado
        val puntuaciones = remember {
            List(tirada.numDianas) {
                List(tirada.numMaxFlechasPorDiana) { null as Int? }.toMutableStateList()
            }.toMutableStateList()
        }

        var currentDiana by remember { mutableIntStateOf(0) }
        var tiempoSegundos by remember { mutableLongStateOf(0L) }

        //? Temporizador
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000L)
                tiempoSegundos++
            }
        }

        val statsDianaActual: StatsDiana by remember {
            derivedStateOf {
                val flechas = puntuaciones.getOrNull(currentDiana)?.toList() ?: emptyList()
                calcularStatsDiana(flechas)
            }
        }

        val statsTotal: StatsTotal by remember {
            derivedStateOf {
                calcularStatsTotal(
                    puntuaciones = puntuaciones.map { it.toList() },
                    numDianas = tirada.numDianas,
                    flechasPorDiana = tirada.numMaxFlechasPorDiana
                )
            }
        }

        suspend fun finalizarTirada(puntuacionesSnapshot: List<List<Int?>>) {
            val completa = puntuacionesSnapshot.all { diana ->
                diana.size == tirada.numMaxFlechasPorDiana && diana.all { it != null }
            }

            //- Construye la tirada antes de enviarla
            val tiradaActualizada = tirada.copy(
                puntuaciones = puntuacionesSnapshot.map { flechas ->
                    PuntuacionTiradaDTO(valores = flechas.toMutableList())
                }.toMutableList()
            )

            if (completa) {
                repository.registrar(tiradaActualizada) //- Guarda la tirada en la bbdd SOLO cuando esta completa
                TiradaManager.clear()
            } else {
                TiradaManager.setTirada(tiradaActualizada)  //- Guarda la tirada incompleta
            }

            onFinalizar(puntuacionesSnapshot)
            navigator.pop()
        }
        var showConfirmDialog by remember { mutableStateOf(false) }

        if (showConfirmDialog) {
            val completa = puntuaciones.all { diana ->
                diana.size == tirada.numMaxFlechasPorDiana && diana.all { it != null }
            }

            DialogBase(
                data = mapOf(
                    "header"        to "Finalizar tirada",
                    "content"       to if (completa) "¿Seguro que quieres finalizar?" else "La tirada esta incompleta, se guardará esta tirada a no ser que hagas una nueva. ¿Seguro que quieres finalizar?",
                    "confirmButton" to "Confirmar",
                    "dismissButton" to "Cancelar"
                ),
                onConfirm = {
                    showConfirmDialog = false
                    scope.launch { finalizarTirada(puntuaciones.map { it.toList() }) }
                },
                onDismiss = { showConfirmDialog = false }
            )
        }

//? Codigo de la UI

        //- Rellena la tirada si esta tiene contenido
        LaunchedEffect(Unit) {
            tirada.puntuaciones.forEachIndexed { dianaIdx, dto ->
                dto.valores.forEachIndexed { flechaIdx, valor ->
                    if (dianaIdx < puntuaciones.size && flechaIdx < puntuaciones[dianaIdx].size) {
                        puntuaciones[dianaIdx][flechaIdx] = valor
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TiradaTopBar(
                    numDianas = tirada.numDianas,
                    currentDiana = currentDiana,
                    tiempoSegundos = tiempoSegundos,
                    puntuacionesPorDiana = puntuaciones.map { diana -> diana.toList() },
                    onDianaSelected = { index -> currentDiana = index },
                    onFinalizar = { showConfirmDialog = true }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Diana ${currentDiana + 1} de ${tirada.numDianas}",
                    style = MaterialTheme.typography.titleMedium
                )

                FlechasSection(
                    modifier = Modifier.weight(1f),
                    numFlechas = tirada.numMaxFlechasPorDiana,
                    puntuaciones = puntuaciones.getOrNull(currentDiana) ?: emptyList(),
                    onPuntuacionChanged = { index, puntuacion ->
                        puntuaciones[currentDiana][index] = puntuacion
                    }
                )

                HorizontalDivider()

                TiradaStatsSection(
                    statsDiana = statsDianaActual,
                    statsTotal = statsTotal
                )

                Spacer(modifier = Modifier.height(4.dp))

                NavigationButtons(
                    currentDiana = currentDiana,
                    totalDianas = tirada.numDianas,
                    onPrev = { if (currentDiana > 0) currentDiana-- },
                    onNext = { if (currentDiana < tirada.numDianas - 1) currentDiana++ },
                    onFinalizar = {
                        showConfirmDialog = true
                    }
                )
            }
        }
    }
}