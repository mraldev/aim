package org.dam.tfg.customElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.screens.Profile
import org.dam.tfg.screens.Settings


//Para utiliziar basta con solo llamar profileBar y pasarle los elementos necesarios (modifier no hace nada)

//*Por desgracia para poder alinearlo correctamente en la pantalla que se quiera ver, hay que colocar el
//*componente entero como se quiera dentro del box y column.
//* Por favor, utilizar lo siguiente para que siempre este igual:
//* Box(modifier = Modifier.fillMaxSize()) {
//*           Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp).fillMaxSize()){}}'
@Composable
fun profileBar(navigator: Navigator = LocalNavigator.currentOrThrow) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Column {
                UserManager.correo.value?.let {
                    Text(
                        text = UserManager.nombre.value ?: UserManager.correo.value.toString().split("@")[0],
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            //TODO : cambiar 'Button' por IconButton con la imagen ya pasada a Res.drawable.ajustes
            Spacer(modifier = Modifier.width(15.dp))

            Box {
                AnimatedButton(onClick = {
                    if (navigator.lastItem is Settings) {
                        navigator.push(Profile())
                    } else {
                        navigator.pop()
                        navigator.push(Settings())
                    }
                },
                    containerColor = Color.Transparent,
                    borderColor = Color.Transparent
                    ) {
                    Icon(Icons.Filled.Settings, tint = Color.Black, contentDescription = "Ajustes")
                }
            }
        }
    }
