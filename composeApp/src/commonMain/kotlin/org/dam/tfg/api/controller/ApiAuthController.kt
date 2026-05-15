package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.VerificacionUsuario
import org.dam.tfg.api.responses.UsuarioResponse
import org.dam.tfg.dto.UsuarioDtoMostrarBasico
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
}