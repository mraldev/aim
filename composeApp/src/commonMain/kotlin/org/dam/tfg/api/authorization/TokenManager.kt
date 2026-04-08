package org.dam.tfg.api.authorization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TokenManager {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    fun setToken(jwt: String) {
        _token.value = jwt
    }

    fun getToken(): String? {
        return _token.value
    }

    fun clear() {
        _token.value = null
    }

    val isLoggedIn: Boolean
        get() = _token.value != null
}