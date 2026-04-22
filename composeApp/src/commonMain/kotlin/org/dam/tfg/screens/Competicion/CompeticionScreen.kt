package org.dam.tfg.screens.competicion

import androidx.compose.runtime.*
import org.dam.tfg.model.Competicion
import org.dam.tfg.enums.UserRole
import cafe.adriel.voyager.core.screen.Screen

internal sealed class NavState {
    object Lista : NavState()
    data class Detalle(val comp: Competicion) : NavState()
    data class Formulario(val comp: Competicion?) : NavState()
}

class CompeticionScreen(
    val userRole: UserRole,
    val userId: String,
    val competiciones: List<Competicion>,
    val onParticipar: (String) -> Unit,
    val onDejarParticipar: (String) -> Unit,
    val onEliminarParticipante: (String, String) -> Unit,
    val onGuardarCompeticion: (Competicion) -> Unit,
    val onCancelarCompeticion: (String) -> Unit,
    val onApuntarTirada: (Int, Int, List<String>) -> Unit
) : Screen {
    @Composable
    override fun Content() {
        var nav by remember { mutableStateOf<NavState>(NavState.Lista) }

        when (val state = nav) {
            is NavState.Lista -> ListaCompeticiones(
                userRole = userRole,
                userId = userId,
                competiciones = competiciones,
                onSelect = { nav = NavState.Detalle(it) },
                onAñadir = { nav = NavState.Formulario(null) }
            )
            is NavState.Detalle -> DetalleCompeticion(
                competicion = state.comp,
                userRole = userRole,
                userId = userId,
                onBack = { nav = NavState.Lista },
                onParticipar = { onParticipar(state.comp.id) },
                onDejarParticipar = { onDejarParticipar(state.comp.id) },
                onEditar = { nav = NavState.Formulario(state.comp) },
                onCancelarCompeticion = { onCancelarCompeticion(state.comp.id) },
                onEliminarParticipante = { pid -> onEliminarParticipante(state.comp.id, pid) },
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