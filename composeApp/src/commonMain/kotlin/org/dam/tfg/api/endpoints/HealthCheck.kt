package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.*
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.responses.HealthResponse
import org.dam.tfg.api.responses.ResponseHelper

class HealthCheck {
    suspend fun getHealth(): String {
        try {
            val response = ApiClient.client.get(
                "${ApiConfig.BASE_URL}/health"
            )

            val exito = ResponseHelper.validarResponse(response)

            return if (exito) {
                response.body<HealthResponse>().health
            } else {
                "DOWN"
            }

        } catch (e: Exception) {
            println("HealthCheck error: ${e.message}")
            return "DOWN"
        }
    }
}