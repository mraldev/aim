package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.VerificacionUsuario

class ApiAuthController {
    private val login = VerificacionUsuario()

    suspend fun logIn(correo: String, contrasenya: String): Boolean {
        return login.logIn(correo, contrasenya)
    }

    suspend fun register(correo: String, contrasenya: String): Boolean {
        return login.register(correo, contrasenya)
    }
}