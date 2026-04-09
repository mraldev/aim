package org.dam.tfg.model

import org.dam.tfg.api.enumerados.RolesUsuario

data class Users
    (
        val correo: String,
        val contrasenya: String,
        val name: String?,
        val descripcion: String?,
        val rolesUsuario: List<RolesUsuario>,
    )
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Users

        //? cambiar a correo electronico cuando lo implementemos

        if (rolesUsuario != other.rolesUsuario) return false
        if (correo != other.correo) return false
        if (contrasenya != other.contrasenya) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rolesUsuario.hashCode()
        result = 31 * result + correo.hashCode()
        result = 31 * result + contrasenya.hashCode()
        result = 31 * result + descripcion.hashCode()
        return result
    }

    override fun toString(): String {
        return "usuario='$correo', descripcion=$descripcion, admin=$rolesUsuario"
    }


}