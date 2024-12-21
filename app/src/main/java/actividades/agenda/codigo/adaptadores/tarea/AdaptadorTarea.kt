package actividades.agenda.codigo.adaptadores.tarea

import actividades.agenda.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.parseadores.fecha.ParseadorFecha
import actividades.agenda.codigo.parseadores.string.ParseadorString
import android.annotation.SuppressLint
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout

class AdaptadorTarea(
    private var items: MutableList<Tarea>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit

): RecyclerView.Adapter<AdaptadorTarea.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actividadNombre: TextView = view.findViewById(R.id.filaActividadNombre)
        val actividadPrioridad: TextView = view.findViewById(R.id.filaActividadPrioridad)
        val actividadFechaInicio: TextView = view.findViewById(R.id.filaActividadInicio)
        val actividadFechaFin: TextView = view.findViewById(R.id.filaActividadFin)
        private val actividadEditar: ImageButton = view.findViewById(R.id.filaEditar)
        private val actividadEliminar: ImageButton = view.findViewById(R.id.filaEliminar)
        val filaLayout: ConstraintLayout = view.findViewById(R.id.filaConstraintLayout)

        init {
            actividadEditar.setOnClickListener {
                onEdit(adapterPosition)
            }

            actividadEliminar.setOnClickListener {
                onDelete(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fila_actividad_agenda, parent, false)
        return ViewHolder(view)
    }

    // Vincula los datos del item a la vista
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarea = items[position]
        holder.actividadNombre.text = ParseadorString.parseEnumText(tarea.nombre)
        holder.actividadPrioridad.text = ParseadorString.parseEnumText(tarea.categoria.toString())
        holder.actividadFechaInicio.text = ParseadorFecha.fechaATextoEs(tarea.fechaInicio)
        holder.actividadFechaFin.text = ParseadorFecha.fechaATextoEs(tarea.fechaFin)

        // Configurar fondo basado en la categoría de prioridad
        holder.filaLayout.setBackgroundResource(
            when (tarea.categoria) {
                Categorias.BAJA_PRIORIDAD -> R.drawable.fondo_1
                Categorias.MEDIA_PRIORIDAD -> R.drawable.fondo_2
                Categorias.ALTA_PRIORIDAD -> R.drawable.fondo_3
            }
        )
    }

    // Retorna el tamaño de la lista
    override fun getItemCount(): Int = items.size

    // Eliminar ítem
    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    // Método para actualizar la lista de tareas y refrescar la vista

    @SuppressLint("NotifyDataSetChanged")
    fun updateTasks(nuevasTareas: List<Tarea>) {
        items = nuevasTareas.toMutableList() // Actualizamos la lista de tareas
        notifyDataSetChanged() // Notificamos al adaptador que los datos han cambiado
    }
}
