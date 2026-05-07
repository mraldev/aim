package org.dam.tfg.customElements.Tirada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.dam.tfg.customElements.DialogContent

@Composable
fun TiradaDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, List<String>) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Fondo con efecto glass — base para todos los dialogs futuros
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)) //- Oscurecido semitransparente sobre el fondo
                    .blur(200.dp)                                 //- Efecto blur al fondo de la app
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.Champagne,                     //- Color Mauve para el fondo del dialog
                    tonalElevation = 4.dp
                ) {
                    DialogContent(
                        header = "Nueva tirada",
                        onConfirm = onConfirm,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}