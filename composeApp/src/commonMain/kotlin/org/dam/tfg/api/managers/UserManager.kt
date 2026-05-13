package org.dam.tfg.api.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.responses.FederadoResponse
import org.dam.tfg.api.responses.LogInResponse
import org.dam.tfg.api.responses.UsuarioResponse
import org.dam.tfg.dto.ClubBasicoDto
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

    private val _apellidos = MutableStateFlow<String?>(null)
    val apellidos: StateFlow<String?> get() = _apellidos

    private val _numFederado = MutableStateFlow<Int?>(null)
    val numFederado: StateFlow<Int?> get() = _numFederado

    private val _fecNac = MutableStateFlow<LocalDate?>(null)
    val fecNac: StateFlow<LocalDate?> get() = _fecNac

    private val _genero = MutableStateFlow<Genero?>(null)
    val genero: StateFlow<Genero?> get() = _genero

    private var _asociaciones = MutableStateFlow<Map<Asociacion, Int>>(emptyMap())
    val asociaciones: StateFlow<Map<Asociacion, Int>> get() = _asociaciones

    private var _clubesBasico = MutableStateFlow< List<ClubBasicoDto>>(emptyList())
    val clubesBasico: StateFlow<List<ClubBasicoDto>> get() = _clubesBasico

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

    fun setFechaNacimiento(nuevaFecha: LocalDate?) {
        _fecNac.value = nuevaFecha
    }

    fun setCorreoVerificado(verificado: Boolean?) {
        _correoVerificado.value = verificado
    }

    fun setGenero(genero : Genero?){
        _genero.value = genero
    }

    fun setNombre(name : String?){
        _nombre.value = name
    }

    fun setApellidos(apellidos : String?){
        _apellidos.value = apellidos
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

    fun setAsociaciones(asociaciones: Map<Asociacion, Int>){
        _asociaciones.value = asociaciones
    }

    fun setClubesBasico(clubesBasico: List<ClubBasicoDto>){
        _clubesBasico.value = clubesBasico
    }

    fun clear(){
        _correo.value = null;
        _roles.value = null;
        _descripcion.value = null;
        _contrasenya.value = null;
        _fechaAlta.value = null;
        _asociaciones.value = emptyMap<Asociacion, Int>();
        _correoVerificado.value = null;
        _genero.value = null;
        _asociaciones.value = emptyMap<Asociacion, Int>();
        _clubesBasico.value = emptyList<ClubBasicoDto>()
    }

    fun asignarValoresDesdeLogInResponse(body: LogInResponse){
        when (body){
            is UsuarioResponse -> {
                TokenManager.setToken(body.token)
                UserManager.setFechaAlta(body.fechaAlta)
                UserManager.setRoles(body.rol)
                UserManager.setDescripcion(body.descripcion)
                UserManager.setCorreoVerificado(body.correoVerificado)
            }
            is FederadoResponse -> {
                TokenManager.setToken(body.token)
                UserManager.setFechaAlta(body.fechaAlta)
                UserManager.setRoles(body.rol)
                UserManager.setDescripcion(body.descripcion)
                UserManager.setCorreoVerificado(body.correoVerificado)
                UserManager.setAsociaciones(body.asociaciones)
                UserManager.setNombre(body.nombre)
                UserManager.setApellidos(body.apellidos)
                UserManager.setFechaNacimiento(body.fechaNacimiento)
                UserManager.setGenero(body.genero)
                UserManager.setClubesBasico(body.clubes)
            }
        }
    }
}