package org.dam.tfg.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.dam.tfg.api.managers.TiradaManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.Tirada.DianaListSection
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
import org.dam.tfg.repository.TiradaRepository

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

        val puntuaciones = remember {
            List(tirada.numDianas) {
                List(tirada.numMaxFlechasPorDiana) { null as Int? }.toMutableStateList()
            }.toMutableStateList()
        }

        var currentDiana by remember { mutableIntStateOf(0) }

        val statsDianaActual: StatsDiana by remember {
            derivedStateOf {
                calcularStatsDiana(
                    puntuaciones.getOrNull(currentDiana)?.toList() ?: emptyList()
                )
            }
        }

        val statsTotal: StatsTotal by remember {
            derivedStateOf {
                calcularStatsTotal(
                    puntuaciones   = puntuaciones.map { it.toList() },
                    numDianas      = tirada.numDianas,
                    flechasPorDiana = tirada.numMaxFlechasPorDiana
                )
            }
        }

        //- Lógica de finalizar
        suspend fun finalizarTirada(snapshot: List<List<Int?>>) {
            val completa = snapshot.all { diana ->
                diana.size == tirada.numMaxFlechasPorDiana && diana.all { it != null }
            }
            val tiradaActualizada = tirada.copy(
                puntuaciones = snapshot.map { flechas ->
                    PuntuacionTiradaDTO(valores = flechas.toMutableList())
                }.toMutableList()
            )
            if (completa) {
                repository.registrar(tiradaActualizada)
                TiradaManager.clear()
            } else {
                TiradaManager.setTirada(tiradaActualizada)
            }
            onFinalizar(snapshot)
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
                    "content"       to if (completa)
                        "¿Seguro que quieres finalizar?"
                    else
                        "La tirada está incompleta, se guardará a no ser que hagas una nueva. ¿Seguro que quieres finalizar?",
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

        LaunchedEffect(Unit) {
            tirada.puntuaciones.forEachIndexed { dianaIdx, dto ->
                dto.valores.forEachIndexed { flechaIdx, valor ->
                    if (dianaIdx < puntuaciones.size &&
                        flechaIdx < puntuaciones[dianaIdx].size
                    ) puntuaciones[dianaIdx][flechaIdx] = valor
                }
            }
        }

        //- Codigo de la UI
        Scaffold(
            topBar = {
                TiradaTopBar(onFinalizar = { showConfirmDialog = true })
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                DianaListSection(
                    numDianas            = tirada.numDianas,
                    currentDiana         = currentDiana,
                    puntuacionesPorDiana = puntuaciones.map { it.toList() },
                    onDianaSelected      = { currentDiana = it }
                )

                HorizontalDivider()

                FlechasSection(
                    modifier             = Modifier.weight(1f),
                    numFlechas           = tirada.numMaxFlechasPorDiana,
                    currentDiana         = currentDiana,
                    puntuaciones         = puntuaciones.getOrNull(currentDiana) ?: emptyList(),
                    onPuntuacionChanged  = { index, puntuacion ->
                        puntuaciones[currentDiana][index] = puntuacion
                    }
                )

                HorizontalDivider()

                TiradaStatsSection(
                    statsDiana = statsDianaActual,
                    statsTotal = statsTotal,
                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                NavigationButtons(
                    currentDiana = currentDiana,
                    totalDianas  = tirada.numDianas,
                    onPrev       = { if (currentDiana > 0) currentDiana-- },
                    onNext       = { if (currentDiana < tirada.numDianas - 1) currentDiana++ },
                    onFinalizar  = { showConfirmDialog = true },
                    modifier     = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}