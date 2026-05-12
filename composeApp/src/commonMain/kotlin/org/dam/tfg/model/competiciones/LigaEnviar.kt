package org.dam.tfg.model.competiciones

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.dto.FederadoTiradaDto
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito

@Serializable
data class LigaEnviar (
    val competidores: List<FederadoTiradaDto>,
    val asociacion: Asociacion,
    val tipoCircuito: TipoCircuito,
    val nombreLiga: String,
    val fecha: LocalDate,
    val administradorId: Int
){
}