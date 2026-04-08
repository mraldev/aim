package org.dam.tfg.customElements

import aim.composeapp.generated.resources.Res
import aim.composeapp.generated.resources.ajustes
import aim.composeapp.generated.resources.allDrawableResources
import aim.composeapp.generated.resources.allStringResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.model.Users
import org.dam.tfg.screens.Login
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getResourceUri
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun profileBar(modifier: Modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).padding(bottom = 80.dp),
               usuario : Users, navigator: Navigator = LocalNavigator.currentOrThrow) {

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
        Spacer(modifier = Modifier.width(15.dp))
        Box(modifier = Modifier.size(45.dp)){
            Button(onClick = { navigator.push(Login()) } )  {Text("I")}
    }
}
}
