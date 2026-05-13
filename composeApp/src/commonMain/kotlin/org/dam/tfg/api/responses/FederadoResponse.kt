package org.dam.tfg.api.responses

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.dto.ClubBasicoDto
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.UserRole

@Serializable
data class FederadoResponse(
    override val token: String,
    val fechaAlta: LocalDate,
    val rol: UserRole,
    val descripcion: String?,
    val correoVerificado: Boolean,

    val asociaciones: Map<Asociacion, Int>,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val genero: Genero,
    val clubes: List<ClubBasicoDto>
): LogInResponse
