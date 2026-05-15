package org.dam.tfg.repository

import kotlinx.datetime.LocalDate
import org.dam.tfg.api.controller.ApiAuthController
import org.dam.tfg.api.responses.UsuarioResponse
import org.dam.tfg.dto.UsuarioDtoMostrarBasico
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero
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

        suspend fun baja(): Boolean {
            return api.baja()
        }

        suspend fun eliminarAsociacion(asociacion: Asociacion): Boolean {
            return api.eliminarAsociacion(asociacion)
        }

        suspend fun nuevoCorreo(nuevoCorreo: String): Boolean {
            return api.nuevoCorreo(nuevoCorreo)
        }

        suspend fun putNombre(nombre: String): Boolean {
            return api.putNombre(nombre)
        }

        suspend fun putGenero(genero: Genero): Boolean {
            return api.putGenero(genero)
        }

        suspend fun putAsociacion(asociacion: Asociacion, numAsociado: Int): Boolean {
            return api.putAsociacion(asociacion, numAsociado)
        }

        suspend fun putContrasenya(contrasenya: String): Boolean {
            return api.putContrasenya(contrasenya)
        }

        suspend fun putFechaNac(fechaNacimiento: LocalDate): Boolean {
            return api.putFechaNac(fechaNacimiento)
        }
    }