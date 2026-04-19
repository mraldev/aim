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
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.Tirada.DianaListSection
import org.dam.tfg.customElements.Tirada.FlechasSection
import org.dam.tfg.customElements.Tirada.NavigationButtons
import org.dam.tfg.customElements.Tirada.TiradaStatsSection
import org.dam.tfg.customElements.Tirada.TiradaTopBar
import org.dam.tfg.dto.PuntuacionTiradaDTO
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.model.Tirada.StatsDiana
import org.dam.tfg.model.Tirada.StatsTotal
import org.dam.tfg.model.Tirada.calcularStatsDiana
import org.dam.tfg.model.Tirada.calcularStatsTotal
import org.dam.tfg.repository.TiradaRepository

@OptIn(ExperimentalMaterial3Api::class)
class TiradaScreen(
    private val sesion: Sesion,
    private val onFinalizar: (puntuaciones: List<List<List<Int?>>>) -> Unit = {}
) : Screen {

    private val repository = TiradaRepository()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val puntuaciones = remember {
            List(sesion.tiradas.size) { //cantidad de tiradas
                List(sesion.tiradas[0].numDianas) { //dianas por tirada
                    List(sesion.tiradas[0].numMaxFlechasPorDiana)
                    { null as Int? }.toMutableStateList() //flechas por diana
                }.toMutableStateList()
            }
        }

        var currentTirada by remember { mutableIntStateOf(0) }
        var currentDiana by remember { mutableIntStateOf(0) }

        val statsDianaActual: StatsDiana by remember {
            derivedStateOf {
                calcularStatsDiana(
                    puntuaciones.getOrNull(currentTirada)
                        ?.getOrNull(currentDiana)?.toList() ?: emptyList()
                )
            }
        }

        val statsTotal: StatsTotal by remember {
            derivedStateOf {
                calcularStatsTotal(
                    puntuaciones = puntuaciones.getOrNull(currentTirada)
                        ?.map { it.toList() } ?: emptyList(),
                    numDianas = sesion.tiradas[0].numDianas,
                    flechasPorDiana = sesion.tiradas[0].numMaxFlechasPorDiana
                )
            }
        }

        //- Lógica de finalizar
        suspend fun finalizarTirada(snapshot: List<List<List<Int?>>>) {
            val completa = snapshot.all { itSesion ->
                itSesion.all { diana ->
                    diana.size == sesion.tiradas[0].numMaxFlechasPorDiana
                            && diana.all { it != null }
                }
            }

            //Recorre todas las tiradas y actualiza la actual
            val sesionActualizada = sesion.copy(
                tiradas = sesion.tiradas.mapIndexed { index, tirada ->
                    tirada.copy(
                        puntuaciones = snapshot[index].map { flechas ->
                            PuntuacionTiradaDTO(valores = flechas.toMutableList())
                        }.toMutableList()
                    )
                }.toMutableList()
            )

            if (completa) {
                repository.registrar(sesionActualizada)
                SesionManager.clear()
            } else {
                SesionManager.setSesion(sesionActualizada)
            }
            onFinalizar(snapshot)
            navigator.pop()
        }

        var showConfirmDialog by remember { mutableStateOf(false) }

        if (showConfirmDialog) {
            val completa = puntuaciones.all { itSesion ->
                itSesion.all { diana ->
                    diana.size == sesion.tiradas[0].numMaxFlechasPorDiana && diana.all { it != null }
                }
            }

            DialogBase(
                data = mapOf(
                    "header" to "Finalizar tirada",
                    "content" to if (completa)
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

        LaunchedEffect(sesion) {
            sesion.tiradas.forEachIndexed { tiradaIdx, tirada ->
                tirada.puntuaciones.forEachIndexed { dianaIdx, dto ->
                    dto.valores.forEachIndexed { flechaIdx, valor ->
                        if (tiradaIdx < puntuaciones.size &&
                            dianaIdx < puntuaciones[tiradaIdx].size &&
                            flechaIdx < puntuaciones[tiradaIdx][dianaIdx].size
                        ) {
                            puntuaciones[tiradaIdx][dianaIdx][flechaIdx] = valor
                        }
                    }
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
                    modifier = Modifier.weight(0.35f),
                    numDianas = sesion.tiradas[0].numDianas,
                    currentDiana = currentDiana,
                    puntuacionesPorDiana = puntuaciones[currentTirada].map { it.toList() },
                    onDianaSelected = { currentDiana = it }
                )

                HorizontalDivider()

                FlechasSection(
                    modifier = Modifier.weight(0.35f),
                    numFlechas = sesion.tiradas[0].numMaxFlechasPorDiana,
                    currentDiana = currentDiana,
                    puntuaciones = puntuaciones[currentTirada].getOrNull(currentDiana) ?: emptyList(),
                    onPuntuacionChanged = { index, puntuacion ->
                        puntuaciones[currentTirada][currentDiana][index] = puntuacion
                    }
                )

                HorizontalDivider()

                TiradaStatsSection(
                    statsDiana = statsDianaActual,
                    statsTotal = statsTotal,
                    modifier = Modifier.weight(0.15f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                NavigationButtons(
                    currentDiana = currentDiana,
                    totalDianas = sesion.tiradas[0].numDianas,
                    onPrev = { if (currentDiana > 0) currentDiana-- },
                    onNext = { if (currentDiana < sesion.tiradas[0].numDianas - 1) currentDiana++ },
                    onFinalizar = { showConfirmDialog = true },
                    modifier = Modifier.weight(0.15f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}