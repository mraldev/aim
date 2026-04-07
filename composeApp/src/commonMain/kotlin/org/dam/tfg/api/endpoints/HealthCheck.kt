package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig

class HealthCheck {
    suspend fun getHealth(): Boolean {
        try {
            val response = ApiClient.client.get(
                "${ApiConfig.BASE_URL}/health"
            )
//            {
//                ApiConfig.defaultHeaders.forEach { (key, value) ->
//                    headers.append(key, value)
//                }
//            }
            println("RESPONSE")
            val body = response.body<String>()
            //? Deberiamos de devolver el body, pero por ahora solo verificamos el response
            return response.status == HttpStatusCode.OK

        } catch (e: Exception) {
            println("HealthCheck error: ${e.message}")
            return false
        }
    }

}