package org.dam.tfg.screens.Historial

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.exceptions.ExceptionNoRegistrado
import org.dam.tfg.model.Tirada.SesionHistorial
import org.dam.tfg.repository.TiradaRepository

class HistorialScreen(val sesiones: List<SesionHistorial> = emptyList()) : Screen {
    @Composable
    override fun Content() {

        val tiradaRepository = TiradaRepository()
        var showDialog by remember { mutableStateOf(false) }
        val nav = LocalNavigator.currentOrThrow
        var sesiones by remember { mutableStateOf<List<SesionHistorial>>(emptyList()) }

        LaunchedEffect(Unit) {
            try {
                sesiones = tiradaRepository.historialTiradasNormales()
            } catch (e: ExceptionNoRegistrado) {
                showDialog = true
            }
        }

        if (showDialog) {
            DialogBase(
                data = mapOf(
                    "header"        to "Sin sesión activa",
                    "content"       to "Debes iniciar sesión para ver el historial.",
                    "confirmButton" to "Aceptar"
                ),
                onConfirm = { showDialog = false; nav.pop() }
            )
        }

        HistorialContent(sesiones)
    }
}