package org.dam.tfg.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.enums.UserRole

@Serializable
data class UsuarioDtoMostrarBasico(
    val correo: String,
    val fechaAlta: LocalDate,
    val rolUsuario: UserRole,
    val descripcion: String? = "Sin descripcón",
    val correoVerificado: Boolean,
)