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
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar

class Profile(): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        if (UserManager.correo.value != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp).fillMaxSize()){
                    profileBar(navigator)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    UserManager.descripcion.value?.let { descripcion ->
                        Text(
                            text = descripcion
                        )
                    }
                }
                Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                    buttonBar(Modifier, navigator)
                }
            }
        } else {
            navigator.pop()
            navigator.push(Login())
        }
    }
}