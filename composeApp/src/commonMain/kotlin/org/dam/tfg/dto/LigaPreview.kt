package org.dam.tfg.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito

@Serializable
data class LigaPreview(
    val competidores: List<FederadoDtoLiga>,
    val nombreLiga: String,
    val fecha: LocalDate,
    val asociacion: Asociacion,
    val tipoCircuito: TipoCircuito,
    val administradorId: Int
) {
}