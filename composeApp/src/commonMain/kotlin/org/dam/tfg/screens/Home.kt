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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.authorization.UserManager
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar
import org.dam.tfg.model.Users

class Home(): Screen {
    //? En esta ventana no vamos a usar isAdmin, pero de aqui se la podemos pasar a todas las demas ventanas
    //? lo que significa que podemos saber cuando un usuario es admin en cualquier punto de la app

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp).fillMaxSize()){
                profileBar(navigator)


            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bio",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            repeat(100) {
                UserManager.descripcion.value?.let { descripcion ->
                    Text(
                        text = descripcion //? Biografia del usuario
                        //* Si hay mas texto que el de la pantalla, este se deberia de hacer scrolleable, por el verticalScroll
                    )
                }
            }
        }
            Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator, usuario)
            }
        }
    }
}