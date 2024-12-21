package actividades.agenda.codigo.repositorios.controladores

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper
import actividades.agenda.codigo.repositorios.dbAgenda.DBTarea
import actividades.agenda.codigo.repositorios.manejadores.textos.TextoTareaDatos
import android.content.Context

class TareaRepositorio(context: Context): ITareaRepositorio {
    // variable de control del repositorio
    var contexto = context

    override fun crearTarea(dbHelper: DBHelper, tarea: Tarea) {
        if(cacheData[tarea.id] == null) {
            cacheData[tarea.id] = tarea
            TextoTareaDatos.inicializarManejadorArchivos(context = contexto)
            TextoTareaDatos.crearTarea(tarea = tarea)
            DBTarea.insertarTarea(dbHelper = dbHelper, tarea = tarea)
        }
    }

    override fun modificarTarea(dbHelper: DBHelper, tarea: Tarea) {
        if(cacheData[tarea.id] == null) crearTarea(dbHelper = dbHelper, tarea = tarea)
        else {
            cacheData[tarea.id] = tarea
            TextoTareaDatos.inicializarManejadorArchivos(context = contexto)
            TextoTareaDatos.modificarTarea(tarea = tarea)
            DBTarea.actualizarTarea(dbHelper = dbHelper, tarea = tarea)
        }
    }
    //--------------------------------------------------------------


    override fun buscarTarea(dbHelper: DBHelper, query: String): Map<Int, Tarea> {
        TextoTareaDatos.inicializarManejadorArchivos(context = contexto)
        val txtCacheData = TextoTareaDatos.leerTarea(contexto = contexto)
        val bbddCache = DBTarea.obtenerTodasLasTareas(dbHelper = dbHelper)
        // convierte un iterable (colección simple de tipo genérico) a un map genérico
        val bbddCacheData = bbddCache.associateBy { it.id }
        cacheData.putAll(txtCacheData)
        cacheData.putAll(bbddCacheData)

        if(query.isEmpty()) { return cacheData.toMap() }

        return cacheData.filter { (_, t) ->
            val nombreRes = this.partialSearchFor(t.nombre, query)
            val categoriaRes = this.partialSearchFor("${t.categoria}", query)
            val fechaInicioRes = this.partialSearchFor("${t.fechaInicio}", query)
            val fechaFinRes = this.partialSearchFor("${t.fechaFin}", query)
            val estadoRes = this.partialSearchFor("${t.estado}", query)

            nombreRes || categoriaRes || fechaInicioRes || fechaFinRes || estadoRes
        }
    }

    override fun buscarTareaId(dbHelper: DBHelper, id: Int): Tarea {
        TextoTareaDatos.inicializarManejadorArchivos(context = contexto)
        val txtCacheData = TextoTareaDatos.leerTarea(contexto = contexto)
        val bbddCache = DBTarea.obtenerTodasLasTareas(dbHelper = dbHelper)
        // convierte un iterable (colección simple de tipo genérico) a un map genérico
        val bbddCacheData = bbddCache.associateBy { it.id }
        cacheData.putAll(txtCacheData)
        cacheData.putAll(bbddCacheData)

        return cacheData.values.filter { tarea -> tarea.id == id }[0]
    }

    override fun eliminarTarea(dbHelper: DBHelper, id: Int) {
        TextoTareaDatos.inicializarManejadorArchivos(context = contexto)
        TextoTareaDatos.eliminarTarea(contexto = contexto, id = id)
        DBTarea.eliminarTarea(dbHelper = dbHelper, tareaId = id)

        if(cacheData.isEmpty()) return
        if(cacheData[id] == null) return

        cacheData.remove(id)
    }

    // métodos auxiliares

    fun partialSearchFor(attr: String, query: String): Boolean {
        // no toma en consideración mayúsculas o minúsculas
        return attr.contains(query, ignoreCase = true)
    }

    companion object {
        var cacheData: MutableMap<Int, Tarea> = mutableMapOf()

        fun obtenerData(): Set<Tarea> {
            return cacheData.values.toSet()
        }

        fun obtenerNuevoId(tareas: List<Tarea>): Int {
            // notar que el menor id posible por generar es 0
            var idMayor = -1
            for (tarea in tareas) if (idMayor < tarea.id) idMayor = tarea.id

            return idMayor + 1
        }
    }
    // Métodos de la interfaz
    override fun agregarTarea(dbHelper: DBHelper, tarea: Tarea) {
        crearTarea(dbHelper, tarea) // Puedes reutilizar el método crearTarea
    }
}



