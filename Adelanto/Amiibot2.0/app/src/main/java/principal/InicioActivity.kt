package principal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.amiibot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import principal.homeActivity

class inicioActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var editTextNombre: EditText
    private val images = arrayOf(
        R.drawable.fuego,
        R.drawable.electrico,
        R.drawable.agua
    )
    private var currentIndex = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        sharedPreferences = getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)

        // Verificar si ya se ha seleccionado una mascota
        val mascotaSeleccionada = sharedPreferences.getBoolean("mascotaSeleccionada", false)
        if (mascotaSeleccionada) {
            // Si ya se ha seleccionado una mascota, navegar directamente a homeActivity
            navegarAHome()
            return
        }

        imageView = findViewById(R.id.imageView)
        editTextNombre = findViewById(R.id.nombreText)

        val buttonLeft: ImageButton = findViewById(R.id.buttonLeft)
        val buttonRight: ImageButton = findViewById(R.id.buttonRight)
        val buttonSelect: Button = findViewById(R.id.buttonSelect)

        buttonLeft.setOnClickListener {
            currentIndex = if (currentIndex > 0) currentIndex - 1 else images.size - 1
            imageView.setImageResource(images[currentIndex])
        }

        buttonRight.setOnClickListener {
            currentIndex = if (currentIndex < images.size - 1) currentIndex + 1 else 0
            imageView.setImageResource(images[currentIndex])
        }

        buttonSelect.setOnClickListener {
            guardarMascotaEnFirestore()
        }
    }

    private fun guardarMascotaEnFirestore() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid
        val db = FirebaseFirestore.getInstance()

        val mascotaId = when (images[currentIndex]) {
            R.drawable.fuego -> "WXCAvnfbtTD4NvcIPJxl"
            R.drawable.electrico -> "fPIAZeaiI5iIWuf52ZkD"
            R.drawable.agua -> "SOw5IcI0vXMUHbcmOamE"
            else -> "desconocido"
        }

        val selectedBackground = when (images[currentIndex]) {
            R.drawable.fuego -> R.drawable.fondo_fuego_1
            R.drawable.electrico -> R.drawable.fondo_electrico_1
            R.drawable.agua -> R.drawable.fondo_agua_1
            else -> R.drawable.fondo_default
        }

        val nombreMascota = editTextNombre.text.toString().trim()
        if (nombreMascota.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un nombre para tu mascota", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users_mascotas")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        db.collection("users_mascotas").document(document.id)
                            .update(
                                "nombre", nombreMascota,
                                "mascotaId", mascotaId
                            )
                            .addOnSuccessListener {
                                guardarEnSharedPreferences(images[currentIndex], selectedBackground, nombreMascota)
                                navegarAHome()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar la mascota: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        return@addOnSuccessListener
                    }
                } else {
                    val mascotaData = hashMapOf(
                        "userId" to userId,
                        "mascotaId" to mascotaId,
                        "nombre" to nombreMascota,
                        "energia" to 100,
                        "hambre" to 100,
                        "nivel" to 1
                    )

                    db.collection("users_mascotas").document()
                        .set(mascotaData)
                        .addOnSuccessListener {
                            guardarEnSharedPreferences(images[currentIndex], selectedBackground, nombreMascota)
                            navegarAHome()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar la mascota: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar la mascota: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarEnSharedPreferences(selectedImageId: Int, selectedBackground: Int, nombreMascota: String) {
        val editor = sharedPreferences.edit()
        editor.putInt("selectedImage", selectedImageId)
        editor.putInt("selectedBackground", selectedBackground)
        editor.putString("nombreMascota", nombreMascota)
        editor.putBoolean("mascotaSeleccionada", true)  // Guardamos que ya seleccionó la mascota
        editor.apply()
    }

    private fun navegarAHome() {
        val intent = Intent(this, homeActivity::class.java)
        // Configuramos el intent para que no se pueda regresar a inicioActivity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
