package org.dam.tfg.helpers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.serializer

object HelperSerializadorTiradasPorFaltaDeConexion {
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified A, reified B> pairSerializer(): KSerializer<Pair<A, B>> {
        return PairSerializer(
            serializer<A>(),
            serializer<B>()
        )
    }
}