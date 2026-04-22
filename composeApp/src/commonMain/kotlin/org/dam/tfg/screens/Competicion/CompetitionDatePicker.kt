package org.dam.tfg.screens.competicion

import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompeticionDatePicker(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // TODO: convertir state.selectedDateMillis a LocalDate real
                onDateSelected(LocalDate(2024, 1, 1))
            }) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) { DatePicker(state = state) }
}