package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam.tfg.dto.DatosCompeticionDto
import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.model.Tirada.SesionEnviar

object SesionManager {

    private val _sesionEnviar = MutableStateFlow<SesionEnviar?>(null)
    val sesionEnviar: StateFlow<SesionEnviar?> get() = _sesionEnviar

    private val _datosLiga = MutableStateFlow<DatosCompeticionDto?>(null)
    val datosLiga: StateFlow<DatosCompeticionDto?> get() = _datosLiga

    fun setSesion(nuevaSesionEnviar: SesionEnviar) {
        _sesionEnviar.value = nuevaSesionEnviar
    }

    fun setDatosLiga(nuevosDatosLiga: DatosCompeticionDto){
        _datosLiga.value = nuevosDatosLiga
    }

    fun clear() {
        _sesionEnviar.value = null
        _datosLiga.value = null
    }
}