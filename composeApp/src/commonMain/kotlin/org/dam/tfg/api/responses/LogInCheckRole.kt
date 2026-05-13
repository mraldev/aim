package org.dam.tfg.api.responses

import kotlinx.serialization.Serializable
import org.dam.tfg.enums.UserRole

@Serializable
data class LogInCheckRole(val rol: UserRole){
}