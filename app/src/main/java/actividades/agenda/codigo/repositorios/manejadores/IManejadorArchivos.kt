package actividades.agenda.codigo.repositorios.manejadores

interface IManejadorArchivos {
    fun crearArchivoSiNoExiste()
    fun leerArchivo(): List<String>
    fun agregarAlArchivo(info: String)
    fun quitarDelArchivo(id: Number)
    fun modificarDelArchivo(id: Number, info: String)
}