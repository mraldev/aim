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

//NO LA LLAMEIS - ES PARA COPIAR Y PEGAR A DIFERENTES PÁGINAS DE FORMA RÁPIDA
class pantallaVacia(private val usuario: Users): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        //RECORDAR LOS MODIFIERS - LAS PANTALLAS QUEDARAN DIFERENTES SI NO SE PONEN
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(18.dp).fillMaxSize()) {
                profileBar(usuario, navigator)
                Spacer(modifier = Modifier.height(16.dp))
            }

            //MANTENER COMO LA ULTIMA LINEA DE CODIGO, DENTRO DEL BOX FUERA DE EL PRIMER COLUMN.
                Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                    buttonBar(Modifier, navigator, usuario);
                }
        }
    }
}
