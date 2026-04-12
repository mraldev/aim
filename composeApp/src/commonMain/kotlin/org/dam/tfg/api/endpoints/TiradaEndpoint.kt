package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.TiradaManager
import org.dam.tfg.api.responses.LogInResponse
import org.dam.tfg.model.Tirada.Tirada

class TiradaEndpoint {
    suspend fun registrar(tirada: Tirada): Boolean {
        try {
            val response = ApiClient.client.post(
                "${ApiConfig.BASE_URL}/tiradas/registrar"
            ){
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token}")

                setBody(
                    tirada
                )
            }

            val json = Json { prettyPrint = true }

            println("Tirada JSON:\n${json.encodeToString(tirada)}")

            return true

        } catch (e: Exception) {
            println("Tirada error: ${e.message}")
            return false
        }
    }
}