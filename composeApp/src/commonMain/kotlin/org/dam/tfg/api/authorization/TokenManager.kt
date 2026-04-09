package org.dam.tfg.api.authorization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TokenManager {

    private val _token = MutableStateFlow<String?>(null)
    val token: String? get() = _token.value

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