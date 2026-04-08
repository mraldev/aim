package org.dam.tfg.customElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.screens.Login

@Composable
fun buttonBar(modifier : Modifier = Modifier
    .fillMaxWidth().size(height = 65.dp, width = 80.dp)
    .background(Color(255,255,255)),
              navigator: Navigator = LocalNavigator.currentOrThrow) {
        Row(
            modifier = Modifier
                .fillMaxWidth().size(height = 65.dp, width = 80.dp)
                .background(Color(255, 255, 255)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
                //? De momento todos llevan al login (si se quiere agregar uno mas, se agrega
                Button(onClick = { navigator.push(Login()) }) { Text("Btn1") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn2") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn3") }
                Button(onClick = { navigator.push(Login()) }) { Text("Btn4") }
            }
}