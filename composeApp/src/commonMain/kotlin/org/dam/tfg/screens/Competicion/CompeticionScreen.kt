package org.dam.tfg.screens.competicion

import androidx.compose.runtime.*
import org.dam.tfg.enums.UserRole
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.dam.tfg.api.managers.UserManager
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
        var competiciones by remember { mutableStateOf<List<LigaPreview>>(emptyList()) }

        LaunchedEffect(Unit) {
            competiciones = ligaRepository.getCompeticionesByCorreo()
        }

        var nav by remember { mutableStateOf<NavState>(NavState.Lista) }

        val userRole = UserManager.roles.value!! //? tiene el !! porque ya se ha validado en button bar que tuviera la sesión iniciada
        val asociacionesUsuario = UserManager.asociaciones.value //? igual que la anterior

        val scope = rememberCoroutineScope()

        //println(competiciones)
        //println(competiciones[0].administradorId)

        //TODO IMPORTANTE cambiar todas las referencias del correo a nombre (comprobando que esté federado primero.
        //es importante pero no corre prisa hacerlo

        when (val state = nav) {
            is NavState.Lista -> ListaCompeticiones(
                userRole = userRole,
                asociacionesUsuario = asociacionesUsuario,
                competiciones = competiciones,
                onSelect = { nav = NavState.Detalle(it) },
                onAñadir = { nav = NavState.Formulario(null) }
            )
            is NavState.Detalle -> DetalleCompeticion(
                competicion = state.comp,
                //? Si ha llegado hasta aquí, se presupone que hay valor en userRole
                userRole = userRole!!,
                userId = asociacionesUsuario.toString(),
                onBack = { nav = NavState.Lista },
                onParticipar = {
                    AccionesCompeticion.onParticipar?.invoke(state.comp)
                },
                onDejarParticipar = {
                    AccionesCompeticion.onDejarParticipar?.invoke(state.comp)
                },
                onEditar = { nav = NavState.Formulario(state.comp) },
                onCancelarCompeticion = {
                    AccionesCompeticion.onCancelarCompeticion?.invoke(state.comp)
                },
                onEliminarParticipante = { pid ->
                    AccionesCompeticion.onEliminarParticipante?.invoke(state.comp, pid)
                },
                onApuntarTirada = { d, f, p ->
                    AccionesCompeticion.onApuntarTirada?.invoke(d, f, p)
                }
            )
            is NavState.Formulario -> FormularioCompeticion(
                competicion = state.comp,
                onGuardar = { ligaEnviar ->
                    scope.launch {
                        try {
                            LigaRepository().registrar(ligaEnviar)
                            nav = NavState.Lista
                        } catch (e: Exception) {
                            println("ERROR en registrar: ${e::class.simpleName} - ${e.message}")
                            e.printStackTrace()
                        }
                    }
                },
                onCancelar = { nav = NavState.Lista }
            )

        }
    }
}