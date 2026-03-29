package org.dam.tfg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen

class Home: Screen {
    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxHeight().background(Color.Green),
            contentAlignment = Alignment.Center
        ) {
            Text("Test de segunda pantalla, si has llegado aqui, el login es correcto", fontSize = 26.sp, color = Color.White)
        }
    }
}