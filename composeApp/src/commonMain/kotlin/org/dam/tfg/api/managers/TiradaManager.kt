package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam.tfg.model.Tirada.Tirada

object TiradaManager {

    private val _tirada = MutableStateFlow<Tirada?>(null)

    val tirada: StateFlow<Tirada?> get() = _tirada

    //? Set cada vez que se cambia el valor de tirada -> actualiza la tirada
    fun setTirada(nuevaTirada: Tirada) {
        _tirada.value = nuevaTirada
    }

    fun clear() {
        _tirada.value = null
    }
}