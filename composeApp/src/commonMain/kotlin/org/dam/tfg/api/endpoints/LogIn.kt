package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.responses.LogInResponse
import org.dam.tfg.api.responses.RegisterResponse

class LogIn {

    /**
     * Función de LogIn, simple
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

            val body = response.body<LogInResponse>()

            TokenManager.setToken(body.token)

            return true

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }

    /**
     * Función de registro, simple
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

            val body = response.body<RegisterResponse>()

            TokenManager.setToken(body.token)

            return true

        } catch (e: Exception) {
            println("Login error: ${e.message}")
            return false
        }
    }
}