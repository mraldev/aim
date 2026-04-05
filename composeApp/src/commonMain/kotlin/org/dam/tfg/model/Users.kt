package org.dam.tfg.model

data class Users
    (
        val usuario: String,
        val contrasenya: String,
        val name: String?,
        val descripcion: String,
        val admin: Boolean,
    )
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Users

        //? cambiar a correo electronico cuando lo implementemos

        if (admin != other.admin) return false
        if (usuario != other.usuario) return false
        if (contrasenya != other.contrasenya) return false

        return true
    }

    override fun hashCode(): Int {
        var result = admin.hashCode()
        result = 31 * result + usuario.hashCode()
        result = 31 * result + contrasenya.hashCode()
        result = 31 * result + descripcion.hashCode()
        return result
    }

    override fun toString(): String {
        return "usuario='$usuario', descripcion=$descripcion, admin=$admin"
    }


}