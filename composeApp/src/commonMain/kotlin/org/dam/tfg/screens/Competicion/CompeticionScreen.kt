package org.dam.tfg.screens.competicion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.dam.tfg.enums.UserRole
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.repository.LigaRepository
import org.dam.tfg.screens.Competicion.AccionesCompeticion

internal sealed class NavState {
    object Lista : NavState()
    data class Detalle(val comp: LigaPreview) : NavState()
    data class Formulario(val comp: LigaPreview?) : NavState()
}

class CompeticionScreen(
    val userRole: UserRole?,
    val asociacionesUsuario: String
) : Screen {
    @Composable
    override fun Content() {

        val ligaRepository = LigaRepository()
        var competiciones by remember {
            mutableStateOf<List<LigaPreview>>(emptyList())
        }
        var isLoading by remember {
            mutableStateOf(true)
        }

        LaunchedEffect(Unit) {
            isLoading = true
            try {
                competiciones =
                    ligaRepository.getCompeticionesByCorreo()
            } catch (e: Exception) {
                println(
                    "ERROR cargando competiciones: " +
                            "${e.message}"
                )
            } finally {
                isLoading = false
            }
        }
        var nav by remember {
            mutableStateOf<NavState>(NavState.Lista)
        }
        val userRole = UserManager.roles.value!!
        val asociacionesUsuario = UserManager.asociaciones.value
        val scope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                    Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                        buttonBar(Modifier)
                    }
                }
                else -> {
                    when (val state = nav) {

                        is NavState.Lista -> ListaCompeticiones(
                            userRole = userRole,
                            asociacionesUsuario = asociacionesUsuario,
                            competiciones = competiciones,
                            onSelect = {
                                nav = NavState.Detalle(it)
                            },
                            onAñadir = {
                                nav = NavState.Formulario(null)
                            }
                        )

                        is NavState.Detalle -> DetalleCompeticion(
                            competicion = state.comp,
                            userRole = userRole!!,
                            userId = asociacionesUsuario.toString(),
                            onBack = {
                                nav = NavState.Lista
                            },
                            onParticipar = {
                                AccionesCompeticion
                                    .onParticipar
                                    ?.invoke(state.comp)
                            },
                            onDejarParticipar = {
                                AccionesCompeticion
                                    .onDejarParticipar
                                    ?.invoke(state.comp)
                            },
                            onEditar = {
                                nav = NavState.Formulario(state.comp)
                            },
                            onCancelarCompeticion = {
                                AccionesCompeticion
                                    .onCancelarCompeticion
                                    ?.invoke(state.comp)
                            },
                            onEliminarParticipante = { pid ->
                                AccionesCompeticion
                                    .onEliminarParticipante
                                    ?.invoke(state.comp, pid)
                            },
                            onApuntarTirada = { d, f, p ->
                                AccionesCompeticion
                                    .onApuntarTirada
                                    ?.invoke(d, f, p)
                            }
                        )
                        is NavState.Formulario -> FormularioCompeticion(
                            competicion = state.comp,
                            onGuardar = { ligaEnviar ->
                                scope.launch {
                                    try {

                                        LigaRepository()
                                            .registrar(ligaEnviar)
                                        nav = NavState.Lista

                                    } catch (e: Exception) {

                                        println(
                                            "ERROR en registrar: " +
                                                    "${e.message}"
                                        )

                                        e.printStackTrace()
                                    }
                                }
                            },
                            onCancelar = {
                                nav = NavState.Lista
                            }
                        )
                    }
                }
            }
        }
    }
}