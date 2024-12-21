package actividades.agenda.codigo.parseadores.tarea

import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.parseadores.string.ParseadorString


class ParseadorTarea {
    companion object {
        fun parseCategoriasPosicion(categorias: Categorias): Int {
            return when(categorias) {
                Categorias.BAJA_PRIORIDAD -> 0
                Categorias.MEDIA_PRIORIDAD -> 1
                Categorias.ALTA_PRIORIDAD -> 2
            }
        }

        fun parseCategorias(texto: String): Categorias {
            val t = ParseadorString.parseEnumText(texto)
            return when(t) {
                "Baja prioridad" -> Categorias.BAJA_PRIORIDAD
                "Media prioridad" -> Categorias.MEDIA_PRIORIDAD
                "Alta prioridad" -> Categorias.ALTA_PRIORIDAD
                else -> Categorias.BAJA_PRIORIDAD
            }
        }

        fun parseEstadosPosicion(estados: Estados): Int {
            return when(estados) {
                Estados.PENDIENTE -> 0
                Estados.COMPLETADO -> 1
                Estados.VENCIDO -> 2
            }
        }

        fun parseEstados(texto: String): Estados {
            val t = ParseadorString.parseEnumText(texto)
            return when(t) {
                "Pendiente" -> Estados.PENDIENTE
                "Completado" -> Estados.COMPLETADO
                "Vencido" -> Estados.VENCIDO
                else -> Estados.PENDIENTE
            }
        }
    }
}