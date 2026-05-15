package org.dam.tfg.api.controller

import org.dam.tfg.api.endpoints.LigaEndpoint
import org.dam.tfg.dto.LigaCompletoDto
import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.model.competiciones.LigaEnviar

class ApiLigaController {

    private val endpoint = LigaEndpoint()

    suspend fun registrarLiga(ligaEnviar: LigaEnviar): Boolean {
        return endpoint.registrar(ligaEnviar)
    }

    suspend fun obtenerLigasByCorreo(): List<LigaPreview> {
        return endpoint.getCompeticionesByCorreo()
    }

    suspend fun obtenerLigas(): List<LigaCompletoDto> {
        return endpoint.getCompeticiones()
    }

    suspend fun invalidar(clave: String, correo: String): List<LigaCompletoDto> {
        return endpoint.invalidarTirada(clave, correo)
    }
}