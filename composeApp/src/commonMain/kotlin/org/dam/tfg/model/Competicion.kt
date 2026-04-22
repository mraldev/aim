package org.dam.tfg.model

import kotlinx.datetime.LocalDate
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.TipoCircuito

data class Competicion(
    val id: String,
    val nombre: String,
    val asociacion: Asociacion,
    val tipoCircuito: TipoCircuito,
    val fecha: LocalDate,
    val numFlechas: Int,
    val numDianas: Int,
    val administradorId: String,
    val participantes: List<String> = emptyList(),
    val cancelada: Boolean = false
)
