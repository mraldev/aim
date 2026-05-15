package org.dam.tfg.api.controller

import kotlinx.datetime.LocalDate
import org.dam.tfg.api.endpoints.VerificacionUsuario
import org.dam.tfg.api.responses.UsuarioResponse
import org.dam.tfg.dto.UsuarioDtoMostrarBasico
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.UserRole

class ApiAuthController {
    private val login = VerificacionUsuario()

    suspend fun logIn(correo: String, contrasenya: String): Boolean {
        return login.logIn(correo, contrasenya)
    }

    suspend fun register(correo: String, contrasenya: String): Boolean {
        return login.register(correo, contrasenya)
    }

    suspend fun getTodos(): List<UsuarioDtoMostrarBasico> {
        return login.getTodos()
    }

    suspend fun nuevoRol(correo: String, nuevoRole: UserRole): UsuarioDtoMostrarBasico? {
        return login.nuevoRol(correo, nuevoRole)
    }

    suspend fun baja(): Boolean {
        return login.baja()
    }

    suspend fun eliminarAsociacion(asociacion: Asociacion): Boolean {
        return login.eliminarAsociacion(asociacion)
    }

    suspend fun nuevoCorreo(nuevoCorreo: String): Boolean {
        return login.nuevoCorreo(nuevoCorreo)
    }

    suspend fun putNombre(nombre: String): Boolean {
        return login.putNombre(nombre)
    }

    suspend fun putGenero(genero: Genero): Boolean {
        return login.putGenero(genero)
    }

    suspend fun putAsociacion(asociacion: Asociacion, numAsociado: Int): Boolean {
        return login.putAsociacion(asociacion, numAsociado)
    }

    suspend fun putContrasenya(contrasenya: String): Boolean {
        return login.putContrasenya(contrasenya)
    }

    suspend fun putFechaNac(fechaNacimiento: LocalDate): Boolean {
        return login.putFechaNac(fechaNacimiento)
    }

}