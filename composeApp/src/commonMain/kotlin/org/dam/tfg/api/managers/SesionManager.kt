package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam.tfg.model.Tirada.Sesion
import org.dam.tfg.model.Tirada.Tirada

object SesionManager {

    private val _sesion = MutableStateFlow<Sesion?>(null)

    val sesion: StateFlow<Sesion?> get() = _sesion

    //? Set cada vez que se cambia el valor de sesion -> actualiza la tirada
    fun setSesion(nuevaSesion: Sesion) {
        _sesion.value = nuevaSesion
    }

    fun clear() {
        _sesion.value = null
    }
}