package org.dam.tfg.api.responses

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.enums.UserRole

@Serializable
data class LogInResponse(
    val token: String,
    val fechaAlta: LocalDate,
    val rol: UserRole,
    val descripcion: String?,
    val correoVerificado: Boolean
)