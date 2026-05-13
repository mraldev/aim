package org.dam.tfg.screens.Competicion

import org.dam.tfg.dto.LigaPreview
import org.dam.tfg.model.competiciones.Liga
import org.dam.tfg.model.competiciones.LigaEnviar

/**
 * Esta clase existe para no tener que pasar las lambdas a las clases de las competiciones. Si se pasasen, la app no se
 * podría cerrar ya que las lambdas no son serializables
 * */
object AccionesCompeticion {
    var onParticipar: ((LigaPreview) -> Unit)? = null
    var onDejarParticipar: ((LigaPreview) -> Unit)? = null
    var onEliminarParticipante: ((LigaPreview, String) -> Unit)? = null
    var onGuardarCompeticion: ((LigaEnviar) -> Unit)? = null
    var onCancelarCompeticion: ((LigaPreview) -> Unit)? = null
    var onApuntarTirada: ((Int, Int, List<String>) -> Unit)? = null
}