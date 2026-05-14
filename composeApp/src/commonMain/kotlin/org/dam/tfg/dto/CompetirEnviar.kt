package org.dam.tfg.dto

import kotlinx.serialization.Serializable
import org.dam.tfg.enums.Estilo
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.Posicion
import org.dam.tfg.enums.RangoDeEdad
import org.dam.tfg.enums.TipoCircuito

@Serializable
data class CompetirEnviar(
    val usuario: UsuarioTiradaDTO,
    val numDianas: Int,
    val numMaxFlechasPorDiana: Int,
    val puntuaciones: MutableList<PuntuacionTiradaDTO>,
    val tipoCircuito: TipoCircuito,

    val nombreLigaAsociada: String,
    val dorsal: Int,
    val posicion: Posicion,
    val patrulla: Int,
    val estilo: Estilo,
    val rangoEdad: RangoDeEdad,
    val genero: Genero
)
