package actividades.agenda.codigo.actividades

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import actividades.agenda.R
import actividades.agenda.codigo.formularios.FormularioTarea
import android.app.Activity
import android.content.Intent
import android.widget.Button

class ActividadTarea : AppCompatActivity() {
    //? variables layout
    private lateinit var buttonVolver: Button

    //? funciones de ciclo de vida de actividad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.actividad_tarea)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        main()
    }

    //? funciones principales

    private fun main() {
        val id = obtenerIdActividad()

        asignarVariablesLayout()
        asignarVolverListener()

        if (id == -1) abrirFormularioCrear()
        else abrirFormularioEditar(id = id)
    }

    private fun obtenerIdActividad(): Int {
        return intent.getIntExtra("id", -1)
    }

    private fun asignarVariablesLayout() {
        buttonVolver = findViewById(R.id.buttonVolverAgenda)
    }

    private fun asignarVolverListener() {
        buttonVolver.setOnClickListener {
            val intent = Intent(this, ActividadAgenda::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun abrirFormularioCrear() {
        val fragment = FormularioTarea.newInstance("Nueva Tarea", -1)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Reemplaza el contenido actual del frame layout con el fragmento
        fragmentTransaction
            .replace(R.id.fragment_form_tarea_contenedor, fragment)
            .addToBackStack(null) // Optional: add to back stack
            .commit()
    }

    private fun abrirFormularioEditar(id: Int) {
        val formFragment = FormularioTarea.newInstance(text = "Editar Tarea", id = id)

        // Mostrar el Fragmento
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_form_tarea_contenedor, formFragment)
            .addToBackStack(null)
            .commit()
    }

    // funciones públicas auxiliares

    fun guardarDatosFormulario() {
        val resultIntent = Intent()
        resultIntent.putExtra("id", intent.getIntExtra("id", -1))
        setResult(Activity.RESULT_OK, resultIntent)

        // Finalizar este Activity
        finish()
    }
}
