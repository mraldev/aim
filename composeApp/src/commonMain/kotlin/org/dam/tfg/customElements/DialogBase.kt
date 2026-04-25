package org.dam.tfg.customElements

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

//! Ejemplo de como llamar al dialogBase, confirmar y dissmiss son opcionales
//?if (showDialog) {
//?    DialogBase(
//?        data = mapOf(
//?            "header"        to "Título del diálogo",
//?            "content"       to "Aquí va el contenido del mensaje.",
//?            "confirmButton" to "Confirmar",
//?            "dismissButton" to "Cancelar"
//?        ),
//?        onConfirm = { codigo a ejecutar }
//?        onDismiss = { showDialog = false }
//?    )


@Composable
fun DialogBase(
    data: Map<String, String>,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null //? Esto hace que sea nulable, si no se rellena solo habrá opción de aceptar
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        title = data["header"]?.let {
            { Text(text = it) }
        },
        text = data["content"]?.let {
            { Text(text = it) }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(data["confirmButton"] ?: "Aceptar")
            }
        },
        dismissButton = data["dismissButton"]?.let { label ->
            {
                TextButton(onClick = { onDismiss?.invoke() }) {
                    Text(label)
                }
            }
        }
    )
}