package org.dam.tfg.model.competiciones

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.dto.FederadoTiradaDto

//Actua de la misma manera que Sesion, pero para competiciones
@Serializable
data class SesionCompetitiva(
    val tiradasCompetitivas: List<TiradaCompetitiva>,
    val fecha: LocalDate?
){
    fun getCompetidores(): List<FederadoTiradaDto>{
        return tiradasCompetitivas.map { it.usuario }
    }
}
