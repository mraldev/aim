package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
                        "correo" to correo,
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

                else -> {
                    response.body<UsuarioResponse>()
                }
            }

            TokenManager.setToken(body.token)
            UserManager.setCorreo(correo)
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
                        "correo" to correo,
                        "contrasenya" to contrasenya
                    )
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            val body = response.body<RegisterResponse>()

            TokenManager.setToken(body.token)
            UserManager.setCorreo(correo)
            UserManager.setContrasenya(contrasenya)
            UserManager.setCorreoVerificado(false)

            return exito

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }
}