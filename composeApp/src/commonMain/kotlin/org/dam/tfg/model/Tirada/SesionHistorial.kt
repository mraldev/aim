package org.dam.tfg.model.Tirada

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SesionHistorial (
    val tiradas: List<Tirada>,
    val fecha: LocalDate
)