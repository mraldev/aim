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
import org.dam.tfg.model.Tirada.StatsDiana
import org.dam.tfg.model.Tirada.StatsTotal
import org.dam.tfg.model.Tirada.calcularStatsDiana
import org.dam.tfg.model.Tirada.calcularStatsTotal
import org.dam.tfg.repository.TiradaRepository
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
class TiradaScreen : Screen {

    @Composable
    override fun Content() {
        //? Recoge el StateFlow reactivamente
        val sesionState by SesionManager.sesionEnviar.collectAsState()
        val sesionActual = sesionState ?: return  //? Guard: si no hay sesión activa no renderiza nada

        val repository = TiradaRepository()
        val onFinalizar: (puntuaciones: List<List<List<Int?>>>) -> Unit = {}
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val puntuaciones = remember {
            List(sesionActual.tiradas.size) {
                List(sesionActual.tiradas[0].numDianas) {
                    List(sesionActual.tiradas[0].numMaxFlechasPorDiana)
                    { null as Int? }.toMutableStateList()
                }.toMutableStateList()
            }
        }

        var currentTirada by remember { mutableIntStateOf(0) }
        var currentDiana  by remember { mutableIntStateOf(0) }

        val totalTiradas = sesionActual.tiradas.size
        val totalDianas  = sesionActual.tiradas[0].numDianas

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
                    puntuaciones    = puntuaciones.getOrNull(currentTirada)
                        ?.map { it.toList() } ?: emptyList(),
                    numDianas       = totalDianas,
                    flechasPorDiana = sesionActual.tiradas[0].numMaxFlechasPorDiana
                )
            }
        }

        fun guardarTirada(snapshot: List<List<List<Int?>>>) {
            val sesionActualizada = sesionActual.copy(
                tiradas = sesionActual.tiradas.mapIndexed { index, tirada ->
                    tirada.copy(
                        puntuaciones = snapshot[index].map { flechas ->
                            PuntuacionTiradaDTO(
                                valores = flechas.toMutableList()
                            )
                        }.toMutableList()
                    )
                }.toMutableList()
            )

            SesionManager.setSesion(sesionActualizada)
        }

        suspend fun finalizarTirada(snapshot: List<List<List<Int?>>>) {
            val completa = snapshot.all { itSesion ->
                itSesion.all { diana ->
                    diana.size == sesionActual.tiradas[0].numMaxFlechasPorDiana
                            && diana.all { it != null }
                }
            }

            val sesionActualizada = sesionActual.copy(
                tiradas = sesionActual.tiradas.mapIndexed { index, tirada ->
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
                onFinalizar(snapshot)
                navigator.pop()
            } else {
                //? Guarda en caché si la tirada está incompleta
                guardarTirada(snapshot)
                onFinalizar(snapshot)
                navigator.pop()
            }
        }

        var showConfirmDialog by remember { mutableStateOf(false) }

        if (showConfirmDialog) {
            val completa = puntuaciones.all { itSesion ->
                itSesion.all { diana ->
                    diana.size == sesionActual.tiradas[0].numMaxFlechasPorDiana && diana.all { it != null }
                }
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
                    navigator.pop()
                    navigator.push(Home())
                },
                onDismiss = {
                    showConfirmDialog = false
                    navigator.pop()
                    navigator.push(Home())
                }
            )
        }

        LaunchedEffect(sesionActual) {
            sesionActual.tiradas.forEachIndexed { tiradaIdx, tirada ->
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

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TiradaTopBar(
                    sesionActual.tiradas[currentTirada].usuario.correo!!,
                    onFinalizar = { showConfirmDialog = true }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                DianaListSection(
                    modifier             = Modifier.weight(0.35f),
                    numDianas            = totalDianas,
                    currentDiana         = currentDiana,
                    puntuacionesPorDiana = puntuaciones[currentTirada].map { it.toList() },
                    onDianaSelected      = { currentDiana = it }
                )

                HorizontalDivider()

                FlechasSection(
                    modifier            = Modifier.weight(0.35f),
                    numFlechas          = sesionActual.tiradas[0].numMaxFlechasPorDiana,
                    currentDiana        = currentDiana,
                    currentTirada       = currentTirada,
                    puntuaciones        = puntuaciones[currentTirada].getOrNull(currentDiana) ?: emptyList(),
                    onPuntuacionChanged = { index, puntuacion ->
                        val diana = puntuaciones.getOrNull(currentTirada)?.getOrNull(currentDiana)
                        if (diana != null && index in diana.indices) {
                            diana[index] = puntuacion
                        }
                    }
                )

                HorizontalDivider()

                TiradaStatsSection(
                    statsDiana = statsDianaActual,
                    statsTotal = statsTotal,
                    modifier   = Modifier
                        .weight(0.15f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                NavigationButtons(
                    currentDiana  = currentDiana,
                    totalDianas   = totalDianas,
                    currentTirada = currentTirada,
                    totalTiradas  = totalTiradas,
                    onPrev = {
                        //? Retrocede arquero; si es el primero, retrocede diana y va al último arquero
                        if (currentTirada > 0) {
                            currentTirada--
                        } else if (currentDiana > 0) {
                            currentDiana--
                            currentTirada = totalTiradas - 1
                        }
                    },
                    onNext = {
                        if (currentTirada < totalTiradas - 1) {
                            currentTirada++
                        } else {
                            currentTirada = 0
                            currentDiana++
                        }

                        //? Se guarda la tirada siempre después de cada paso por si el usuario saliese de la pantalla
                        guardarTirada(puntuaciones.map { it.toList() })
                    },
                    onFinalizar = { showConfirmDialog = true },
                    modifier    = Modifier
                        .weight(0.15f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}