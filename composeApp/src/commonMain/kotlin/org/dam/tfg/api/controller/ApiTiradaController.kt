package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.TiradaEndpoint
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.model.Tirada.Tirada

class ApiTiradaController {
    private val endpoint = TiradaEndpoint()

    suspend fun registrarTirada(sesion: Sesion): Boolean {
        return endpoint.registrar(sesion)
    }
}