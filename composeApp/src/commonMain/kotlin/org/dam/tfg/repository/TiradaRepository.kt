package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiTiradaController
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.SesionHistorial

class TiradaRepository(private val api: ApiTiradaController = ApiTiradaController()
)
{
    suspend fun registrar(sesionEnviar: SesionEnviar): Boolean {
        return api.registrarTirada(sesionEnviar)
    }

    /**
     * Con normales me refiero a realizadas fuera de una competición
     **/
    suspend fun historialTiradasNormales(): List<SesionHistorial> {
        return api.getHistorial()
    }
}