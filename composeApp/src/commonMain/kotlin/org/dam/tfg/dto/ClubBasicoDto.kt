package org.dam.tfg.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubBasicoDto(
    val nombre: String,
    val direccion: String
) {
}