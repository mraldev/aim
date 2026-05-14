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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.crypto.CredentialStore
import org.dam.tfg.customElements.AnimatedButton
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero

class Settings : Screen {
    private fun limpiarManagers() {
        SesionManager.clear()
        UserManager.clear()
        TokenManager.clear()
        CredentialStore.clear()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val regex = remember {
            Regex(
                pattern = "^[A-Za-z0-9+._%\\-]{1,64}@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$",
                option = RegexOption.IGNORE_CASE
            )
        }

        //- Colores reutilizables para los botones de los dialogs
        val confirmContainerColor = AppColors.Amethyst
        val confirmTextColor      = Color.White
        val confirmBorderColor    = AppColors.Lavender
        val dismissContainerColor = Color.Transparent
        val dismissBorderColor    = Color.Transparent

        //- Colores reutilizables para OutlinedTextField/dropdown con borde Lavender
        val lavenderFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppColors.Lavender,
            unfocusedBorderColor = AppColors.Lavender
        )

        val navigator = LocalNavigator.currentOrThrow

        var showEmailDialog        by remember { mutableStateOf(false) }
        var showPasswordDialog     by remember { mutableStateOf(false) }
        var showConfirmationDialog by remember { mutableStateOf(false) }
        var showFedDialog          by remember { mutableStateOf(false) }
        var showAsociacionDialog   by remember { mutableStateOf(false) }
        var showNameDialog         by remember { mutableStateOf(false) }
        var showFecNacDialog       by remember { mutableStateOf(false) }
        var showSexDialog          by remember { mutableStateOf(false) }

        var asociacionNueva      by remember { mutableStateOf<Asociacion?>(null) }
        var asociacionAEliminar  by remember { mutableStateOf<Asociacion?>(null) }
        var generoNuevo          by remember { mutableStateOf<Genero?>(null) }

        var expandedGenero       by remember { mutableStateOf(false) }
        var expandedAsociacion   by remember { mutableStateOf(false) }

        var contrasenyaNueva     by remember { mutableStateOf("") }
        var confirmarContrasenya by remember { mutableStateOf("") }
        var correoNuevo          by remember { mutableStateOf("") }
        var numFedNuevo          by remember { mutableStateOf("") }
        var nombreNuevo          by remember { mutableStateOf("") }
        var fecNacNuevo          by remember { mutableStateOf<LocalDate?>(null) }

        //- Dialog Confirmación baja cuenta
        if (showConfirmationDialog) {
            DialogBase(
                data = mapOf(
                    "header"        to "Dar de baja",
                    "content"       to "¿Seguro que quieres dar de baja tu cuenta?",
                    "confirmButton" to "Confirmar",
                    "dismissButton" to "Cancelar"
                ),
                onConfirm = {
                    limpiarManagers()
                    navigator.push(Home())
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }

        //- Dialog Confirmar baja de asociación
        asociacionAEliminar?.let { asoc ->
            DialogBase(
                data = mapOf(
                    "header"        to "Dejar asociación",
                    "content"       to "¿Seguro que quieres dejar de pertenecer a ${asoc.label}?",
                    "confirmButton" to "Confirmar",
                    "dismissButton" to "Cancelar"
                ),
                onConfirm = {
                    UserManager.removeAsociacion(asoc)
                    asociacionAEliminar = null
                },
                onDismiss = { asociacionAEliminar = null }
            )
        }

        //- Dialog Cambiar Email
        if (showEmailDialog) {
            AlertDialog(
                onDismissRequest = { showEmailDialog = false },
                containerColor   = AppColors.Champagne,
                confirmButton = {
                    AnimatedButton(
                        onClick = {
                            if (correoNuevo.isBlank() || !regex.matches(correoNuevo)) {
                                correoNuevo = ""
                            } else {
                                UserManager.setCorreo(correoNuevo)
                                showEmailDialog = false
                            }
                        },
                        containerColor = confirmContainerColor,
                        textColor      = confirmTextColor,
                        borderColor    = confirmBorderColor
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    AnimatedButton(
                        onClick        = { showEmailDialog = false },
                        containerColor = dismissContainerColor,
                        borderColor    = dismissBorderColor
                    ) { Text("Cancelar") }
                },
                title = { Text("Cambiar Email") },
                text = {
                    OutlinedTextField(
                        value         = correoNuevo,
                        onValueChange = { correoNuevo = it },
                        label         = { Text("Nuevo Email") },
                        singleLine    = true,
                        colors        = lavenderFieldColors,
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
            )
        }

        //- Dialog Asignar Nombre
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                containerColor   = AppColors.Champagne,
                confirmButton = {
                    AnimatedButton(
                        onClick = {
                            if (nombreNuevo.isNotBlank()) {
                                UserManager.setNombre(nombreNuevo)
                                showNameDialog = false
                            }
                        },
                        containerColor = confirmContainerColor,
                        textColor      = confirmTextColor,
                        borderColor    = confirmBorderColor
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    AnimatedButton(
                        onClick        = { showNameDialog = false },
                        containerColor = dismissContainerColor,
                        borderColor    = dismissBorderColor
                    ) { Text("Cancelar") }
                },
                title = { Text("Asignar Nombre") },
                text = {
                    OutlinedTextField(
                        value         = nombreNuevo,
                        onValueChange = { nombreNuevo = it },
                        label         = { Text("Nombre") },
                        singleLine    = true,
                        colors        = lavenderFieldColors,
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
            )
        }

        //- Dialog Selección de Género
        if (showSexDialog) {
            AlertDialog(
                onDismissRequest = { showSexDialog = false },
                containerColor   = AppColors.Champagne,
                confirmButton = {
                    AnimatedButton(
                        onClick = {
                            if (generoNuevo != null) {
                                UserManager.setGenero(generoNuevo)
                                showSexDialog = false
                            }
                        },
                        containerColor = confirmContainerColor,
                        textColor      = confirmTextColor,
                        borderColor    = confirmBorderColor
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    AnimatedButton(
                        onClick        = { showSexDialog = false },
                        containerColor = dismissContainerColor,
                        borderColor    = dismissBorderColor
                    ) { Text("Cancelar") }
                },
                title = { Text("Seleccionar Género") },
                text = {
                    ExposedDropdownMenuBox(
                        expanded         = expandedGenero,
                        onExpandedChange = { expandedGenero = it },
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    ) {
                        OutlinedTextField(
                            value         = generoNuevo?.name ?: "Selecciona un género",
                            onValueChange = {},
                            readOnly      = true,
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero) },
                            colors        = lavenderFieldColors,
                            modifier      = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded         = expandedGenero,
                            onDismissRequest = { expandedGenero = false }
                        ) {
                            Genero.entries.forEach { genero ->
                                DropdownMenuItem(
                                    text    = { Text(genero.name) },
                                    onClick = {
                                        generoNuevo    = genero
                                        expandedGenero = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }

        //- Dialog Selección de Asociación
        if (showAsociacionDialog) {
            AlertDialog(
                onDismissRequest = { showAsociacionDialog = false },
                containerColor   = AppColors.Champagne,
                confirmButton = {
                    AnimatedButton(
                        onClick = {
                            if (asociacionNueva != null) {
                                UserManager.setAsociacion(asociacionNueva!!, 1)
                                showAsociacionDialog = false
                            }
                        },
                        containerColor = confirmContainerColor,
                        textColor      = confirmTextColor,
                        borderColor    = confirmBorderColor
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    AnimatedButton(
                        onClick        = { showAsociacionDialog = false },
                        containerColor = dismissContainerColor,
                        borderColor    = dismissBorderColor
                    ) { Text("Cancelar") }
                },
                title = { Text("Seleccionar Asociación") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        //- Spinner para añadir nueva asociación
                        ExposedDropdownMenuBox(
                            expanded         = expandedAsociacion,
                            onExpandedChange = { expandedAsociacion = it },
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                        ) {
                            OutlinedTextField(
                                value         = asociacionNueva?.label ?: "Selecciona una asociación",
                                onValueChange = {},
                                readOnly      = true,
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAsociacion) },
                                colors        = lavenderFieldColors,
                                modifier      = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded         = expandedAsociacion,
                                onDismissRequest = { expandedAsociacion = false }
                            ) {
                                Asociacion.entries.forEach { asociacion ->
                                    DropdownMenuItem(
                                        text    = { Text(asociacion.label) },
                                        onClick = {
                                            asociacionNueva    = asociacion
                                            expandedAsociacion = false
                                        }
                                    )
                                }
                            }
                        }

                        //- Lista de asociaciones actuales
                        val asociacionesActuales = UserManager.asociaciones.value
                        if (!asociacionesActuales.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text     = "Tus asociaciones:",
                                style    = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 30.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            asociacionesActuales.forEach { entry ->
                                AnimatedButton(
                                    onClick        = { asociacionAEliminar = entry.key },
                                    textColor = AppColors.Eggshell,
                                    containerColor = AppColors.Amethyst,
                                    borderColor    = AppColors.Lavender,
                                    modifier       = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp, vertical = 2.dp)
                                ) {
                                    Text("✕  ${entry.key.label}")
                                }
                            }
                        }
                    }
                }
            )
        }

        //- Dialog Cambiar Contraseña
        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                containerColor   = AppColors.Champagne,
                confirmButton = {
                    AnimatedButton(
                        onClick = {
                            if (contrasenyaNueva.isBlank() || contrasenyaNueva.length < 8) {
                                contrasenyaNueva = ""
                            } else {
                                if (contrasenyaNueva == confirmarContrasenya) {
                                    UserManager.setContrasenya(contrasenyaNueva)
                                    showPasswordDialog = false
                                }
                            }
                        },
                        containerColor = confirmContainerColor,
                        textColor      = confirmTextColor,
                        borderColor    = confirmBorderColor
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    AnimatedButton(
                        onClick        = { showPasswordDialog = false },
                        containerColor = dismissContainerColor,
                        borderColor    = dismissBorderColor
                    ) { Text("Cancelar") }
                },
                title = { Text("Cambiar Contraseña") },
                text = {
                    Column {
                        Row {
                            OutlinedTextField(
                                value         = contrasenyaNueva,
                                onValueChange = { contrasenyaNueva = it },
                                label         = { Text("Nueva Contraseña") },
                                singleLine    = true,
                                colors        = lavenderFieldColors,
                                modifier      = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                            )
                        }
                        Row {
                            OutlinedTextField(
                                value         = confirmarContrasenya,
                                onValueChange = { confirmarContrasenya = it },
                                label         = { Text("Confirmar Contraseña") },
                                singleLine    = true,
                                colors        = lavenderFieldColors,
                                modifier      = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                            )
                        }
                    }
                }
            )
        }

        //- Dialog Número federado
        if (showFedDialog) {
            AlertDialog(
                onDismissRequest = { showFedDialog = false },
                containerColor   = AppColors.Champagne,
                confirmButton = {
                    AnimatedButton(
                        onClick = {
                            if (!numFedNuevo.all { it.isDigit() }) {
                                numFedNuevo = "Solo se aceptan números."
                            } else {
                                UserManager.setNumFed(numFedNuevo.toInt())
                                showFedDialog = false
                            }
                        },
                        containerColor = confirmContainerColor,
                        textColor      = confirmTextColor,
                        borderColor    = confirmBorderColor
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    AnimatedButton(
                        onClick        = { showFedDialog = false },
                        containerColor = dismissContainerColor,
                        borderColor    = dismissBorderColor
                    ) { Text("Cancelar") }
                },
                title = { Text("Cambiar número federado") },
                text = {
                    OutlinedTextField(
                        value         = numFedNuevo,
                        onValueChange = { numFedNuevo = it },
                        label         = { Text("Numero de federado") },
                        singleLine    = true,
                        colors        = lavenderFieldColors,
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    )
                }
            )
        }

        //- Dialog Fecha nacimiento
        if (showFecNacDialog) {
            org.dam.tfg.screens.Historial.DatePicker(
                onDateSelected = { date ->
                    fecNacNuevo = date
                    UserManager.setFechaNacimiento(fecNacNuevo)
                    showFecNacDialog = false
                },
                onDismiss = { showFecNacDialog = false }
            )
        }

        //- Código UI
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
            ) {
                profileBar(navigator)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text  = "AJUSTES DE CUENTA",
                    style = MaterialTheme.typography.titleLarge
                )

                //- Nombre o email como subtítulo
                Column {
                    UserManager.correo.value?.let { correo ->
                        Text(
                            text  = UserManager.nombre.value ?: correo.split("@")[0],
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                //- Cuenta
                Row {
                    AnimatedButton(
                        onClick  = { showPasswordDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cambiar contraseña") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AnimatedButton(
                        onClick  = { showEmailDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cambiar Correo") }
                }

                Spacer(modifier = Modifier.height(50.dp))

                //- Datos del perfil (botones siempre visibles)
                Row {
                    AnimatedButton(
                        onClick  = { showFedDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar número federado") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AnimatedButton(
                        onClick  = { showAsociacionDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar asociación") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AnimatedButton(
                        onClick  = { showNameDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar nombre") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AnimatedButton(
                        onClick  = { showFecNacDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar fecha nacimiento") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AnimatedButton(
                        onClick  = { showSexDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Asignar genero") }
                }

                Spacer(modifier = Modifier.height(50.dp))

                //- Sesión
                Row {
                    AnimatedButton(
                        onClick = {
                            limpiarManagers()
                            navigator.push(Login())
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cerrar Sesión") }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    AnimatedButton(
                        onClick  = { showConfirmationDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Dar la cuenta de baja") }
                }
            }

            Row(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator)
            }
        }
    }
}