package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam.tfg.model.Tirada.SesionEnviar

object SesionManager {

    private val _sesionEnviar = MutableStateFlow<SesionEnviar?>(null)

    val sesionEnviar: StateFlow<SesionEnviar?> get() = _sesionEnviar

    //? Set cada vez que se cambia el valor de sesion -> actualiza la tirada
    fun setSesion(nuevaSesionEnviar: SesionEnviar) {
        _sesionEnviar.value = nuevaSesionEnviar
    }

    fun clear() {
        _sesionEnviar.value = null
    }
}