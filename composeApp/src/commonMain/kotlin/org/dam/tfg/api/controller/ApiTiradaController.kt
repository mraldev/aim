package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.TiradaEndpoint
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.SesionHistorial

class ApiTiradaController {
    private val endpoint = TiradaEndpoint()

    suspend fun registrarTirada(sesionEnviar: SesionEnviar): Boolean {
        return endpoint.registrar(sesionEnviar)
    }

    suspend fun getHistorial(): List<SesionHistorial> {
        return endpoint.getHistorial()
    }
}