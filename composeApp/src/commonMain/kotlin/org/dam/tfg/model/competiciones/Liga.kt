package org.dam.tfg.model.competiciones

import kotlinx.datetime.LocalDate
import org.dam.tfg.dto.FederadoTiradaDto
import org.dam.tfg.dto.UsuarioTiradaDTO

data class Liga(
    val sesionesCompetidas: List<SesionCompetitiva>,
    val competidores: List<FederadoTiradaDto>,
    val nombreLiga: String,
    val fecha: LocalDate,
    val administradorId: Int
)
