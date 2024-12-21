package actividades.agenda.codigo.conexiones

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import com.google.gson.annotations.SerializedName
import java.util.Date


data class TareaResponse(
    @SerializedName("taskList") var listaTarea:ArrayList<Tarea>)
