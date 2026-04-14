package org.dam.tfg

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import org.dam.tfg.screens.Home
import org.dam.tfg.screens.Login

@Composable
fun App() {
    MaterialTheme {
        Navigator(Home())
    }
}