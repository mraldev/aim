package org.dam.tfg.dto

import kotlinx.serialization.Serializable

@Serializable
data class PuntuacionTiradaDTO (
    val valores: MutableList<Int?>

) {
}