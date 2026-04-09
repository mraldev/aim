package org.dam.tfg.api.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(val token: String)
