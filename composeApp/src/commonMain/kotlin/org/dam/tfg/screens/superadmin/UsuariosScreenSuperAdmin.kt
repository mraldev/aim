package org.dam.tfg.screens.superadmin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar
import org.dam.tfg.dto.UsuarioDtoMostrarBasico
import org.dam.tfg.enums.UserRole
import org.dam.tfg.repository.LoginRepository

// ---------------------------------------------------------------------------
// Roles asignables
// ---------------------------------------------------------------------------

private val ROLES_ASIGNABLES = UserRole.entries

// ---------------------------------------------------------------------------
// Screen EN DESUSO
// ---------------------------------------------------------------------------

class UsuariosScreenSuperAdmin : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val usuarioRepository = LoginRepository()
        val scope = rememberCoroutineScope()

        var usuarios by remember { mutableStateOf<List<UsuarioDtoMostrarBasico>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        // Mapa local correo → rol para reflejar cambios sin recargar
        var rolesLocales by remember { mutableStateOf<Map<String, UserRole>>(emptyMap()) }

        LaunchedEffect(Unit) {
            usuarios = usuarioRepository.getUsuarios()
            rolesLocales = usuarios.associate { it.correo to it.rolUsuario }   // asume campo correo en UsuarioResponse
            isLoading = false
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 80.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Gestión de Usuarios",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${usuarios.size} usuarios registrados",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }

                    usuarios.isEmpty() -> EmptyStateUsuarios()

                    else -> {
                        usuarios.forEach { usuario ->
                            val rolActual = rolesLocales[usuario.correo] ?: usuario.rolUsuario
                            UsuarioCard(
                                usuario = usuario,
                                rolActual = rolActual,
                                onCambiarRol = { nuevoRol ->
                                    scope.launch {
                                        usuarioRepository.cambiarRol(usuario.correo!!, nuevoRol)
                                        rolesLocales = rolesLocales + (usuario.correo to nuevoRol)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                buttonBar(Modifier, navigator)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// UsuarioCard
// ---------------------------------------------------------------------------

@Composable
private fun UsuarioCard(
    usuario: UsuarioDtoMostrarBasico,
    rolActual: UserRole,
    onCambiarRol: (UserRole) -> Unit
) {
    var expandida by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Cabecera clickable ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expandida = !expandida },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar con inicial del correo
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = usuario.correo!!.first().uppercaseChar().toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario.correo!!,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Alta: ${usuario.fechaAlta}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RolPill(rolActual)
                    VerificadoPill(usuario.correoVerificado)
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (expandida) "▲" else "▼",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Cuerpo expandible ───────────────────────────────────────────
            AnimatedVisibility(
                visible = expandida,
                enter = expandVertically(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Descripción (si existe)
                    if (!usuario.descripcion.isNullOrBlank()) {
                        SectionLabelU("Descripción")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = usuario.descripcion,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    SectionLabelU("Cambiar rol")
                    Spacer(modifier = Modifier.height(8.dp))

                    RolDropdown(
                        rolSeleccionado = rolActual,
                        onRolSeleccionado = onCambiarRol
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// RolDropdown — spinner de roles asignables
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RolDropdown(
    rolSeleccionado: UserRole,
    onRolSeleccionado: (UserRole) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = rolSeleccionado.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Rol", fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ROLES_ASIGNABLES.forEach { rol ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(rolEmoji(rol), fontSize = 14.sp)
                            Text(
                                text = rol.name,
                                fontSize = 14.sp,
                                fontWeight = if (rol == rolSeleccionado)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = {
                        onRolSeleccionado(rol)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Componentes de apoyo
// ---------------------------------------------------------------------------

@Composable
private fun RolPill(rol: UserRole) {
    val (bg, fg) = rolColors(rol)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "${rolEmoji(rol)} ${rol.name}",
            fontSize = 11.sp,
            color = fg,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun VerificadoPill(verificado: Boolean) {
    val (bg, fg, label) = if (verificado)
        Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "✓ Verificado"
        )
    else
        Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "✗ Sin verificar"
        )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = fg, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SectionLabelU(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun EmptyStateUsuarios() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("👤", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Sin usuarios registrados",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Utilidades de color / emoji por rol
// ---------------------------------------------------------------------------

@Composable
private fun rolColors(rol: UserRole): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> =
    when (rol) {
        UserRole.ADMIN -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        // Añade aquí el resto de valores de UserRole según necesites
        else -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

private fun rolEmoji(rol: UserRole): String =
    when (rol) {
        UserRole.ADMIN       -> "🛡️"
        // Añade aquí el resto de valores de UserRole según necesites
        else                 -> "👤"
    }