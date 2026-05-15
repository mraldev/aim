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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.buttonBar
import org.dam.tfg.customElements.profileBar
import org.dam.tfg.dto.LigaCompletoDto
import org.dam.tfg.model.Tirada.SesionHistorial
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.repository.LigaRepository

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

class CompeticionesScreenSuperAdmin : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val ligaRepository = LigaRepository()

        val scope = rememberCoroutineScope()

        var competiciones by remember { mutableStateOf<List<LigaCompletoDto>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        // Clave compuesta: "{ligaNombre}|{sesionIndex}|{tiradaIndex}"
        var invalidadas by remember { mutableStateOf<Set<String>>(emptySet()) }

        LaunchedEffect(Unit) {
            competiciones = ligaRepository.getCompeticiones()

            // Seed inicial: tiradas ya invalidadas según la API
            invalidadas = competiciones.flatMap { liga ->
                liga.sesionesCompetidas.flatMapIndexed { sesionIdx, sesion ->
                    sesion.tiradas.mapIndexedNotNull { tiradaIdx, tirada ->
                        if (tirada.fechaInvalidacion != null) {
                            buildClave(liga.nombreLiga)
                        } else {
                            null
                        }
                    }
                }
            }.toSet()

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
                    text = "Gestión de Competiciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${competiciones.size} ligas registradas",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    competiciones.isEmpty() -> {
                        EmptyState()
                    }
                    else -> {
                        competiciones.forEach { liga ->
                            LigaCard(
                                liga = liga,
                                invalidadas = invalidadas,
                                onInvalidarTirada = { clave, tirada ->
                                    scope.launch {
                                        ligaRepository.invalidar(clave, tirada.usuario.correo!!)

                                        invalidadas = invalidadas + clave
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
// LigaCard — nivel 1
// ---------------------------------------------------------------------------

@Composable
private fun LigaCard(
    liga: LigaCompletoDto,
    invalidadas: Set<String>,
    onInvalidarTirada: (clave: String, tirada: Tirada) -> Unit
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
                // Icono decorativo de liga
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = liga.nombreLiga,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${liga.asociacion.label} · ${liga.tipoCircuito.label}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = liga.fecha.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MiniPill(
                        text = "${liga.competidores.size} competidores",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    MiniPill(
                        text = "${liga.sesionesCompetidas.size} sesiones",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
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

                    // Competidores
                    if (liga.competidores.isNotEmpty()) {
                        SectionLabel("Competidores federados")
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            liga.competidores.take(6).forEach { federado ->
                                MiniPill(
                                    text = "#${federado.numFederado}",
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (liga.competidores.size > 6) {
                                MiniPill(
                                    text = "+${liga.competidores.size - 6}",
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    textColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    SectionLabel("Sesiones")
                    Spacer(modifier = Modifier.height(8.dp))

                    if (liga.sesionesCompetidas.isEmpty()) {
                        Text(
                            "Sin sesiones registradas.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        liga.sesionesCompetidas.forEachIndexed { sesionIdx, sesion ->
                            SesionCard(
                                sesion = sesion,
                                sesionIndex = sesionIdx,
                                ligaNombre = liga.nombreLiga,
                                invalidadas = invalidadas,
                                onInvalidarTirada = onInvalidarTirada
                            )
                            if (sesionIdx < liga.sesionesCompetidas.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SesionCard — nivel 2
// ---------------------------------------------------------------------------

@Composable
private fun SesionCard(
    sesion: SesionHistorial,
    sesionIndex: Int,
    ligaNombre: String,
    invalidadas: Set<String>,
    onInvalidarTirada: (clave: String, tirada: Tirada) -> Unit
) {
    var expandida by remember { mutableStateOf(false) }

    val invalidadasEnSesion = sesion.tiradas.indices.count { tiradaIdx ->
        buildClave(ligaNombre) in invalidadas
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ── Cabecera ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expandida = !expandida },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📅", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sesión ${sesionIndex + 1}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = sesion.fecha.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    MiniPill(
                        text = "${sesion.tiradas.size} tiradas",
                        color = MaterialTheme.colorScheme.surface,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                    if (invalidadasEnSesion > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        MiniPill(
                            text = "$invalidadasEnSesion invalidadas",
                            color = MaterialTheme.colorScheme.errorContainer,
                            textColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (expandida) "▲" else "▼",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Tiradas ─────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = expandida,
                enter = expandVertically(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (sesion.tiradas.isEmpty()) {
                        Text(
                            "Sin tiradas en esta sesión.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        sesion.tiradas.forEachIndexed { tiradaIdx, tirada ->
                            val clave = buildClave(ligaNombre)
                            TiradaCard(
                                tirada = tirada,
                                invalidada = clave in invalidadas,
                                onInvalidar = { onInvalidarTirada(clave, tirada) }
                            )
                            if (tiradaIdx < sesion.tiradas.lastIndex) {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// TiradaCard — nivel 3
// ---------------------------------------------------------------------------

@Composable
private fun TiradaCard(
    tirada: Tirada,
    invalidada: Boolean,
    onInvalidar: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val bgColor = if (invalidada)
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Icono usuario
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (invalidada)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.tertiaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (invalidada) "✗" else "🎯", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Info tirada
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tirada.usuario.correo ?: "Usuario desconocido",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (invalidada)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatItem(label = "Total", value = "${tirada.getPuntuacionTotal()}")
                    StatItem(
                        label = "Perfectas",
                        value = "${tirada.getNumDianasPerfectas()}/${tirada.numDianas}"
                    )
                    StatItem(
                        label = "Aciertos",
                        value = "${"%.0f".format(tirada.getPorcentajeAciertos())}%"
                    )
                }
                Text(
                    text = tirada.tipoCircuito.label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Estado / acción
            if (invalidada) {
                Text(
                    text = "Invalidada",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                OutlinedButton(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Invalidar", fontSize = 11.sp)
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Dialog de confirmación
    // ---------------------------------------------------------------------
    if (showDialog) {
        DialogBase(
            data = mapOf(
                "header" to "Invalidar tirada",
                "content" to "¿Seguro que quieres invalidar esta tirada?",
                "confirmButton" to "Invalidar",
                "dismissButton" to "Cancelar"
            ),
            onConfirm = {
                onInvalidar()
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

// ---------------------------------------------------------------------------
// Componentes de apoyo
// ---------------------------------------------------------------------------

@Composable
private fun MiniPill(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, fontSize = 11.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏹", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Sin competiciones registradas",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Utilidades
// ---------------------------------------------------------------------------

/**
 * Construye la clave única de una tirada para el seguimiento de invalidaciones.
 * Formato: "{ligaNombre}|{sesionIndex}|{tiradaIndex}"
 */
private fun buildClave(ligaNombre: String): String =
    "${ligaNombre}"