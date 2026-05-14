package org.dam.tfg.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar

class Profile(): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var showEditBioDialog by remember { mutableStateOf(false) }
        var bioNueva by remember { mutableStateOf("") }
        var loggedIn by remember { mutableStateOf<Boolean?>(null) }

        LaunchedEffect(Unit) {
            loggedIn = TokenManager.isLoggedIn()
//            loggedIn = true //- DEV
        }

        if (showEditBioDialog) {
            AlertDialog(
                onDismissRequest = { showEditBioDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            UserManager.setDescripcion(bioNueva)
                            showEditBioDialog = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEditBioDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Cambiar Biografia") },
                text = {
                    OutlinedTextField(
                        value = bioNueva,
                        onValueChange = { bioNueva = it },
                        label = { Text("Biografia") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
            )
        }

        when (loggedIn) {
            null -> {
                // estado de carga
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Comprobando sesión...")
                }
            }

            true -> {
                // usuario logueado
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(18.dp)
                            .fillMaxSize()
                    ) {
                        profileBar(navigator)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Bio", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        UserManager.descripcion.value?.let {
                            Text(text = it)
                        }

                        Row {
                            TextButton(onClick = { showEditBioDialog = true }) {
                                Text("Editar Bio")
                            }
                        }
                    }

                    Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                        buttonBar(Modifier, navigator)
                    }
                }
            }

            false -> {
                LaunchedEffect(Unit) {
                    navigator.pop()
                    navigator.push(Login())
                }
            }
        }
    }
}