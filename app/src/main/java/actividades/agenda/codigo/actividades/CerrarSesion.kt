package actividades.agenda.codigo.actividades

import actividades.agenda.R
import actividades.agenda.databinding.ActivityCerrarSesionBinding
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CerrarSesion : AppCompatActivity() {
    private lateinit var binding: ActivityCerrarSesionBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCerrarSesionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        firebaseAuth = Firebase.auth

        // Configurar la vista para que ocupe toda la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mostrar el correo electrónico del usuario en el TextView
        mostrarCorreoUsuario()

        // Configurar el botón "Cerrar Sesión"
        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        // Configurar el botón "Volver"
        binding.btnVolver.setOnClickListener {
            abrirFormularioAgenda()
        }
    }

    private fun mostrarCorreoUsuario() {
        val user = firebaseAuth.currentUser
        val tvSubtitle: TextView = findViewById(R.id.tv_subtitle)
        if (user != null) {
            tvSubtitle.text = " ${user.email}"
        } else {
            tvSubtitle.text = "No hay sesión activa"
        }
    }

    private fun cerrarSesion() {
        firebaseAuth.signOut()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        abrirFormularioAgenda()
    }

    private fun abrirFormularioAgenda() {
        val intent = Intent(this, ActividadAgenda::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual para que no pueda regresar
    }
}