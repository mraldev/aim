package org.dam.tfg.api.authorization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//? Un object se instancia cuando se abre la aplicacion, se elimina cuando se cierra, por lo que es un objeto temporal que guarda datos siempre y cuando la aplicacion este abierta, es como localStorage, pero sin persistencia
object TokenManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> get() = _token

    fun setToken(jwt: String) {
        _token.value = jwt
    }

    fun clear() {
        _token.value = null
    }

    //TODO validar si la sesión está activa
    val isLoggedIn: Boolean
        get() = _token.value != null
}