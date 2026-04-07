package org.dam.tfg.repository

import org.dam.tfg.api.ApiController

class HealthCheckRepository (
    private val api: ApiController = ApiController()
    )
     {
        suspend fun getHealthStatus(): String {
            return api.getHealth()
        }
    }
