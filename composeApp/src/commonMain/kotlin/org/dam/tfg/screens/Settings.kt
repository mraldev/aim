package org.dam.tfg.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar
import org.dam.tfg.model.Users

class Settings(): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp).fillMaxSize()) {
            profileBar(navigator)
            Spacer(modifier = Modifier.height(16.dp))

                //TODO : AÑADIR FUNCIONALIDAD DE LOS DIFERENTES BOTONES
                Row{
                    Button(onClick = { navigator.push(Login()) }) { Text("Cambiar nombre de usuario") }
                }
                Row{
                    Button(onClick = { navigator.push(Login()) }) { Text("Cambiar Contraseña") }
                }
                Row{
                    Button(onClick = { navigator.push(Login()) }) { Text("Cerrar Sesión") }
                }
            }

            Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator, usuario)
            }
        }
    }
}
