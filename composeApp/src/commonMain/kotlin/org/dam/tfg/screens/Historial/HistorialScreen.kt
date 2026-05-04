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
        /*
        Borrar más adelante
        val sesionesEjemplo = listOf(
            // -------- SESIÓN 1 --------
            SesionHistorial(
                fecha = LocalDate(2026, 4, 20),
                tiradas = listOf(

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero1@email.com"),
                        numDianas = 3,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(10, 10, 11)), // perfecta
                            PuntuacionTiradaDTO(mutableListOf(8, 5, 0)),
                            PuntuacionTiradaDTO(mutableListOf(10, 11, 10))  // perfecta
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    ),

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero2@email.com"),
                        numDianas = 2,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(5, 8, 10)),
                            PuntuacionTiradaDTO(mutableListOf(0, 5, 8))
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    )
                )
            ),

            // -------- SESIÓN 2 --------
            SesionHistorial(
                fecha = LocalDate(2026, 4, 22),
                tiradas = listOf(

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero1@email.com"),
                        numDianas = 4,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(10, 11, 10)), // perfecta
                            PuntuacionTiradaDTO(mutableListOf(10, 10, 10)), // perfecta
                            PuntuacionTiradaDTO(mutableListOf(8, 8, 5)),
                            PuntuacionTiradaDTO(mutableListOf(0, 5, 5))
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    ),

                    Tirada(
                        usuario = UsuarioTiradaDTO(correo = "arquero3@email.com"),
                        numDianas = 3,
                        numMaxFlechasPorDiana = 3,
                        puntuaciones = mutableListOf(
                            PuntuacionTiradaDTO(mutableListOf(0, 0, 5)),
                            PuntuacionTiradaDTO(mutableListOf(8, 10, 11)),
                            PuntuacionTiradaDTO(mutableListOf(null, null, null)) // incompleta
                        ),
                        tipoCircuito = TipoCircuito.CUSTOM
                    )
                )
            )
        )
        */

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