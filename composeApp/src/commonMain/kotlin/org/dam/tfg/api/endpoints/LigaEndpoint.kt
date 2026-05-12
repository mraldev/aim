package org.dam.tfg.api.endpoints

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.responses.ResponseHelper
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.model.competiciones.LigaEnviar

class LigaEndpoint{
    suspend fun registrar(ligaEnviar: LigaEnviar): Boolean {
        try {
            val response = ApiClient.client.post(
                "${ApiConfig.BASE_URL}/ligas/registrar"
            ) {
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")

                setBody(
                    ligaEnviar
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Tirada error: ${e.message}")
            return false
        }
    }
}
