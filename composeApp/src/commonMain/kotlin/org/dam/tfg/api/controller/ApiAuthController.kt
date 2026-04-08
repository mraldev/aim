package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.LogIn

class ApiAuthController {
    private val login = LogIn()

    suspend fun logIn(correo: String, contrasenya: String): Boolean {
        return login.logIn(correo, contrasenya)
    }
}