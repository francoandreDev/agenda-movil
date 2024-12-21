package actividades.agenda

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.parseadores.fecha.ParseadorFecha
import actividades.agenda.codigo.repositorios.controladores.TareaRepositorio
import actividades.agenda.codigo.validaciones.ValidacionTarea
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import java.util.Calendar
import java.util.Date

class ValidacionTareaTest {
    @Before
    fun setUp() {
        // Reiniciar la lista de tareas antes de cada prueba
        ValidacionTarea.actualizarListaTareas()
    }

    @Test
    fun retornarIdUnico() {
        val id = 1
        val resultado = ValidacionTarea.obtenerIdUnico(id)
        assertEquals(id, resultado)
    }

    @Test
    fun validarHorarios() {
        // Crear objeto Calendar para establecer las fechas
        val calendarInicio = Calendar.getInstance().apply {
            set(2023, Calendar.OCTOBER, 1, 10, 0) // 1 de octubre de 2023 a las 10:00
        }
        val fechaInicio: Date = calendarInicio.time

        val calendarFin = Calendar.getInstance().apply {
            set(2023, Calendar.OCTOBER, 1, 12, 0) // 1 de octubre de 2023 a las 12:00
        }
        val fechaFin: Date = calendarFin.time

        // Llamar a la función de validación
        val resultado = ValidacionTarea.esHorarioValido(fechaInicio.toString(), fechaFin.toString())

        // Verificar que el resultado es verdadero
        assertTrue(resultado)
    }

    @Test
    fun validarFechaNula() {
        val fechaInicio = null
        val calendarFin = Calendar.getInstance().apply {
            set(2023, Calendar.OCTOBER, 1, 12, 0) // 1 de octubre de 2023 a las 12:00
        }
        val fechaFin: Date = calendarFin.time

        val resultado = ValidacionTarea.esHorarioValido(fechaInicio, fechaFin.toString())
        assertFalse(resultado)
    }

    @Test
    fun superPosicionFechas_Superponen() {
        // Tarea existente: del 1 de octubre a las 10:00 a 12:00
        val tareaExistente = Tarea(
            id = 1,
            nombre = "Tarea existente",
            categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Calendar.getInstance().apply {
                set(2023, 9, 1, 10, 0) // 1 de octubre a las 10:00
            }.time,
            fechaFin = Calendar.getInstance().apply {
                set(2023, 9, 1, 12, 0) // 1 de octubre a las 12:00
            }.time,
            estado = Estados.PENDIENTE
        )

        // Agregar tarea existente a la lista
        ValidacionTarea.tareas.add(tareaExistente)

        // Nueva tarea que se superpone: del 1 de octubre a las 11:00 a 13:00
        val nuevaTarea = Tarea(
            id = 2,
            nombre = "Nueva tarea",
            categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Calendar.getInstance().apply {
                set(2023, 9, 1, 11, 0) // 1 de octubre a las 11:00
            }.time,
            fechaFin = Calendar.getInstance().apply {
                set(2023, 9, 1, 13, 0) // 1 de octubre a las 13:00
            }.time,
            estado = Estados.PENDIENTE
        )

        // Verifica que hay superposición
        val resultado = ValidacionTarea.estaRangoFechasSuperpuesto(nuevaTarea)
        assertTrue("Las tareas deberían superponerse", resultado)
    }

    @Test
    fun superPosicionFechas_NoSuperponen() {
        // Tarea existente: del 1 de octubre a las 10:00 a 12:00
        val tareaExistente = Tarea(
            id = 1,
            nombre = "Tarea existente",
            categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Calendar.getInstance().apply {
                set(2023, 9, 1, 10, 0) // 1 de octubre a las 10:00
            }.time,
            fechaFin = Calendar.getInstance().apply {
                set(2023, 9, 1, 12, 0) // 1 de octubre a las 12:00
            }.time,
            estado = Estados.PENDIENTE
        )

        // Agregar tarea existente a la lista
        ValidacionTarea.tareas.add(tareaExistente)

        // Nueva tarea que no se superpone: del 1 de octubre a las 12:30 a 13:30
        val nuevaTarea = Tarea(
            id = 2,
            nombre = "Nueva tarea",
            categoria = Categorias.BAJA_PRIORIDAD,
            fechaInicio = Calendar.getInstance().apply {
                set(2023, 9, 1, 12, 30) // 1 de octubre a las 12:30
            }.time,
            fechaFin = Calendar.getInstance().apply {
                set(2023, 9, 1, 13, 30) // 1 de octubre a las 13:30
            }.time,
            estado = Estados.PENDIENTE
        )

        // Verifica que no hay superposición
        val resultado = ValidacionTarea.estaRangoFechasSuperpuesto(nuevaTarea)
        assertFalse("Las tareas no deberían superponerse", resultado)
    }

}