package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiLigaController
import org.dam.tfg.dto.LigaCompletoDto
import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.model.competiciones.LigaEnviar

class LigaRepository(private val api: ApiLigaController = ApiLigaController()) {

    suspend fun registrar(ligaEnviar: LigaEnviar): Boolean {
        return api.registrarLiga(ligaEnviar)
    }

    suspend fun getCompeticionesByCorreo(): List<LigaPreview> {
        return api.obtenerLigasByCorreo()
    }

    suspend fun getCompeticiones(): List<LigaCompletoDto> {
        return api.obtenerLigas()
    }

    suspend fun invalidar(clave: String, correo: String): List<LigaCompletoDto> {
        return api.invalidar(clave, correo)
    }
}