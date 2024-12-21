package actividades.agenda.codigo.repositorios.manejadores.textos

import actividades.agenda.codigo.modelos.tarea.Tarea
import android.content.Context

interface ITextoTareaDatos {
    fun crearTarea(tarea: Tarea)
    fun modificarTarea(tarea: Tarea)
    fun leerTarea(contexto: Context): MutableMap<Int, Tarea>?
    fun eliminarTarea(contexto: Context, id: Number)
}