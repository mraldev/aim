package org.dam.tfg.screens.Historial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            isLoading = true
            try {
                sesiones = tiradaRepository.historialTiradasNormales()
            } catch (e: ExceptionNoRegistrado) {
                showDialog = true
            } finally {
                isLoading = false
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (sesiones.isEmpty()) {
                Text(
                    text = "No hay tiradas",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                HistorialContent(sesiones)
            }
        }
    }
}