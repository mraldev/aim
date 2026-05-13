package org.dam.tfg.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.UserRole
import org.dam.tfg.model.competiciones.Liga

@Serializable
data class Federados(
    val correo: String,
    val contrasenya: String,
    val name: String?,
    val descripcion: String?,
    val rolesUsuario: UserRole,
    val asociaciones: Map<Asociacion, Int>,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val genero: Genero,
    val clubes: List<Club>,
    val ligas: List<Liga>
)
