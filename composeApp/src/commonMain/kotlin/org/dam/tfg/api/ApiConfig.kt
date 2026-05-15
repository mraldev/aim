package org.dam.tfg.api

object ApiConfig {

    //* Esta es la api que usaremos en produccion
    const val apiProd = "https://viscosity-amperage-cozily.ngrok-free.dev/api"
    const val apiLocal = "http://10.0.2.2:8443/api"
    //?  Cambiar a la api necesaria (10.0.2.2 es una dirección especial que apunta a tu máquina anfitrión)
    const val BASE_URL = apiLocal
//    const val BASE_URL = apiProd

}