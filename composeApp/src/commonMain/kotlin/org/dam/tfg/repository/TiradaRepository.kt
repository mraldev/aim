package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiAuthController
import org.dam.tfg.api.controller.ApiTiradaController
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.model.Tirada.Tirada

class TiradaRepository(private val api: ApiTiradaController = ApiTiradaController()
)
{
    suspend fun registrar(sesion: Sesion): Boolean {
        return api.registrarTirada(sesion)
    }
}