package actividades.agenda.codigo.modelos.tarea

import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import java.util.Date

data class Tarea(
    val id: Int,
    var nombre: String,
    var categoria: Categorias,
    var fechaInicio: Date,
    var fechaFin: Date,
    var estado: Estados
)
