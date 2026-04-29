package org.dam.tfg.model.Tirada

import kotlin.math.round

data class StatsTotal(
    val puntuacionTotal: Int = 0,
    val dianasPerfectas: Int = 0,
    val porcentajePerfecta: Float = 0f,
    val porcentajeAciertos: Float = 0f
){
    fun formatPercent(value: Double): String {
        return "${(round(value * 10) / 10)}%"
    }
}
