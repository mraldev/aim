package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiAuthController
import org.dam.tfg.api.responses.UsuarioResponse
import org.dam.tfg.dto.UsuarioDtoMostrarBasico
import org.dam.tfg.enums.UserRole

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

        suspend fun getUsuarios(): List<UsuarioDtoMostrarBasico> {
            return api.getTodos()
        }

        suspend fun cambiarRol(correo: String, nuevoRole: UserRole): UsuarioDtoMostrarBasico? {
            return api.nuevoRol(correo, nuevoRole)
        }
    }