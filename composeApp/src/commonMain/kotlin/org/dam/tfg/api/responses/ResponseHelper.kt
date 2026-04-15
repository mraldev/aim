package org.dam.tfg.api.responses

import io.ktor.client.statement.HttpResponse

object ResponseHelper {
    fun validarResponse(response: HttpResponse): Boolean{
        return ((response.status.value >= 200) && (response.status.value <= 299))
    }
}