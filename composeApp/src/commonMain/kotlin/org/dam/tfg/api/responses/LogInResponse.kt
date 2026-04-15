package org.dam.tfg.api.responses

import kotlinx.serialization.Serializable

@Serializable
data class LogInResponse(val token: String)