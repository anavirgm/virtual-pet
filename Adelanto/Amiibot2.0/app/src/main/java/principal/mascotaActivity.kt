package principal

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.amiibot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class mascotaActivity : AppCompatActivity() {

    private lateinit var mascotaManager: MascotaManager
    private lateinit var progressXP: ProgressBar
    private lateinit var progressHambre: ProgressBar
    private lateinit var progressAnimo: ProgressBar
    private lateinit var progressEnergia: ProgressBar
    private lateinit var nivelText: TextView

    private lateinit var textViewNombreMascota: TextView
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascota)

        val mascotaImage = findViewById<ImageView>(R.id.imageView)
        textViewNombreMascota = findViewById(R.id.textView2)

        mascotaManager = MascotaManager(this, mascotaImage)

        if (userId != null) {
            obtenerNombreMascota(userId)
        }

        progressXP = findViewById(R.id.progressBar1)
        progressHambre = findViewById(R.id.progressBar2)
        progressAnimo = findViewById(R.id.progressBar3)
        progressEnergia = findViewById(R.id.progressBar4)
        nivelText = findViewById(R.id.textView1)

        mascotaManager.iniciarReduccionAutomatica()
        //mascotaManager.configurarCarino()
        actualizarInterfaz()
    }

    fun actualizarInterfaz() {
        progressXP.progress = mascotaManager.xp
        progressHambre.progress = mascotaManager.hambre
        progressAnimo.progress = mascotaManager.animo
        progressEnergia.progress = mascotaManager.energia
        nivelText.text = "Nivel: ${mascotaManager.nivel}"

        // Guardar en Firestore cuando se actualiza la interfaz
        guardarDatosEnFirestore()
    }

    private fun guardarDatosEnFirestore() {
        if (userId == null) return

        // Buscar la mascota del usuario
        firestore.collection("users_mascotas")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val mascotaId = document.id // ID del documento en Firestore

                    // Crear un mapa con los datos actualizados
                    val datosMascota = hashMapOf(
                        "animo" to mascotaManager.animo,
                        "energia" to mascotaManager.energia,
                        "hambre" to mascotaManager.hambre,
                        "nivel" to mascotaManager.nivel
                    )

                    // Actualizar en Firestore
                    firestore.collection("users_mascotas")
                        .document(mascotaId)
                        .update(datosMascota as Map<String, Any>)
                        .addOnSuccessListener {
                            println("Datos actualizados correctamente")
                        }
                        .addOnFailureListener { e ->
                            println("Error al actualizar datos: ${e.message}")
                        }
                }
            }
    }

    private fun obtenerNombreMascota(userId: String) {
        val prefs = getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)
        textViewNombreMascota.text = prefs.getString("nombreMascota", "") // Mostrar lo que ya está guardado

        firestore.collection("users_mascotas")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val nombreMascota = document.getString("nombre") ?: "Sin nombre"
                    textViewNombreMascota.text = nombreMascota

                    // Guardar en SharedPreferences para la próxima vez
                    prefs.edit().putString("nombreMascota", nombreMascota).apply()
                }
            }
    }

}
