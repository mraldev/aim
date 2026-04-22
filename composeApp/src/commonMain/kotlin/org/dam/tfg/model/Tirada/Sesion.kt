package org.dam.tfg.model.Tirada

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Sesion(
    val tiradas: List<Tirada>
)
