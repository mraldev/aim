package org.dam.tfg.api

import org.dam.tfg.api.endpoints.HealthCheck

class ApiController {
    private val healthCheck = HealthCheck()

    suspend fun getHealth(): String {
        return healthCheck.getHealth()
    }
}