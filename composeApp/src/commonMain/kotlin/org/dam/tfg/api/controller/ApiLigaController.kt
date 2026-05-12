package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.LigaEndpoint
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.competiciones.LigaEnviar

class ApiLigaController {

    private val endpoint = LigaEndpoint()

    suspend fun registrarLiga(ligaEnviar: LigaEnviar): Boolean {
        return endpoint.registrar(ligaEnviar)
    }
}