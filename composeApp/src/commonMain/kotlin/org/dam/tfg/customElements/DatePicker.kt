package org.dam.tfg.screens.Historial

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = state.selectedDateMillis
                if (millis != null) {
                    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                    val date = instant.toLocalDateTime(kotlinx.datetime.TimeZone.UTC).date
                    onDateSelected(date)
                } else {
                    onDismiss()
                }
            }) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = state)
    }
}