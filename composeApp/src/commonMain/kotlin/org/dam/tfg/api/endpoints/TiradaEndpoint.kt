package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.api.responses.ResponseHelper
import org.dam.tfg.exceptions.ExceptionNoRegistrado
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.SesionHistorial

class TiradaEndpoint {
    suspend fun registrar(sesionEnviar: SesionEnviar): Boolean {
        try {
            val response = ApiClient.client.post(
                "${ApiConfig.BASE_URL}/tiradas/registrar"
            ) {
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")

                setBody(
                    sesionEnviar
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Tirada error: ${e.message}")
            return false
        }
    }

    suspend fun getHistorial(): List<SesionHistorial> {
        if (!TokenManager.isLoggedIn()) {
            throw ExceptionNoRegistrado("Usuario no registrado")
        }

        val response = ApiClient.client.get(
            "${ApiConfig.BASE_URL}/tiradas/correo"
        ) {
            contentType(ContentType.Application.Json)

            header("Authorization", "Bearer ${TokenManager.token.value}")
            header("correo", UserManager.correo.value)
        }

        val listaHistorial: List<SesionHistorial> = response.body()

        return listaHistorial
    }
}