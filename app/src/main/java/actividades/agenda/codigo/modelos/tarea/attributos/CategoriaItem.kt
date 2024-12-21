package actividades.agenda.codigo.modelos.tarea.attributos

sealed class CategoriaItem {
    // Subclase que representa una categoría real
    data class Categoria(val categoria: Categorias) : CategoriaItem()

    // Subclase que representa la opción "Todas"
    data object Todas : CategoriaItem()
}
