package actividades.agenda.codigo.repositorios.manejadores.textos

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.repositorios.manejadores.ManejadorArchivos
import android.annotation.SuppressLint
import android.content.Context

class TextoTareaDatos{
    companion object: ITextoTareaDatos {
        @SuppressLint("StaticFieldLeak")
        private lateinit var manejadorArchivos: ManejadorArchivos

        override fun crearTarea(tarea: Tarea) {
            manejadorArchivos.crearArchivoSiNoExiste()
            manejadorArchivos.agregarAlArchivo(info= manejadorArchivos.parseTareaATexto(tarea))
        }

        override fun modificarTarea(tarea: Tarea) {
            manejadorArchivos.modificarDelArchivo(
                id=tarea.id,
                info= manejadorArchivos.parseTareaATexto(tarea)
            )
        }

        override fun leerTarea(contexto: Context): MutableMap<Int, Tarea> {
            inicializarManejadorArchivos(context = contexto)
            val res: List<String> = manejadorArchivos.leerArchivo()
            // tengo que convertir List<String> en MutableMap<Int, Tarea> y no incluir valores null

            val tareasMutables: MutableMap<Int, Tarea> = mutableMapOf()

            for (linea in res) {
                // Revisar el operador elvis, básicamente evalúa y devuelve un valor por defecto
                val resultado: Tarea = manejadorArchivos.parseTextoTarea(linea = linea) ?: continue
                tareasMutables[resultado.id] = resultado
            }

            return tareasMutables
        }

        override fun eliminarTarea(contexto: Context, id: Number) {
            inicializarManejadorArchivos(context = contexto)
            manejadorArchivos.quitarDelArchivo(id = id)
        }

        fun inicializarManejadorArchivos(context: Context) {
            if (!(Companion::manejadorArchivos.isInitialized)) {
                manejadorArchivos = ManejadorArchivos(context = context)
            }
        }

    }
}