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
import androidx.compose.material3.Button

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
import org.dam.tfg.model.Users
import org.dam.tfg.screens.Settings


//Para utiliziar basta con solo llamar profileBar y pasarle los elementos necesarios (modifier no hace nada)

//*Por desgracia para poder alinearlo correctamente en la pantalla que se quiera ver, hay que colocar el
//*componente entero como se quiera dentro del box y column.
//* Por favor, utilizar lo siguiente para que siempre este igual:
//* Box(modifier = Modifier.fillMaxSize()) {
//*           Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp).fillMaxSize()){}}'
@Composable
fun profileBar(usuario : Users, navigator: Navigator = LocalNavigator.currentOrThrow) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = usuario.usuario, //? usuario (correo)
                style = MaterialTheme.typography.titleLarge
            )
            if (usuario.name != null) {
                Text(
                    text = usuario.name, //? nombre (nombre de la persona, usado en eventos)
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        //TODO : cambiar 'Button' por IconButton con la imagen ya pasada a Res.drawable.ajustes
        Spacer(modifier = Modifier.width(15.dp))
        Box(modifier = Modifier.size(45.dp)){
            Button(onClick = { navigator.push(Settings(usuario)) } )  {Text("I")}
    }
}
}
