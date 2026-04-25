package org.dam.tfg.model.Tirada

import kotlinx.serialization.Serializable

@Serializable
data class SesionEnviar(
    val tiradas: List<Tirada>
)
