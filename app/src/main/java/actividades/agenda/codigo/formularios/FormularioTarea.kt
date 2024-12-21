package actividades.agenda.codigo.formularios

import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.repositorios.controladores.TareaRepositorio
import actividades.agenda.codigo.actividades.ActividadAgenda
import actividades.agenda.codigo.actividades.ActividadTarea
import actividades.agenda.codigo.validaciones.ValidacionTarea
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.parseadores.xml.ParseadorElementosVista
import actividades.agenda.codigo.parseadores.fecha.ParseadorFecha
import actividades.agenda.codigo.parseadores.string.ParseadorString
import actividades.agenda.codigo.parseadores.tarea.ParseadorTarea
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import actividades.agenda.databinding.FragmentoFormularioTareaBinding
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import actividades.agenda.codigo.conexiones.RetrofitCliente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class FormularioTarea : Fragment() {
    //? Variables de control
    private var job: Job? = null

    private var textParam: String? = null
    private var idP: Int = -1

    //? Variables de Layout

    private var _binding: FragmentoFormularioTareaBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttonHorarioInicio: Button
    private lateinit var buttonHorarioFin: Button
    private lateinit var buttonEnviar: Button

    private var calendario: Calendar = Calendar.getInstance()
    private lateinit var tareaRepo: TareaRepositorio

    //? funciones de ciclo de vida del fragmento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            textParam = it.getString(ARG_TEXT)
            idP = it.getInt(ARG_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoFormularioTareaBinding.inflate(
            inflater,
            container,
            false)
        return binding.root
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TEXT = "textParam"
        private var id: Int = 1 + TareaRepositorio.obtenerNuevoId(
            tareas = ActividadAgenda.listaTareas.toList()
        )

        @JvmStatic
        fun newInstance(text: String, id: Int): FormularioTarea {
            return FormularioTarea().apply {
                arguments = Bundle().apply {
                    putString(ARG_TEXT, text)
                    putInt(ARG_ID, id)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main()
    }

    override fun onDestroyView() {
        job?.cancel()
        super.onDestroyView()
        _binding = null
    }

    //? funciones principales

    private fun main() {
        actualizarTitulo()
        asignarReferencias()
        asignarEventListeners()
        asignarDatosSpinner(
            spinner = binding.spinnerCategorias,
            datos = Categorias.entries.toList().map { ParseadorString.parseEnumText(it.toString()) }
        )
        asignarDatosSpinner(
            spinner = binding.spinnerEstados,
            datos = Estados.entries.toList().map { ParseadorString.parseEnumText(it.toString()) }
        )
        if (idP != -1) llenarDatosFormulario()
    }

    private fun actualizarTitulo() {
        binding.textViewTareaTitle.text = textParam
    }

    private fun asignarReferencias() {
        tareaRepo = TareaRepositorio(requireContext())
        buttonHorarioInicio = binding.buttonElegirFechaInicioTarea
        buttonHorarioFin = binding.buttonElegirFechaFinTarea
        buttonEnviar = binding.buttonEnviarFormularioTarea
    }

    private fun asignarEventListeners() {
        buttonHorarioInicio.setOnClickListener {
            elegirFecha(button = binding.buttonElegirFechaInicioTarea)
        }

        buttonHorarioFin.setOnClickListener {
            elegirFecha(button = binding.buttonElegirFechaFinTarea)
        }

        buttonEnviar.setOnClickListener {
            enviarFormulario()
        }
    }

    private fun asignarDatosSpinner(spinner: Spinner, datos: List<String>) {
        val adaptador = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            datos
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = adaptador
    }

    private fun llenarDatosFormulario() {
        val tarea = tareaRepo.buscarTareaId(dbHelper = ActividadAgenda.dbHelper, id = idP)
        binding.inputEditNombreTarea.text = ParseadorElementosVista.textoToEditable(
            tarea.nombre
        )
        binding.spinnerCategorias.setSelection(
            ParseadorTarea.parseCategoriasPosicion(tarea.categoria)
        )
        binding.buttonElegirFechaInicioTarea.text = ParseadorFecha.fechaATextoEs(
            tarea.fechaInicio
        )
        binding.buttonElegirFechaFinTarea.text = ParseadorFecha.fechaATextoEs(
            tarea.fechaFin
        )
        binding.spinnerEstados.setSelection(ParseadorTarea.parseEstadosPosicion(tarea.estado))
    }

    //? funciones secundarias

    private fun elegirFecha(button: Button) {
        val zonaHorariaLima = TimeZone.getTimeZone("GMT-5")
        val calendar = Calendar.getInstance(zonaHorariaLima)
        val fechaUTC = ParseadorFecha.obtenerFechaActual()
        val offset = zonaHorariaLima.getOffset(fechaUTC.time)

        calendar.time = fechaUTC
        calendar.timeInMillis = fechaUTC.time

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendario.set(selectedYear, selectedMonth, selectedDay)
                obtenerHora(button)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis - offset
        datePickerDialog.show()
    }

    private fun obtenerHora(button: Button) {
        val zonaHorariaLima = TimeZone.getTimeZone("GMT-5")
        val calendar = Calendar.getInstance(zonaHorariaLima)
        val fechaUTC = ParseadorFecha.obtenerFechaActual()

        calendar.time = fechaUTC
        calendar.timeInMillis = fechaUTC.time

        val hora = calendar.get(Calendar.HOUR_OF_DAY)
        val minutos = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendario.set(Calendar.MINUTE, minute)
                actualizarButton(button)
            },
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.updateTime(hora, minutos)
        timePickerDialog.show()
    }

    private fun actualizarButton(button: Button) {
        val fecha: Date = calendario.time
        button.text = ParseadorFecha.fechaATextoEs(fecha)
    }

    private fun enviarFormulario() {
        mostrarConfirmacion(::guardarTarea)
    }

    private fun volverAgenda() {
        val intent = Intent(requireContext(), ActividadAgenda::class.java)
        startActivity(intent)
        if (activity != null) {
            job?.cancel()
            activity?.finish()
        }
    }

    private fun mostrarMensaje(title: String, msg: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title).setMessage(msg)
            .setPositiveButton("Si") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarConfirmacion(fn: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder
            .setTitle("Confirmación de envío de formulario")
            .setMessage("¿Desea enviar el formulario?")
            .setPositiveButton("Si") { dialog, _ ->
                dialog.dismiss()
                fn()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun guardarTarea() {
        ValidacionTarea.actualizarListaTareas()

        val nombre = binding.inputEditNombreTarea.text.toString()
        val categoria = ParseadorTarea.parseCategorias(
            binding.spinnerCategorias.selectedItem.toString()
        )
        val fechaInicio = ParseadorFecha.textoEsAFecha(
            binding.buttonElegirFechaInicioTarea.text.toString()
        )
        val fechaFin = ParseadorFecha.textoEsAFecha(
            binding.buttonElegirFechaFinTarea.text.toString()
        )
        val estado = ParseadorTarea.parseEstados(
            binding.spinnerEstados.selectedItem.toString()
        )

        Companion.id = ValidacionTarea.obtenerIdUnico(Companion.id)

        val tarea = Tarea(
            id = if (idP == -1) Companion.id else idP,
            nombre = nombre,
            categoria = categoria,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            estado = estado
        )

        val esValido = hacerValidacionesHorario(
            tarea = tarea,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin
        )
        if(!esValido) return

        guardarDatosLocales(tarea = tarea)

        if(!ActividadAgenda.hayConexionRemota) volverAgenda()
        else guardarDatosRemotos(tarea = tarea)
    }

    private fun hacerValidacionesHorario(
        tarea: Tarea, fechaInicio: Date, fechaFin: Date
    ): Boolean {
        // Verificar si el horario es válido
        if (!ValidacionTarea.esHorarioValido(fechaInicio.toString(), fechaFin.toString())) {
            mostrarMensaje("Horario no válido", "Prueba otro horario")
            return false
        }

        // Verificamos si hay superposición de fechas:
        // Si es una tarea nueva (idP == -1), validamos que no se superponga
        // Si es una tarea existente (idP != -1), permitimos la superposición con su propio
        // horario, pero no con otros.
        if (idP == -1 && ValidacionTarea.estaRangoFechasSuperpuesto(tarea)) {
            mostrarMensaje(
                "Rango de fechas inválido por superposición",
                "Existe un conflicto de horarios"
            )
            return false
        } else if (idP != -1 && ValidacionTarea.estaRangoFechasSuperpuesto(tarea, tarea.id)) {
            // Aquí pasamos el ID de la tarea que estamos editando para que no se valide contra
            // sí misma
            mostrarMensaje(
                "Rango de fechas inválido por superposición",
                "Existe un conflicto de horarios"
            )
            return false
        }
        return true
    }

    private fun guardarDatosLocales(tarea: Tarea) {
        if (idP == -1) {
            // crear
            tareaRepo.crearTarea(dbHelper = ActividadAgenda.dbHelper, tarea = tarea)
            mostrarMensajeToast(texto = "Tarea añadida correctamente")
        } else {
            // actualizar
            tareaRepo.modificarTarea(dbHelper = ActividadAgenda.dbHelper, tarea = tarea)
            (activity as ActividadTarea).guardarDatosFormulario()
            mostrarMensajeToast(texto = "Tarea actualizada correctamente")
        }
    }

    private fun guardarDatosRemotos(tarea: Tarea) {
        // Llamada para creación o actualización de la tarea
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                if (idP == -1) {
                    // Crear nueva tarea si no tiene ID existente
                    val response = RetrofitCliente.webServicepost.addTask(tarea)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            tareaRepo.crearTarea(
                                dbHelper = ActividadAgenda.dbHelper,
                                tarea = tarea
                            )
                            mostrarMensajeToast(texto = "Tarea añadida correctamente")
                        } else {
                            mostrarMensajeToast("Error al añadir tarea")
                        }
                    }
                } else {
                    // Actualizar tarea existente si tiene un ID válido
                    val responseUpdate = RetrofitCliente.webServicepost.updateTask(
                        idP, tarea
                    )
                    withContext(Dispatchers.Main) {
                        if (responseUpdate.isSuccessful) {
                            mostrarMensajeToast(texto = "Tarea actualizada correctamente")
                        } else {
                            mostrarMensajeToast("Error al actualizar la tarea")
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                // Aquí capturamos el error de tiempo de espera
                withContext(Dispatchers.Main) {
                    mostrarMensajeToast(
                        texto = "La conexión ha tardado demasiado tiempo. Intenta más tarde"
                    )
                }
            } catch (e: Exception) {
                // Capturamos cualquier otra excepción
                withContext(Dispatchers.Main) {
                    mostrarMensajeToast(texto = "Ocurrió un error inesperado")
                }
            } finally {
                volverAgenda()
            }
        }
    }

    private fun mostrarMensajeToast(texto: String) {
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show()
    }
}