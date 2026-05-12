package org.dam.tfg.screens.Competicion

import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.model.competiciones.LigaEnviar

/**
 * Esta clase existe para no tener que pasar las lambdas a las clases de las competiciones. Si se pasasen, la app no se
 * podría cerrar ya que las lambdas no son serializables
 * */
object AccionesCompeticion {
    var onParticipar: ((Liga) -> Unit)? = null
    var onDejarParticipar: ((Liga) -> Unit)? = null
    var onEliminarParticipante: ((Liga, String) -> Unit)? = null
    var onGuardarCompeticion: ((LigaEnviar) -> Unit)? = null
    var onCancelarCompeticion: ((Liga) -> Unit)? = null
    var onApuntarTirada: ((Int, Int, List<String>) -> Unit)? = null
}