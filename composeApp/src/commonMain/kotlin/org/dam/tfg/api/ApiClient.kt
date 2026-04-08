package org.dam.tfg.api

import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import kotlinx.serialization.json.Json
import org.dam.tfg.api.authorization.TokenManager

object ApiClient {
    val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            //?Añade el token jwt de forma automática en todas las llamadas, solo si existe
            defaultRequest {
                url(ApiConfig.BASE_URL)
                TokenManager.getToken()?.let { token ->
                    header("Authorization", "Bearer $token")
                }

            }
        }
    }
}