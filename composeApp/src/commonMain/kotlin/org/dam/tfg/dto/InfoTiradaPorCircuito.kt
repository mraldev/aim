package org.dam.tfg.dto

import kotlinx.serialization.Serializable

@Serializable
data class InfoTiradaPorCircuito(
    val numDianas: Int,
    val numFlechasPorDiana: Int
)
