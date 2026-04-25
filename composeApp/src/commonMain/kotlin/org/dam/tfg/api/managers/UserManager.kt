package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.UserRole


object UserManager {

    private val _correo = MutableStateFlow<String?>(null)
    val correo: StateFlow<String?> get() = _correo

    //!Encriptada siempre
    private val _contrasenya = MutableStateFlow<String?>(null)

    /**Contraseña ya encriptada*/
    val contrasenya: StateFlow<String?> get() = _contrasenya

    private val _fechaAlta = MutableStateFlow<LocalDate?>(null)
    val fechaAlta: StateFlow<LocalDate?> get() = _fechaAlta

    private val _roles = MutableStateFlow<UserRole?>(null)
    val roles: StateFlow<UserRole?> get() = _roles

    private val _descripcion = MutableStateFlow<String?>(null)
    val descripcion: StateFlow<String?> get() = _descripcion

    private var _asociaciones = MutableStateFlow<Map<Asociacion, Int>>(emptyMap())
    val asociaciones: StateFlow<Map<Asociacion, Int>> get() = _asociaciones

    fun setCorreo(nuevoCorreo: String?) {
        _correo.value = nuevoCorreo
    }

    //TODO cifrarla al setearla
    fun setContrasenya(nuevaContrasenya: String) {
        _contrasenya.value = nuevaContrasenya
    }

    fun getFechaAlta(): LocalDate? {
        return _fechaAlta.value
    }

    fun setFechaAlta(nuevaFecha: LocalDate?) {
        _fechaAlta.value = nuevaFecha
    }

    fun setRoles(nuevoRol: UserRole) {
        _roles.value = nuevoRol
    }

    fun setDescripcion(nuevaDescripcion: String?) {
        _descripcion.value = nuevaDescripcion
    }

    fun setAsociacion(asociacion: Asociacion, numAsociado: Int){
        _asociaciones.value = _asociaciones.value.toMutableMap().apply {
            this[asociacion] = numAsociado
        }
    }

    fun clear(){
        _correo.value = null;
        _roles.value = null;
        _descripcion.value = null;
        _contrasenya.value = null;
        _fechaAlta.value = null;
        _asociaciones.value = emptyMap<Asociacion, Int>();
    }
}