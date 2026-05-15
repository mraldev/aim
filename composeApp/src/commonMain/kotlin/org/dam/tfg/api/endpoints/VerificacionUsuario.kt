package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.api.responses.FederadoResponse
import org.dam.tfg.api.responses.LogInCheckRole
import org.dam.tfg.api.responses.LogInResponse
import org.dam.tfg.api.responses.UsuarioResponse
import org.dam.tfg.api.responses.RegisterResponse
import org.dam.tfg.api.responses.ResponseHelper
import org.dam.tfg.dto.UsuarioDtoMostrarBasico
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero
import org.dam.tfg.enums.UserRole

class VerificacionUsuario {

    /**
     * Función de LogIn. Deja almacenado en memoria (y en futuras actualizaciones, cifrado con persistencia)
     * el correo y la contraseña. Hace LogIn automáticamente en usuario federado admin o superadmin depende del correo
     * que se le haya pasado 
     *
     * @param correo correo del usuario
     * @param contrasenya contraseña del usuario. El día de mañana se pasará directamente encriptada,
     * el propio login la desencriptará
     *
     * @return Devuelve un booleano depende de si el login ha funcionado o no. Guarda
     * automáticamente el token en el TokenManager
     *
     * .
     *
     * Ejemplo:
     *
     * api.logIn("marcos", "asdf") //api.logIn(correo, contrasenya)
     *
     * println(TokenManager.getToken())
     *
     * console >> fhdjsbs.fnsjhbfdsauhfdbsajhuajfds.fabsjhbfasdujhfbasujh
     * */
    suspend fun logIn(correo: String, contrasenya: String): Boolean {
        try {
            val response = ApiClient.client.post(
                "${ApiConfig.BASE_URL}/auth/login"
            ){
                contentType(ContentType.Application.Json)

                setBody(
                    mapOf(
                        "correo" to correo.trim().lowercase(),
                        "contrasenya" to contrasenya
                    )
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            val check = response.body<LogInCheckRole>()

            val body: LogInResponse = when (check.rol) {
                UserRole.FEDERADO -> {
                    response.body<FederadoResponse>()
                }

                UserRole.ADMIN -> {
                    response.body<FederadoResponse>()
                }

                else -> {
                    response.body<UsuarioResponse>()
                }
            }

            TokenManager.setToken(body.token)
            UserManager.setCorreo(correo.trim().lowercase())
            UserManager.setContrasenya(contrasenya)

            UserManager.asignarValoresDesdeLogInResponse(body)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    /**
     * Función de registro, simple. Deja almacenado en memoria (y en futuras actualizaciones, cifrado con persistencia)
     *      * el correo y la contraseña
     *
     * @param correo correo del usuario
     * @param contrasenya contraseña del usuario.
     *
     *
     * @return Devuelve un booleano depende de si el registro ha funcionado o no. Guarda
     * automáticamente el token en el TokenManager
     *
     *
     *
     * Ejemplo:
     *
     * api.logIn("marcos", "asdf") //api.register(correo, contrasenya)
     *
     * println(TokenManager.getToken())
     *
     * console >> fhdjsbs.fnsjhbfdsauhfdbsajhuajfds.fabsjhbfasdujhfbasujh
     * */
    suspend fun register(correo: String, contrasenya: String): Boolean {
        try {
            val response = ApiClient.client.post(
                "${ApiConfig.BASE_URL}/auth/registrar"
            ){
                contentType(ContentType.Application.Json)

                setBody(
                    mapOf(
                        "correo" to correo.trim().lowercase(),
                        "contrasenya" to contrasenya
                    )
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            val body = response.body<RegisterResponse>()

            TokenManager.setToken(body.token)
            UserManager.setCorreo(correo.trim().lowercase())
            UserManager.setContrasenya(contrasenya)
            UserManager.setCorreoVerificado(false)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun getTodos(): List<UsuarioDtoMostrarBasico> {
        try {
            val response = ApiClient.client.get(
                "${ApiConfig.BASE_URL}/usuarios/"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
            }

            val exito = ResponseHelper.validarResponse(response)

            val lista = response.body<List<UsuarioDtoMostrarBasico>>()

            return lista

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return emptyList()
        }
    }

    suspend fun nuevoRol(correo: String, nuevoRole: UserRole): UsuarioDtoMostrarBasico? {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/usuarios/actualizar/rol"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", correo)
                header("nuevoRol", nuevoRole)
            }

            val exito = ResponseHelper.validarResponse(response)

            val lista = response.body<UsuarioDtoMostrarBasico>()

            return lista

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return null
        }
    }

    suspend fun baja(): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/usuarios/baja"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun eliminarAsociacion(asociacion: Asociacion): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/eliminar/asociacion"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("asociacion", asociacion)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun nuevoCorreo(nuevoCorreo: String): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/actualizar/correo"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("nuevoCorreo", nuevoCorreo)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun putNombre(nombre: String): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/actualizar/nombre"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("nombre", nombre)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun putGenero(genero: Genero): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/actualizar/genero"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("genero", genero)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun putAsociacion(asociacion: Asociacion, numAsociado: Int): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/agregar/asociacion"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("asociacion", asociacion)
                header("numAsociado", numAsociado)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun putContrasenya(contrasenya: String): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/actualizar/contrasenya"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("contrasenya", contrasenya)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    suspend fun putFechaNac(fechaNacimiento: LocalDate): Boolean {
        try {
            val response = ApiClient.client.put (
                "${ApiConfig.BASE_URL}/actualizar/nacimiento"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")
                header("correo", UserManager.correo.value)
                header("fechaNacimiento", fechaNacimiento)
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }
}