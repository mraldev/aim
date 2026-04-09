package org.dam.tfg.api.authorization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import org.dam.tfg.api.enumerados.RolesUsuario


object UserManager {

    private val _correo = MutableStateFlow<String?>(null)
    val correo: StateFlow<String?> get() = _correo

    //!Encriptada siempre
    private val _contrasenya = MutableStateFlow<String?>(null)

    /**Contraseña ya encriptada*/
    val contrasenya: StateFlow<String?> get() = _contrasenya

    private val _fechaAlta = MutableStateFlow<LocalDate?>(null)
    val fechaAlta: StateFlow<LocalDate?> get() = _fechaAlta

    private val _roles = MutableStateFlow<List<RolesUsuario>>(emptyList())
    val roles: StateFlow<List<RolesUsuario>> get() = _roles

    private val _descripcion = MutableStateFlow<String?>(null)
    val descripcion: StateFlow<String?> get() = _descripcion

    fun setCorreo(nuevoCorreo: String?) {
        _correo.value = nuevoCorreo
    }

    //TODO cifrarla al setearla
    fun setContrasenya(nuevaContrasenya: String?) {
        _contrasenya.value = nuevaContrasenya
    }


    fun getFechaAlta(): LocalDate? {
        return _fechaAlta.value
    }

    fun setFechaAlta(nuevaFecha: LocalDate?) {
        _fechaAlta.value = nuevaFecha
    }

    fun setRoles(nuevosRoles: List<RolesUsuario>) {
        _roles.value = nuevosRoles
    }

    fun setDescripcion(nuevaDescripcion: String?) {
        _descripcion.value = nuevaDescripcion
    }
}