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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import org.dam.tfg.customElements.DialogBase
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

        //- Diálogo para editar la biografía, usa customContent para alojar el TextField
        if (showEditBioDialog) {
            DialogBase(
                data = mapOf(
                    "header"        to "Cambiar Biografia",
                    "confirmButton" to "Confirmar",
                    "dismissButton" to "Cancelar"
                ),
                customContent = {
                    OutlinedTextField(
                        value = bioNueva,
                        onValueChange = { bioNueva = it },
                        label = { Text("Biografia") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(         //- Borde en Lavender para mantener consistencia con el resto de inputs
                            focusedBorderColor   = AppColors.Lavender,
                            unfocusedBorderColor = AppColors.Lavender,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                },
                onConfirm = {
                    UserManager.setDescripcion(bioNueva)
                    showEditBioDialog = false
                },
                onDismiss = { showEditBioDialog = false }
            )
        }

        when (loggedIn) {
            null -> {
                //- Estado de carga mientras se comprueba la sesión
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Comprobando sesión...")
                }
            }

            true -> {
                //- Usuario logueado, se muestra el perfil
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
                //- Sesión no válida, redirige al login
                LaunchedEffect(Unit) {
                    navigator.pop()
                    navigator.push(Login())
                }
            }
        }
    }
}