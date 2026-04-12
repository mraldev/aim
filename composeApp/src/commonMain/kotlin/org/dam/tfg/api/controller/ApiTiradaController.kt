package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.TiradaEndpoint
import org.dam.tfg.model.Tirada.Tirada

class ApiTiradaController {
    private val endpoint = TiradaEndpoint()

    suspend fun registrarTirada(tirada: Tirada): Boolean {
        return endpoint.registrar(tirada)
    }
}