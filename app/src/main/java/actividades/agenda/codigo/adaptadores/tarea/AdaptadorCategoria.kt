package actividades.agenda.codigo.adaptadores.tarea

import actividades.agenda.R
import actividades.agenda.codigo.modelos.tarea.attributos.CategoriaItem
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.parseadores.string.ParseadorString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class AdaptadorCategoria(
    private val items: List<CategoriaItem>,
    private val onCategoriaClick: (CategoriaItem) -> Unit
) : RecyclerView.Adapter<AdaptadorCategoria.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnCategoria: Button = view.findViewById(R.id.btnCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.fila_actividad_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Usamos `when` para determinar qué mostrar en el botón
        when (item) {
            is CategoriaItem.Categoria -> {
                holder.btnCategoria.text = ParseadorString.parseEnumText("${item.categoria}")
            }
            is CategoriaItem.Todas -> {
                holder.btnCategoria.text = "Todas"
            }
        }

        // Configurar el clic del botón
        holder.btnCategoria.setOnClickListener {
            onCategoriaClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
