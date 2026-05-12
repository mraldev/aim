package org.dam.tfg.repository

import org.dam.tfg.api.controller.ApiLigaController
import org.dam.tfg.model.competiciones.LigaEnviar

class LigaRepository(private val api: ApiLigaController = ApiLigaController()) {

    suspend fun registrar(ligaEnviar: LigaEnviar): Boolean {
        return api.registrarLiga(ligaEnviar)
    }
}