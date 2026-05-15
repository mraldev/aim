package org.dam.tfg.api.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.dam.tfg.api.ApiClient
import org.dam.tfg.api.ApiConfig
import org.dam.tfg.api.authorization.TokenManager
import org.dam.tfg.api.managers.SesionManager
import org.dam.tfg.api.managers.UserManager
import org.dam.tfg.api.responses.ResponseHelper
import org.dam.tfg.crypto.ObjectStore
import org.dam.tfg.dto.CompetirEnviar
import org.dam.tfg.dto.DatosCompeticionDto
import org.dam.tfg.exceptions.ExceptionNoRegistrado
import org.dam.tfg.model.Tirada.SesionEnviar
import org.dam.tfg.model.Tirada.SesionHistorial
import org.dam.tfg.repository.HealthCheckRepository
import kotlinx.serialization.builtins.ListSerializer
import org.dam.tfg.helpers.HelperSerializadorTiradasPorFaltaDeConexion.pairSerializer


class TiradaEndpoint {
    suspend fun registrar(sesionEnviar: SesionEnviar): Boolean {
        return if(SesionManager.datosLiga.value == null) enviarRegistroNormal(sesionEnviar)
        else enviarRegistroCompetitivo(sesionEnviar, SesionManager.datosLiga.value!!)
    }

    suspend fun getHistorial(): List<SesionHistorial> {
        if (!TokenManager.isLoggedIn()) {
            throw ExceptionNoRegistrado("Usuario no registrado")
        }

        val response = ApiClient.client.get(
            "${ApiConfig.BASE_URL}/tiradas/correo"
        ) {
            contentType(ContentType.Application.Json)

            header("Authorization", "Bearer ${TokenManager.token.value}")
            header("correo", UserManager.correo.value)
        }

        val listaHistorial: List<SesionHistorial> = response.body()

        return listaHistorial
    }

    private suspend fun enviarRegistroNormal(sesionEnviar: SesionEnviar): Boolean{
        try {
            val response = ApiClient.client.post(
                "${ApiConfig.BASE_URL}/tiradas/registrar"
            ) {
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")

                setBody(
                    sesionEnviar
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {
            println("Tirada error: ${e.message}")
            val sesionesPendientes: MutableList<SesionEnviar>? =
                ObjectStore.load(
                    "sesiones_pendientes",
                    ListSerializer(SesionEnviar.serializer())
                )?.toMutableList()

            val listaGuardar = mutableListOf<SesionEnviar>()
            sesionesPendientes?.let { listaGuardar.addAll(it) }
            listaGuardar.add(sesionEnviar)

            ObjectStore.save(
                "sesiones_pendientes",
                listaGuardar,
                ListSerializer(SesionEnviar.serializer())
            )
            return false
        }
    }

    suspend fun enviarRegistroCompetitivo(sesionEnviar: SesionEnviar, datosCompeticion: DatosCompeticionDto): Boolean{

        val competirEnviar = CompetirEnviar(
            sesionEnviar.tiradas[0].usuario,
            sesionEnviar.tiradas[0].numDianas,
            sesionEnviar.tiradas[0].numMaxFlechasPorDiana,
            sesionEnviar.tiradas[0].puntuaciones,
            sesionEnviar.tiradas[0].tipoCircuito,
            datosCompeticion.nombreLigaAsociada,
            datosCompeticion.dorsal,
            datosCompeticion.posicion,
            datosCompeticion.patrulla,
            datosCompeticion.estilo,
            datosCompeticion.rangoEdad,
            datosCompeticion.genero
        )

        try {
            val response = ApiClient.client.put(
                "${ApiConfig.BASE_URL}/ligas/competir"
            ) {
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer ${TokenManager.token.value}")

                setBody(
                    competirEnviar
                )
            }

            val exito = ResponseHelper.validarResponse(response)

            return exito

        } catch (e: Exception) {

            val listaPendientes: MutableList<Pair<SesionEnviar, DatosCompeticionDto>>? =
                ObjectStore.load(
                    key = "ligas_pendientes",
                    serializer = ListSerializer(
                        pairSerializer<SesionEnviar, DatosCompeticionDto>()
                    )
                )?.toMutableList()

            val listaGuardar = mutableListOf<Pair<SesionEnviar, DatosCompeticionDto>>()
            listaPendientes?.let { listaGuardar.addAll(it) }
            listaGuardar.add(Pair(sesionEnviar, datosCompeticion))

            ObjectStore.save(
                "ligas_pendientes",
                listaGuardar,
                ListSerializer(
                    pairSerializer<SesionEnviar, DatosCompeticionDto>()
                )
            )

            return false
        }
    }
}