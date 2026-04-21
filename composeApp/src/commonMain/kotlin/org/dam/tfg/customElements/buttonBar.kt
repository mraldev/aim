package org.dam.tfg.customElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.Tirada.TiradaDialog
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.screens.Login
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.screens.Home
import org.dam.tfg.screens.Profile
import org.dam.tfg.screens.TiradaScreen

//Para utilizar TIENE que estar dentro de un row dentro de la pantalla al final.
@Composable
fun buttonBar(
    modifier: Modifier = Modifier
        .fillMaxWidth().size(65.dp)
        .background(Color(255, 255, 255)),
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    var showContinueDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    //? Null usa los valores por defecto
    var dialogInitialDianas by remember { mutableStateOf<Int?>(null) }
    var dialogInitialFlechas by remember { mutableStateOf<Int?>(null) }

    if (showDialog) {
        TiradaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { numDianas, flechas, participantes ->
                showDialog = false

                val tiradas = participantes.map { participante ->
                    Tirada(
                        usuario = UsuarioTiradaDTO(participante),
                        numDianas = numDianas,
                        numMaxFlechasPorDiana = flechas,
                        puntuaciones = mutableListOf(),
                        tipoCircuito = TipoCircuito.CUSTOM
                    )
                }

                val tirada = Sesion(tiradas)

                SesionManager.setSesion(tirada)
                navigator.push(TiradaScreen())
            }
        )
    }

    if (showContinueDialog) {
        val saved = SesionManager.sesion.value  // snapshot, safe here
        AlertDialog(
            onDismissRequest = { showContinueDialog = false },
            title = { Text("Tirada guardada") },
            text = {
                Text(
                    "Tienes una tirada en progreso " +
                            "(${saved?.tiradas[0]?.numDianas} dianas, ${saved?.tiradas[0]?.numMaxFlechasPorDiana} flechas). " +
                            "¿Quieres continuar?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showContinueDialog = false
                    saved?.let {
                        SesionManager.setSesion(it)
                        navigator.push(TiradaScreen())
                    }
                }) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = {
                    SesionManager.clear()
                    showContinueDialog = false
                    dialogInitialDianas = null
                    dialogInitialFlechas = null
                    showDialog = true
                }) { Text("No") }
            }
        )
    }

    //? Codigo de lo visual
    Row(
        modifier = Modifier
            .fillMaxWidth().size(65.dp)
            .background(Color(255, 255, 255)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //TODO : TERMINAR LAS DIFERENTES PANTALLAS _Y_ QUE SE PUEDA PASAR EL USUARIO ACTUAL A ELLAS
        //TODO: Cambiar los textos por iconos/imágenes/emojis/etc
        Button(onClick = { navigator.push(Home()) }) { Text("Home") }
        Button(onClick = { navigator.push(Login()) }) { Text("Btn2") }
        AnimatedButton(text = "+", onClick = {
            if (SesionManager.sesion.value != null) {
                showContinueDialog = true   //? Pregunta si hay tirada cacheada
            } else {
                showDialog = true
            }
        }
        )
        Button(onClick = { navigator.push(Login()) }) { Text("Btn3") }
        Button(onClick = { navigator.push(Profile()) }) { Text("Perfil") }
    }
}