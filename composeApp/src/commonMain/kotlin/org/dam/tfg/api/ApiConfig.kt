package org.dam.tfg.api

object ApiConfig {

    //* Esta es la api que usaremos en produccion
//    const val BASE_URL = "http://137.101.88.89:6767/api"
    //?  Cambiar a la api necesaria (en mi caso la que tiene //!)
    const val BASE_URL = "http://192.168.1.101:6767/api"


    //! Esto deberia de ser un setter con lo que de devuelve /login, defaultheaders deberia de autocompletarse automaticamente cuando este exista

//    val defaultHeaders = mapOf(
//        "Authorization" to "Bearer + jwt",
//    )

}