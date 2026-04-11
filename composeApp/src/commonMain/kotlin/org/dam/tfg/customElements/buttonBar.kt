package org.dam.tfg.customElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.Tirada.TiradaDialog
import org.dam.tfg.dto.PuntuacionTiradaDTO
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.screens.Login
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.screens.TiradaScreen

//Para utilizar TIENE que estar dentro de un row dentro de la pantalla al final.
@Composable
fun buttonBar(modifier : Modifier = Modifier
    .fillMaxWidth().size(65.dp)
    .background(Color(255,255,255)),
              navigator: Navigator = LocalNavigator.currentOrThrow,
              ) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        TiradaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { numDianas, flechas ->
                showDialog = false
                val tirada = Tirada(
                    usuario = UsuarioTiradaDTO(UserManager.correo.value),
                    numDianas = numDianas,
                    numMaxFlechasPorDiana = flechas,
                    puntuaciones = mutableListOf(),
                    tipoCircuito = TipoCircuito.CUSTOM
                )
                navigator.push(TiradaScreen(tirada))
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
                //? De momento todos llevan al login (si se quiere agregar uno mas, se agrega
            //TODO : TERMINAR LAS DIFERENTES PANTALLAS _Y_ QUE SE PUEDA PASAR EL USUARIO ACTUAL A ELLAS
                Button(onClick = { navigator.push(Login()) }) { Text("Btn1") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn2") }
            //- Agreagar logica para que vea tiradaManager, si este tiene valor, que pregunte si quieres continuar la tirada que hay cacheada, al negarse, que vuelva al popup de nueva tirada, sino, procedimiento normal, nueva tirada
            AnimatedButton( text = "+", onClick = { showDialog = true} )
                Button(onClick = { navigator.push(Login()) }) { Text("Btn3") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn4") }
        }
    }