package org.dam.tfg.model.Tirada

fun calcularStatsDiana(flechas: List<Int?>): StatsDiana {
    val puntuadas = flechas.filterNotNull()
    return StatsDiana(
        puntuacion = puntuadas.sum(),
        numFlechasPuntuadas = puntuadas.size,
        esPerfecta = flechas.isNotEmpty() && puntuadas.size == flechas.size && puntuadas.all { it >= 10 }
    )
}

fun calcularStatsTotal(
    puntuaciones: List<List<Int?>>,
    numDianas: Int,
    flechasPorDiana: Int
): StatsTotal {
    val todasPuntuadas = puntuaciones.flatten().filterNotNull()
    val dianasPerfectas = puntuaciones.count { diana ->
        diana.isNotEmpty() && diana.all { it != null && it >= 10 }
    }
    return StatsTotal(
        puntuacionTotal = todasPuntuadas.sum(),
        dianasPerfectas = dianasPerfectas,
        porcentajePerfecta = if (numDianas > 0) (dianasPerfectas.toFloat() / numDianas) * 100f else 0f,
        porcentajeAciertos = if (todasPuntuadas.isNotEmpty()) (todasPuntuadas.count { it > 0 }.toFloat() / todasPuntuadas.size) * 100f else 0f
    )
}

fun formatTiempo(segundos: Long): String {
    val m = segundos / 60
    val s = segundos % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}