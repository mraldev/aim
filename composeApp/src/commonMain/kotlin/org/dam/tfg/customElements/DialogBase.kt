package org.dam.tfg.customElements

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//! Ejemplo de como llamar al dialogBase, confirmar y dissmiss son opcionales
//?if (showDialog) {
//?    DialogBase(
//?        data = mapOf(
//?            "header"        to "Título del diálogo",
//?            "content"       to "Aquí va el contenido del mensaje.",
//?            "confirmButton" to "Confirmar",
//?            "dismissButton" to "Cancelar"
//?        ),
//?        onConfirm = { codigo a ejecutar },
//?        onDismiss = { showDialog = false }
//?    )

//! Si necesitas un composable personalizado en el cuerpo (ej: TextField), usa customContent:
//?    DialogBase(
//?        data = mapOf(...),
//?        customContent = { OutlinedTextField(...) },
//?        onConfirm = { ... },
//?        onDismiss = { showDialog = false }
//?    )

@Composable
fun DialogBase(
    data: Map<String, String>,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,                   //? Nulable, si no se rellena solo habrá opción de aceptar
    customContent: (@Composable () -> Unit)? = null,   //? Slot opcional para contenido personalizado en el cuerpo

    dialogContainerColor : Color = AppColors.Champagne,        //- Color Champagne para el fondo del diálogo

    dialogTextColor      : Color = AppColors.Black,            //- Color Black para el texto del diálogo

    confirmContainerColor: Color = AppColors.Amethyst,         //- Color Amethyst para el fondo del botón confirmar
    confirmTextColor     : Color = AppColors.Eggshell,         //- Color Eggshell para el texto del botón confirmar
    confirmBorderColor   : Color = AppColors.Lavender,         //- Color Lavender para el borde del botón confirmar

    dismissContainerColor: Color = Color.Transparent,          //- Transparente por defecto para el fondo del botón cancelar
    dismissTextColor     : Color = AppColors.Black,            //- Color Black para el texto del botón cancelar
    dismissBorderColor   : Color = Color.Transparent           //- Transparente por defecto para el borde del botón cancelar
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        containerColor   = dialogContainerColor,               //- Color de fondo del diálogo
        title = data["header"]?.let {
            { Text(text = it, color = dialogTextColor) }       //- Color de texto del título
        },
        //- Si hay customContent se usa como cuerpo, si no se usa el texto de data["content"]
        text = customContent
            ?: data["content"]?.let {
                { Text(text = it, color = dialogTextColor) }   //- Color de texto del contenido
            },
        confirmButton = {
            OutlinedButton(
                onClick = onConfirm,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = confirmContainerColor,    //- Color Champagne de fondo del botón confirmar
                    contentColor   = confirmTextColor          //- Color Black de texto del botón confirmar
                ),
                border = BorderStroke(2.dp, confirmBorderColor) //- Color Lavender del borde del botón confirmar
            ) {
                Text(data["confirmButton"] ?: "Aceptar")
            }
        },
        dismissButton = data["dismissButton"]?.let { label ->
            {
                OutlinedButton(
                    onClick = { onDismiss?.invoke() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = dismissContainerColor, //- Transparente para el fondo del botón cancelar
                        contentColor   = dismissTextColor       //- Color Iris para el texto del botón cancelar
                    ),
                    border = BorderStroke(1.dp, dismissBorderColor) //- Transparente para el borde del botón cancelar
                ) {
                    Text(label)
                }
            }
        }
    )
}