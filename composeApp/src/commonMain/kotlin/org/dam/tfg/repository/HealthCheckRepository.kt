package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiHealthController

class HealthCheckRepository (
    private val api: ApiHealthController = ApiHealthController()
    )
     {
        suspend fun getHealthStatus(): String {
            return api.getHealth()
        }
    }
