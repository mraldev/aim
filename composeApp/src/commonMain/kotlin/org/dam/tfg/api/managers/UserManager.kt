package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.responses.LogInResponse
import org.dam.tfg.crypto.CredentialStore
import org.dam.tfg.enums.Asociacion
import org.dam.tfg.enums.Genero
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

    private var _correoVerificado = MutableStateFlow<Boolean?>(null)
    val correoVerificado: StateFlow<Boolean?> get() = _correoVerificado

    private val _nombre = MutableStateFlow<String?>(null)
    val nombre: StateFlow<String?> get() = _nombre

    private val _numFederado = MutableStateFlow<Int?>(null)
    val numFederado: StateFlow<Int?> get() = _numFederado

    private val _fecNac = MutableStateFlow<LocalDate?>(null)
    val fecNac: StateFlow<LocalDate?> get() = _fecNac

    private val _genero = MutableStateFlow<Genero?>(null)
    val genero: StateFlow<Genero?> get() = _genero

    private var _asociaciones = MutableStateFlow<Map<Asociacion, Int>>(emptyMap())
    val asociaciones: StateFlow<Map<Asociacion, Int>> get() = _asociaciones

    fun setCorreo(nuevoCorreo: String?) {
        _correo.value = nuevoCorreo
    }

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

    fun setFechaNac(nuevaFecha: LocalDate?) {
        _fecNac.value = nuevaFecha
    }

    fun setCorreoVerificado(verificado: Boolean?) {
        _correoVerificado.value = verificado
    }

    fun setGenero(genero : Genero?){
        _genero.value = genero
    }

    fun setName(name : String?){
        _nombre.value = name
    }

    fun setNumFed(num : Int?){
        _numFederado.value = num
    }

    fun setDescripcion(nuevaDescripcion: String?) {
        nuevaDescripcion?.let {
            _descripcion.value = it
        }
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
        _correoVerificado.value = null;
    }

    fun asignarValoresDesdeLogInResponse(body: LogInResponse){
        TokenManager.setToken(body.token)
        UserManager.setFechaAlta(body.fechaAlta)
        UserManager.setRoles(body.rol)
        UserManager.setDescripcion(body.descripcion)
        UserManager.setCorreoVerificado(body.correoVerificado)
        CredentialStore.clear()
    }
}