package actividades.agenda.codigo.repositorios.controladores

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper

interface ITareaRepositorio {
    fun crearTarea(dbHelper: DBHelper, tarea: Tarea)
    fun modificarTarea(dbHelper: DBHelper, tarea: Tarea)
    fun eliminarTarea(dbHelper: DBHelper, id: Int)
    fun buscarTarea(dbHelper: DBHelper, query: String): Map<Int, Tarea>
    fun buscarTareaId(dbHelper: DBHelper, id: Int): Tarea
    abstract fun agregarTarea(dbHelper: DBHelper, tarea: Tarea)
}