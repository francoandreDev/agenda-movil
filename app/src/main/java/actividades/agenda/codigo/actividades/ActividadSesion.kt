package actividades.agenda.codigo.actividades

import actividades.agenda.R
import actividades.agenda.databinding.ActivityInicioSesionBinding
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ActividadSesion : AppCompatActivity() {
    private lateinit var binding: ActivityInicioSesionBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        firebaseAuth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        asignar()
        volverListener()
        registrarListener() // Añadido para escuchar el botón Registrar
    }

    private fun volverListener() {
        val buttonLogin: Button = findViewById(R.id.buttonVolverAgenda)
        buttonLogin.setOnClickListener {
            abrirFormularioActividadAgenda()
        }
    }

    private fun abrirFormularioActividadAgenda() {
        val intent = Intent(this, ActividadAgenda::class.java)
        startActivity(intent)
    }

    private fun asignar() {
        binding.btnIngresqar.setOnClickListener {
            val correo = binding.txtCorreo.text.toString()
            val contrasenia = binding.txtContrasenia.text.toString()
            iniciarSesion(correo, contrasenia)
        }
    }

    private fun registrarListener() {
        binding.btnRegistrar.setOnClickListener {
            val correo = binding.txtCorreo.text.toString()
            val contrasenia = binding.txtContrasenia.text.toString()

            // Validaciones simples
            if (correo.isEmpty() || contrasenia.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(correo, contrasenia)
        }
    }

    private fun registrarUsuario(correo: String, contrasenia: String) {
        firebaseAuth.createUserWithEmailAndPassword(correo, contrasenia)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuario registrado exitosamente
                    val user = firebaseAuth.currentUser
                    Toast.makeText(this, "Usuario registrado: ${user?.email}", Toast.LENGTH_SHORT).show()

                    // Redirigir a la actividad Agenda
                    val intent = Intent(this, ActividadAgenda::class.java)
                    startActivity(intent)
                    finish() // Finaliza la actividad actual para que no pueda regresar

                } else {
                    // Mostrar error
                    val exception = task.exception
                    Toast.makeText(this, "Error al registrar: ${exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun iniciarSesion(correo: String, contrasenia: String) {
        firebaseAuth.signInWithEmailAndPassword(correo, contrasenia)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val intent = Intent(this, ActividadAgenda::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
