package actividades.agenda.codigo.repositorios.manejadores

import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.parseadores.fecha.ParseadorFecha
import actividades.agenda.codigo.parseadores.tarea.ParseadorTarea
import android.content.Context
import android.widget.Toast
import java.io.File
import java.util.Date

class ManejadorArchivos(context: Context): IManejadorArchivos {
    private val contexto = context
    // nombres
    private var nombreFichero = "horario-tareas.txt"
    private var nombreCarpeta = "agenda"

    // atributos de control privados
    private var ruta: String = context.getExternalFilesDir(null)?.absolutePath!!
    private var carpeta: File = File(ruta, nombreCarpeta)
    private var fichero: File = File(carpeta, nombreFichero)

    // métodos principales
    override fun crearArchivoSiNoExiste() {
        // Verificar si la carpeta existe
        if (!carpeta.exists()) {
            carpeta.mkdir()  // Crear carpeta
            //enviarMensaje("Se creó una carpeta $nombreCarpeta/")
        }
        // Verificar si el fichero existe
        if (!fichero.exists()) {
            fichero.createNewFile()  // Crear fichero
            //enviarMensaje("Fichero $nombreFichero creado en: ${fichero.absolutePath}")
        }
    }

    override fun leerArchivo(): List<String> {
        crearArchivoSiNoExiste()

        var contenido = ""
        try {
            // Usar el objeto 'fichero' directamente para evitar errores de ruta
            val archivo = fichero.bufferedReader()
            // use cierra el archivo automáticamente
            contenido = archivo.use { it.readText() }
            //enviarMensaje("Contenido recuperado correctamente desde: ${this.nombreFichero}")
        } catch (_: Exception) {
            enviarMensaje("Error al leer el fichero: ${this.nombreFichero}")
        }

        // Filtrar líneas vacías para evitar problemas al parsear
        return contenido.lines().filter { it.isNotBlank() }
    }



    override fun agregarAlArchivo(info: String) {
        if (!carpeta.exists()) crearArchivoSiNoExiste()
        if (info.isNotEmpty()) fichero.appendText("$info\n")

        //enviarMensaje("Se añadió $info a ${this.nombreFichero}")
    }

    override fun quitarDelArchivo(id: Number) {
        val listaTexto: List<String> = this.leerArchivo().toMutableList()
        val listaTareas: MutableList<Tarea> = mutableListOf()

        for(linea in listaTexto) {
            val resultado: Tarea = parseTextoTarea(linea = linea) ?: continue
            val tarea: Tarea = resultado
            if(tarea.id != id) listaTareas.add(tarea)
        }

        val nuevaListaTexto: List<String> = listaTareas.map {
            tarea ->
            parseTareaATexto(tarea = tarea)
        }
        fichero.writeText(nuevaListaTexto.joinToString("\n"))
    }

    override fun modificarDelArchivo(id: Number, info: String) {
        val listaTexto: List<String> = this.leerArchivo().toMutableList()
        // enviarMensaje("modificar: "+listaTexto.joinToString(","))
        val listaTareas: MutableList<Tarea> = mutableListOf()

        for(linea in listaTexto) {
            var resultado: Tarea?
            resultado = parseTextoTarea(linea = linea)
            if(resultado == null) return

            val tarea: Tarea = resultado

            if(tarea.id == id) {
                resultado = parseTextoTarea(linea = info)
                if(resultado == null) return
                listaTareas.add(resultado)
            } else listaTareas.add(tarea)
        }

        val nuevaListaTexto: List<String> = listaTareas.map {
            tarea -> parseTareaATexto(tarea = tarea)
        }
        fichero.writeText(nuevaListaTexto.joinToString("\n"))
    }

    // métodos auxiliares

    private fun enviarMensaje(msg: String) {
        Toast.makeText(this.contexto, msg, Toast.LENGTH_SHORT).show()
    }

    fun parseTextoTarea(linea: String): Tarea? {
        /* La forma normal de una línea promedio luce así:
        * id=1,nombre=nombre tarea,categoria=categoria,fechaInicio=fecha1,fechaFin=fecha2,
        * estado=estado
        * */
        val partes: List<String> = linea.split(",")

        for (parte in partes) {
            if (parte.isEmpty()) return null
        }

        if(!partes[0].startsWith("id=")) return null
        if(!partes[1].startsWith("nombre=")) return null
        if(!partes[2].startsWith("categoria=")) return null
        if(!partes[3].startsWith("fechaInicio=")) return null
        if(!partes[4].startsWith("fechaFin=")) return null
        if(!partes[5].startsWith("estado=")) return null

        val id: Int = partes[0].substring("id=".length).toInt()
        val nombre: String = partes[1].substring("nombre=".length)
        val categoria: Categorias = ParseadorTarea.parseCategorias(
            partes[2].substring("categoria=".length)
        )
        val fechaInicio: Date = ParseadorFecha.textoEnAFecha(
            partes[3].substring("fechaInicio=".length)
        )
        val fechaFin: Date = ParseadorFecha.textoEnAFecha(
            partes[4].substring("fechaFin:".length)
        )
        val estado: Estados = ParseadorTarea.parseEstados(
            partes[5].substring("estado=".length)
        )

        return Tarea(id, nombre, categoria, fechaInicio, fechaFin, estado)
    }

    fun parseTareaATexto(tarea: Tarea): String {
        return "id=${tarea.id}," +
                "nombre=${tarea.nombre}," +
                "categoria=${tarea.categoria}," +
                "fechaInicio=${tarea.fechaInicio}," +
                "fechaFin=${tarea.fechaFin}," +
                "estado=${tarea.estado}\n"
    }

}