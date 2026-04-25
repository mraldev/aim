package org.dam.tfg.screens.competicion

import androidx.compose.runtime.*
import org.dam.tfg.enums.UserRole
import cafe.adriel.voyager.core.screen.Screen
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.dto.FederadoTiradaDto
import org.dam.tfg.model.competiciones.Liga

internal sealed class NavState {
    object Lista : NavState()
    data class Detalle(val comp: Liga) : NavState()
    data class Formulario(val comp: Liga?) : NavState()
}

class CompeticionScreen(
    val userRole: UserRole,
    val asociacionesUsuario: String,
    val competiciones: List<Liga>, //! Se debe buscar en la API en buttonBar
    val onParticipar: (Liga) -> Unit,
    val onDejarParticipar: (Liga) -> Unit,
    val onEliminarParticipante: (Liga, String) -> Unit,
    val onGuardarCompeticion: (Liga) -> Unit,
    val onCancelarCompeticion: (Liga) -> Unit,
    val onApuntarTirada: (Int, Int, List<String>) -> Unit
) : Screen {
    @Composable
    override fun Content() {
        var nav by remember { mutableStateOf<NavState>(NavState.Lista) }

        //val userRole = UserManager.roles.value!! //? tiene el !! porque ya se ha validado en button bar que tuviera la sesión iniciada
        val asociacionesUsuario = UserManager.asociaciones.value //? igual que la anterior

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
                userRole = userRole,
                userId = asociacionesUsuario.toString(),
                onBack = { nav = NavState.Lista },
                onParticipar = { onParticipar(state.comp) },
                onDejarParticipar = { onDejarParticipar(state.comp) },
                onEditar = { nav = NavState.Formulario(state.comp) },
                onCancelarCompeticion = { onCancelarCompeticion(state.comp) },
                onEliminarParticipante = { pid -> onEliminarParticipante(state.comp, pid) },
                onApuntarTirada = onApuntarTirada
            )
            is NavState.Formulario -> FormularioCompeticion(
                competicion = state.comp,
                onGuardar = { onGuardarCompeticion(it); nav = NavState.Lista },
                onCancelar = { nav = NavState.Lista }
            )
        }
    }
}