package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.HealthCheck

class ApiHealthController {
    private val healthCheck = HealthCheck()

    suspend fun getHealth(): String {
        return healthCheck.getHealth()
    }
}