package org.dam.tfg.model.competiciones

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.dto.FederadoTiradaDto
import org.dam.tfg.dto.PuntuacionTiradaDTO
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Estilo
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.Posicion
import org.dam.tfg.enums.RangoDeEdad
import org.dam.tfg.enums.TipoCircuito

@Serializable
data class TiradaCompetitiva(
    val id: String,
    val usuario: FederadoTiradaDto,
    val fecha: LocalDate?,
    val numDianas: Int,
    val numMaxFlechasPorDiana: Int,
    val puntuaciones: MutableList<PuntuacionTiradaDTO>,
    val tipoCircuito: TipoCircuito,
    val asociacion: Asociacion,
    val dorsal: Int,
    val posicion: Posicion,
    val patrulla: Int,
    val estilo: Estilo,
    val edad: RangoDeEdad,
    val genero: Genero,
    val cancelada: Boolean = false
){
    /**
     * Calcula la puntuación total de la tirada sumando los puntos
     * de todas las flechas en todas las dianas.
     *
     * @return La suma total de todas las puntuaciones registradas.
     *
     * @sample
     * // puntuaciones = {0: [9, 8, 7], 1: [10, 6, 5]}
     * // getPuntuacionTotal() -> 45
     */
    fun getPuntuacionTotal(): Int {
        return puntuaciones.sumOf { dto -> dto.valores.filterNotNull().sum() }
    }

    /**
     * Registra o sobreescribe la puntuación de una diana concreta.
     *
     * Si la diana ya tenía puntuaciones registradas, estas serán reemplazadas
     * por la nueva lista proporcionada.
     *
     * @param numDiana Índice de la diana (debe estar entre 0 y [numDianas] - 1).
     * @param flechas Lista de puntuaciones de cada flecha disparada en esa diana
     * (su tamaño no puede superar [numMaxFlechasPorDiana]).
     *
     * @throws IllegalArgumentException Si [numDiana] está fuera del rango válido.
     * @throws IllegalArgumentException Si [flechas] contiene más elementos de los permitidos.
     *
     * @sample
     * // tirada.agregarPuntuacion(0, listOf(9, 8, 7))
     * // tirada.puntuaciones -> {0: [9, 8, 7]}
     */
    fun agregarPuntuacion(numDiana: Int, flechas: List<Int>) {
        require(numDiana in 0 until numDianas) { "Diana $numDiana no existe" }
        require(flechas.size <= numMaxFlechasPorDiana) { "Demasiadas flechas para la diana $numDiana" }
        puntuaciones[numDiana] = PuntuacionTiradaDTO(flechas.map { it as Int? }.toMutableList())
    }

    /**
     * Devuelve las puntuaciones de una diana, o una lista de nulls si no existe.
     */
    fun getFlechasDiana(numDiana: Int): List<Int?> =
        puntuaciones.getOrNull(numDiana)?.valores?.map { it as Int? }
            ?: List(numMaxFlechasPorDiana) { null }

    /**
     * Suma las puntuaciones registradas (no nulas) de una diana concreta.
     *
     * @sample
     * // diana = [null, 8, 10]  →  getPuntuacionDiana(0) = 18
     */
    fun getPuntuacionDiana(numDiana: Int): Int =
        getFlechasDiana(numDiana).filterNotNull().sum()

    /**
     * Indica si una diana es perfecta (todas sus flechas puntuadas con 10 u 11).
     */
    fun isDianaPerfecta(numDiana: Int): Boolean {
        val flechas = getFlechasDiana(numDiana)
        return flechas.isNotEmpty() && flechas.all { it != null && it >= 10 }
    }

    /** Número de dianas perfectas en toda la tirada. */
    fun getNumDianasPerfectas(): Int =
        (0 until numDianas).count { isDianaPerfecta(it) }

    /**
     * Porcentaje de dianas perfectas sobre el total de dianas.
     * Devuelve 0.0 si no hay dianas.
     */
    fun getPorcentajePerfecta(): Float =
        if (numDianas == 0) 0f
        else (getNumDianasPerfectas().toFloat() / numDianas.toFloat()) * 100f

    /**
     * Porcentaje de flechas puntuadas con valor > 0 sobre el total de flechas puntuadas.
     * Devuelve 0.0 si no hay ninguna flecha puntuada.
     *
     * @sample
     * // flechas puntuadas = [0, 8, 10]  →  getPorcentajeAciertos() = 66.67
     */
    fun getPorcentajeAciertos(): Float {
        val puntuadas = puntuaciones.flatMap { it.valores }.filterNotNull()
        return if (puntuadas.isEmpty()) 0f
        else (puntuadas.count { it > 0 }.toFloat() / puntuadas.size.toFloat()) * 100f
    }

    /** Indica si todas las flechas de todas las dianas han sido puntuadas. */
    fun isCompleta(): Boolean =
        puntuaciones.size == numDianas && puntuaciones.all { dto -> dto.valores.size == numMaxFlechasPorDiana }
}