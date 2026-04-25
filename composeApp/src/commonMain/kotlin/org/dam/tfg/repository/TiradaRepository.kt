package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiTiradaController
import org.dam.tfg.model.Tirada.SesionEnviar

class TiradaRepository(private val api: ApiTiradaController = ApiTiradaController()
)
{
    suspend fun registrar(sesionEnviar: SesionEnviar): Boolean {
        return api.registrarTirada(sesionEnviar)
    }
}