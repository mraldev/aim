package org.dam.tfg.api

import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*

object ApiClient {
    val client: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}