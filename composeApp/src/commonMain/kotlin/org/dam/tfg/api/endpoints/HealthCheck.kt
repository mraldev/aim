package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.*
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.responses.HealthResponse

class HealthCheck {
    suspend fun getHealth(): String {
        try {
            val response = ApiClient.client.get(
                "${ApiConfig.BASE_URL}/health"
            )
//            {
//                ApiConfig.defaultHeaders.forEach { (key, value) ->
//                    headers.append(key, value)
//                }
//            }
            val body = response.body<HealthResponse>()
            return body.status

        } catch (e: Exception) {
            println("HealthCheck error: ${e.message}")
            return "DOWN"
        }
    }

}