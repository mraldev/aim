package org.dam.tfg.helpers

import org.dam.tfg.dto.InfoTiradaPorCircuito
import org.dam.tfg.enums.TipoCircuito

object HelperCargadorDeTipoDeTirada {
    fun cargarDatos(tipoCircuito: TipoCircuito): InfoTiradaPorCircuito {
        when (tipoCircuito){
            TipoCircuito.STANDARD -> return InfoTiradaPorCircuito(24, 2)
            else -> return InfoTiradaPorCircuito(24, 2)
        }
    }
}