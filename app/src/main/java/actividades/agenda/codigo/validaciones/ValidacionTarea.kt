package actividades.agenda.codigo.validaciones

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.parseadores.fecha.ParseadorFecha
import actividades.agenda.codigo.repositorios.controladores.TareaRepositorio

class ValidacionTarea {
    companion object {
        var tareas: MutableSet<Tarea> = mutableSetOf()

        fun actualizarListaTareas() {
            tareas.addAll(TareaRepositorio.obtenerData())
        }

        // el id debe ser único
        fun obtenerIdUnico(id: Int): Int {
            var esUnico = true

            for(tarea in tareas) {
                if (tarea.id == id) {
                    esUnico = false
                }
            }

            return if(esUnico) id else TareaRepositorio.obtenerNuevoId(tareas = tareas.toList())
        }

        // tiene que haber una hora de inicio y una de fin y tienen que ser válidas
        fun esHorarioValido(fechaInicio: String?, fechaFin: String?): Boolean {
            if (fechaInicio != null && fechaFin != null) {
                val inicio = ParseadorFecha.textoEnAFecha(fechaInicio)
                val fin = ParseadorFecha.textoEnAFecha(fechaFin)
                return inicio.before(fin)
            }

            return false
        }



        // no pueden sobreponerse los horarios nuevos con los ya existentes
        fun estaRangoFechasSuperpuesto(nuevaTarea: Tarea, tareaIdEditar: Int? = null): Boolean {
            var hayConflicto = false
            val nuevaInicio = nuevaTarea.fechaInicio
            val nuevaFin = nuevaTarea.fechaFin

            for (tarea in tareas) {

                if (tarea.id == tareaIdEditar) continue

                val inicioExistente = tarea.fechaInicio
                val finExistente = tarea.fechaFin

                // Verifica si hay conflicto de horarios
                if ((nuevaInicio.before(finExistente) && nuevaFin.after(inicioExistente)) ||
                        nuevaInicio == inicioExistente || nuevaFin == finExistente) {
                    hayConflicto = true
                }
            }
            return hayConflicto
        }
    }
}