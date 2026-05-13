package org.dam.tfg.model

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val nome: String,
    val direccion: String,

)
