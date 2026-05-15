package org.dam.tfg.screens.competicion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.customElements.DialogBase
import org.dam.tfg.customElements.DialogContent
import org.dam.tfg.dto.DatosCompeticionDto
import org.dam.tfg.dto.UsuarioTiradaDTO
import org.dam.tfg.enums.Estilo
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.Posicion
import org.dam.tfg.enums.RangoDeEdad
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.enums.UserRole
import org.dam.tfg.helpers.HelperCargadorDeTipoDeTirada
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.Tirada
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.screens.Historial.HistorialScreen
import org.dam.tfg.screens.TiradaScreen

@Composable
internal fun DetalleCompeticion(
    competicion: org.dam.tfg.dto.LigaPreview,
    userRole: org.dam.tfg.enums.UserRole?,
    userId: kotlin.String,
    onBack: () -> kotlin.Unit,
    onParticipar: () -> kotlin.Unit,
    onDejarParticipar: () -> kotlin.Unit,
    onEditar: () -> kotlin.Unit,
    onCancelarCompeticion: () -> kotlin.Unit,
    onEliminarParticipante: (kotlin.String) -> kotlin.Unit,
    onApuntarTirada: (kotlin.Int, kotlin.Int, kotlin.collections.List<kotlin.String>) -> kotlin.Unit
) {
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}