package org.dam.tfg.dto

import kotlinx.serialization.Serializable

@Serializable
data class FederadoTiradaDto(
    val correo: String?,
    val numFederado: Int
) {
}