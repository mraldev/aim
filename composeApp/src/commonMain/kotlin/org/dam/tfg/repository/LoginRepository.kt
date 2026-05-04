package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiAuthController

class LoginRepository (
        private val api: ApiAuthController = ApiAuthController()
    )
    {
        suspend fun login(correo: String, contrasenya: String): Boolean {
            return api.logIn(correo, contrasenya)
        }

        suspend fun register(correo: String, contrasenya: String): Boolean {
            return api.register(correo, contrasenya)
        }
    }