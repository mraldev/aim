package org.dam.tfg.model

import kotlinx.serialization.Serializable
import org.dam.tfg.enums.TipoCircuito

/**
 * Representa una tirada de arquería realizada por un usuario.
 *
 * @property usuario El usuario que realiza la tirada.
 * @property numDianas El número total de dianas en el circuito.
 * @property numMaxFlechasPorDiana El número máximo de flechas permitidas por diana.
 * @property puntuaciones Mapa que asocia el índice de cada diana (0 hasta [numDianas] - 1)
 * con la lista de puntuaciones de cada flecha disparada en esa diana.
 * @property tipoCircuito El tipo de circuito en el que se realiza la tirada.
 *
 */
@Serializable
data class Tirada (
    val usuario: Users,
    val numDianas: Int,
    val numMaxFlechasPorDiana: Int,
    val puntuaciones: MutableMap<Int, List<Int>> = mutableMapOf(),
    val tipoCircuito: TipoCircuito,
) {

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
        return puntuaciones.values.sumOf { flechas -> flechas.sum() }
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
        puntuaciones[numDiana] = flechas
    }
}