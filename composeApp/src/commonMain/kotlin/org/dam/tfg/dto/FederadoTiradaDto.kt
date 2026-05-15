package org.dam.tfg.dto

import kotlinx.serialization.Serializable

@Serializable
data class FederadoTiradaDto(
    val correo: String? = null,
    val numFederado: Int
) {
}