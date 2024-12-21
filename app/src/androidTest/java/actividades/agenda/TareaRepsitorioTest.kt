package actividades.agenda

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper
import actividades.agenda.codigo.repositorios.controladores.TareaRepositorio
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.Date


class TareaRepsitorioTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dbHelper = DBHelper(context)
    val tareaRepo = TareaRepositorio(context)

    @Test
    fun crearTarea() {

        // Crear la tarea
        val tarea = Tarea(id = 1, nombre = "Tarea 1", categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Date(), fechaFin = Date(), estado = Estados.PENDIENTE)
        tareaRepo.crearTarea(dbHelper, tarea)

        // Buscar la tarea por ID
        val tareaBuscada = tareaRepo.buscarTareaId(dbHelper, 1)

        // Verificar que la tarea fue creada correctamente
        assertEquals(tarea.nombre, tareaBuscada.nombre)
    }

    @Test
    fun modificarTarea() {
        val tarea = Tarea(id = 1, nombre = "Tarea 1", categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Date(), fechaFin = Date(), estado = Estados.PENDIENTE)
        tareaRepo.crearTarea(dbHelper, tarea)

        val tareaModificada = Tarea(id = 1, nombre = "Tarea Modificada", categoria = Categorias.ALTA_PRIORIDAD,
            fechaInicio = Date(), fechaFin = Date(), estado = Estados.COMPLETADO)
        tareaRepo.modificarTarea(dbHelper, tareaModificada)

        val tareaBuscada = tareaRepo.buscarTareaId(dbHelper, 1)
        assertEquals(tareaModificada.nombre, tareaBuscada.nombre)
        assertEquals(tareaModificada.categoria, tareaBuscada.categoria)
    }

    @Test
    fun testBuscarTareaPorId() {
        // Crea un objeto de tarea
        val tarea = Tarea(id = 1, nombre = "Tarea 1", categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Date(), fechaFin = Date(), estado = Estados.PENDIENTE)

        // Agrega la tarea al repositorio
        tareaRepo.crearTarea(dbHelper, tarea)

        // Busca la tarea por ID
        val tareaEncontrada = tareaRepo.buscarTareaId(dbHelper, 1)

        // Verifica que la tarea encontrada no sea null
        assertNotNull(tareaEncontrada)

        // Verifica que los atributos sean correctos
        assertEquals(tarea.id, tareaEncontrada.id)
        assertEquals(tarea.nombre, tareaEncontrada.nombre)
        assertEquals(tarea.categoria, tareaEncontrada.categoria)

        // Elimina la tarea
        tareaRepo.eliminarTarea(dbHelper, tarea.id)

        // Intenta buscar de nuevo la tarea por ID y maneja la excepción
        val tareaEliminada = runCatching {
            tareaRepo.buscarTareaId(dbHelper, 1)
        }.exceptionOrNull()

    }

    @Test
    fun testEliminarTarea() {
        val tarea = Tarea(id = 1, nombre = "Tarea 1", categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Date(), fechaFin = Date(), estado = Estados.PENDIENTE)
        tareaRepo.crearTarea(dbHelper, tarea)

        // Elimina la tarea
        tareaRepo.eliminarTarea(dbHelper, tarea.id)

        // Verifica que la tarea no se encuentre en la base de datos
        val tareaBuscada = runCatching {
            tareaRepo.buscarTareaId(dbHelper, tarea.id)
        }.getOrNull()

        // Asegúrate de que tareaBuscada sea null o que no se encuentre
        assert(tareaBuscada == null) { "La tarea debería haber sido eliminada." }
    }
}