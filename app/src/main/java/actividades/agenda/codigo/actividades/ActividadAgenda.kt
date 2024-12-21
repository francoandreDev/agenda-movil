package actividades.agenda.codigo.actividades

import actividades.agenda.R
import actividades.agenda.codigo.adaptadores.tarea.AdaptadorCategoria
import actividades.agenda.codigo.adaptadores.tarea.AdaptadorTarea
import actividades.agenda.codigo.modelos.tarea.Tarea
import actividades.agenda.codigo.modelos.tarea.attributos.CategoriaItem
import actividades.agenda.codigo.modelos.tarea.attributos.Categorias
import actividades.agenda.codigo.repositorios.controladores.TareaRepositorio
import actividades.agenda.codigo.repositorios.dbAgenda.DBHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import actividades.agenda.codigo.conexiones.RetrofitCliente
import actividades.agenda.codigo.modelos.tarea.attributos.Estados
import actividades.agenda.codigo.notificaciones.NotificationHelper
import actividades.agenda.codigo.parseadores.fecha.ParseadorFecha
import android.annotation.SuppressLint
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class ActividadAgenda : AppCompatActivity(), SearchView.OnQueryTextListener {
    //? variables de actividad
    companion object {
        lateinit var dbHelper: DBHelper
        var hayConexionRemota: Boolean = true
        var listaTareas: MutableList<Tarea> = mutableListOf()
    }

    //? variables de control de procesos
    private var job1: Job? = null
    private var job2: Job? = null

    //? variables de layout
    private lateinit var adaptadorTareas: AdaptadorTarea
    private lateinit var recyclerViewTarea: RecyclerView

    //? variables auxiliares
    private lateinit var repoDatos: TareaRepositorio
    private lateinit var txtbuscar : SearchView
    private lateinit var recyclerViewCategorias: RecyclerView

    //? funciones del ciclo de vida de la actividad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.actividad_agenda)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        main()
    }

    override fun onResume() {
        super.onResume()
        if (!::adaptadorTareas.isInitialized) {
            adaptadorTareas = AdaptadorTarea(mutableListOf(), ::onItemEdit, ::onItemDelete)
            recyclerViewTarea.adapter = adaptadorTareas
        }
        cargarDatos()
    }

    //? funciones principales

    private fun main() {
        instanciarManejadorBaseDeDatos()
        verificarPermisos()
        asignarVariables()
        crearOnClickListeners()
        buscartarea()
    }

    private fun instanciarManejadorBaseDeDatos() {
        dbHelper = DBHelper(this)
    }

    private fun verificarPermisos() {
        // Los permisos deben verificarse dentro de un contexto (activity en este caso)
        val hasReadPermissions = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val readAndWritePermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (!hasReadPermissions || !hasWritePermission) {
            ActivityCompat.requestPermissions(
                this,
                readAndWritePermissions,
                123
            )
        }
    }

    private fun asignarVariables() {
        asignarVariablesLayout()
        asignarVariablesAuxiliares()
    }

    private fun crearOnClickListeners() {
        crearTareaListener()
        iniciarSesionListener()
    }

    private fun buscartarea(){
        txtbuscar = findViewById(R.id.txtbuscar)
        txtbuscar.setOnQueryTextListener(this)
    }

    //? funciones secundarias

    private fun abrirFormularioTarea() {
        job1?.cancel()
        job2?.cancel()
        val intent = Intent(this, ActividadTarea::class.java)
        startActivity(intent)
    }

    private fun asignarVariablesLayout() {
        recyclerViewTarea = findViewById(R.id.recyclerViewTareas)
        adaptadorTareas = AdaptadorTarea(mutableListOf(), ::onItemEdit, ::onItemDelete)
        recyclerViewTarea.adapter = adaptadorTareas
    }

    private fun asignarVariablesAuxiliares() {
        repoDatos = TareaRepositorio(this)
    }

    private fun crearTareaListener() {
        val buttonAddTask: ImageView = findViewById(R.id.imageView4)
        buttonAddTask.setOnClickListener {
            abrirFormularioTarea()
        }
    }


    private fun iniciarSesionListener() {
        val buttonLogin: ImageButton = findViewById(R.id.btn_iniciarsesion)
        buttonLogin.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser

            if (currentUser == null) {
                abrirFormularioLogin()
            } else {
                abrirFormularioCerrarSesion()
            }
        }
    }

    private fun abrirFormularioLogin() {
        val intent = Intent(this, ActividadSesion::class.java)
        startActivity(intent)
    }

    private fun abrirFormularioCerrarSesion() {
        val intent = Intent(this, CerrarSesion::class.java)
        startActivity(intent)
    }
    private fun cargarTareas(tareas: List<Tarea>) {
        recyclerViewTarea.layoutManager = LinearLayoutManager(this)
        adaptadorTareas = AdaptadorTarea(
            tareas.toMutableList(),
            ::onItemEdit,
            ::onItemDelete
        )
        recyclerViewTarea.adapter = adaptadorTareas
    }

    private fun cargarDatos() {
        val busqueda = ""
        val datos: MutableMap<Int, Tarea> = repoDatos.buscarTarea(
            dbHelper = dbHelper,
            query = busqueda
        ).toMutableMap()
        leerDatosLocales(datos = datos)
        leerDatosRemotos(datos = datos)
    }

    private fun cargarTareas(datos: Map<Int, Tarea>) {
        listaTareas = mutableListOf()
        datos.forEach{(_, tarea) -> listaTareas.add(tarea) }
    }

    //? funciones auxiliares

    private fun onItemEdit(position: Int) {
        val tareas = repoDatos.buscarTarea(dbHelper = dbHelper, query = "")
        val id = tareas.keys.toList()[position]

        val intent = Intent(this, ActividadTarea::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    private fun onItemDelete(position: Int) {
        AlertDialog.Builder(this)
            .setMessage("¿Seguro que deseas eliminar este ítem?")
            .setPositiveButton("Sí") { _, _ ->
                val tareas = repoDatos.buscarTarea(dbHelper = dbHelper, query = "")
                val id = tareas.keys.toList()[position]
                eliminarDatosLocales(position = position, id = id)
                eliminarDatosRemotos(id = id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun leerDatosLocales(datos: MutableMap<Int, Tarea>) {
        cargarTareas(datos = datos.toMap())
        cargarTareas(tareas = listaTareas.toList())
        iniciarCategoria()
    }

    private fun leerDatosRemotos(datos: MutableMap<Int, Tarea>) {
        job1 = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitCliente.webService.obtenerTareas()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        hayConexionRemota = true
                        val tareas = response.body()!!.listaTarea
                        val mapTareaMutable: MutableMap<Int, Tarea> = tareas.associateBy {
                            it.id
                        }.toMutableMap()
                        datos.putAll(mapTareaMutable)
                        cargarTareas(datos = datos.toMap())
                        cargarTareas(tareas = listaTareas.toList())
                        iniciarCategoria()
                        correrNotificaciones()
                    } else {
                        mostrarMensajeToast("Error al obtener las tareas")
                    }
                }
            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    mostrarMensajeToast(
                        "La conexión ha tardado demasiado tiempo. Intenta más tarde"
                    )
                    hayConexionRemota = false
                    correrNotificaciones()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensajeToast("Ocurrió un error inesperado")
                }
            }
        }
    }

    private fun correrNotificaciones() {
        val notificationHelper = NotificationHelper(this)
        notificationHelper.crearCanalNotificacion()
        enviarNotificacionesPendientes(notificationHelper = notificationHelper)
    }

    @SuppressLint("NewApi")
    private fun enviarNotificacionesPendientes(notificationHelper: NotificationHelper) {
        val datos = listaTareas // Lista de tareas

        // Configuramos la zona horaria de Lima
        val zonaHorariaLima = TimeZone.getTimeZone("GMT-5")
        val calendar = Calendar.getInstance(zonaHorariaLima)
        val fechaUTC = ParseadorFecha.obtenerFechaActual()

        calendar.time = fechaUTC
        calendar.timeInMillis = fechaUTC.time

        // Extraemos la fecha y hora actual
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Creamos el LocalDateTime para la fecha y hora actuales
        val nuevaFechaActual = LocalDateTime.of(year, month, day, hour, minute, second)
        val instant = nuevaFechaActual.atZone(ZoneId.systemDefault()).toInstant()
        val fechaActual = Date.from(instant)

        // Verificar cada tarea
        for (tarea in datos) {
            val fechaInicio = tarea.fechaInicio
            val fechaFin = tarea.fechaFin

            // Si la fecha actual está dentro del rango de la tarea y el estado es PENDIENTE, se envía la notificación
            if (fechaActual in fechaInicio..fechaFin) {
                if (tarea.estado == Estados.PENDIENTE) {
                    // Enviar la notificación para esta tarea
                    notificationHelper.mostrarNotificacion(context = this@ActividadAgenda, tarea = tarea)
                }
            }
        }
    }

    private fun eliminarDatosLocales(position: Int, id: Int) {
        repoDatos.eliminarTarea(dbHelper = dbHelper, id = id)
        adaptadorTareas.removeItem(position)
    }

    private fun eliminarDatosRemotos(id: Int) {
        job2 = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitCliente.webServicepost.deleteTask(id)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        mostrarMensajeToast(texto = "Tarea eliminada")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mostrarMensajeToast("Error al eliminar remotamente")
                    }
                }

            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    mostrarMensajeToast("La conexión ha tardado demasiado tiempo. Intenta más tarde")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarMensajeToast("Ocurrió un error inesperado")
                }
            }
        }
    }

    private fun iniciarCategoria() {
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias)
        recyclerViewCategorias.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // Crear una lista de CategoriaItem con las categorías reales + la opción "Todas"
        val categoriasReales = Categorias.entries.map { CategoriaItem.Categoria(it) }
        val todasCategoria = listOf(CategoriaItem.Todas)  // Solo una opción para "Todas"

        val categoriaAdaptador = AdaptadorCategoria(todasCategoria + categoriasReales) {
                categoriaSeleccionada ->
            when (categoriaSeleccionada) {
                is CategoriaItem.Todas -> {
                    mostrarTodasLasCategorias()
                }
                is CategoriaItem.Categoria -> {
                    mostrarTareasPorCategoria(categoriaSeleccionada.categoria)
                }
            }
        }

        recyclerViewCategorias.adapter = categoriaAdaptador
    }

    private fun mostrarTareasPorCategoria(categoriaSeleccionada: Categorias) {
        // Filtra las tareas según la categoría seleccionada
        val tareasFiltradas = listaTareas.filter { it.categoria == categoriaSeleccionada }
        cargarTareas(tareasFiltradas)  // Usa la función existente para recargar el RecyclerView
    }

    private fun mostrarTodasLasCategorias() {
        // Mostrar todas las tareas sin aplicar filtros de categoría
        cargarTareas(tareas = listaTareas.toList())
    }

    private fun mostrarMensajeToast(texto: String) {
        Toast.makeText(this@ActividadAgenda, texto, Toast.LENGTH_SHORT).show()
    }

    //? funciones sobreescritas

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (::adaptadorTareas.isInitialized) {
            val tareasFiltradas = if (newText.isNullOrEmpty()) {
                listaTareas // Mostrar todas las tareas si el texto está vacío
            } else {
                listaTareas.filter { it.nombre.contains(newText, ignoreCase = true) }
            }
            // Solo actualizar si el adaptador está inicializado
            adaptadorTareas.updateTasks(tareasFiltradas)
        }
        return true
    }
}
