package org.dam.tfg.api.authorization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.dam.tfg.crypto.CredentialStore
import org.dam.tfg.repository.LoginRepository
import kotlin.time.Clock

//? Un object se instancia cuando se abre la aplicacion, se elimina cuando se cierra, por lo que es un objeto temporal que guarda datos siempre y cuando la aplicacion este abierta, es como localStorage, pero sin persistencia
object TokenManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> get() = _token

    private val _horaDeLogIn = MutableStateFlow<LocalDateTime?>(null)

    fun setToken(jwt: String) {
        _token.value = jwt
        _horaDeLogIn.value = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun clear() {
        _token.value = null
    }

    suspend fun isLoggedIn(): Boolean {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()

        val loginInstant = _horaDeLogIn.value
            ?.toInstant(timeZone)

        val sessionValid = loginInstant?.let {
            now < it.plus(6, DateTimeUnit.HOUR, timeZone)
        } ?: false

        if (!sessionValid) {
            val creds = CredentialStore.load()
            if (creds != null) {
                val success = LoginRepository().login(creds.correo, creds.contrasenya)
                if (success) {
                    return true
                }
            }
        }

        return _token.value != null && sessionValid
    }
}