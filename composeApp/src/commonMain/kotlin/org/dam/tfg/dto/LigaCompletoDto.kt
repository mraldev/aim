package org.dam.tfg.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito
import org.dam.tfg.model.Tirada.SesionHistorial
import org.dam.tfg.model.Tirada.Tirada

@Serializable
data class LigaCompletoDto(
    val competidores: List<FederadoDtoLiga>,
    val nombreLiga: String,
    val fecha: LocalDate,
    val asociacion: Asociacion,
    val tipoCircuito: TipoCircuito,
    val administradorId: Int,
    val sesionesCompetidas: List<SesionHistorial>
)
