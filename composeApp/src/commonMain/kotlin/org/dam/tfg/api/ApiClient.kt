package org.dam.tfg.api

import io.ktor.client.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.*
import io.ktor.client.plugins.HttpSend
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

            install(DefaultRequest) {
                url(ApiConfig.BASE_URL)

                header("Content-Type", "application/json")

                header("Authorization", "Bearer ${TokenManager.token}")
            }
        }
    }
}