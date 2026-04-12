package org.dam.tfg.api

import io.ktor.client.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.header
import io.ktor.http.encodedPath
import io.ktor.http.path
import kotlinx.serialization.json.Json
import org.dam.tfg.api.authorization.TokenManager

object ApiClient {
    val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
}