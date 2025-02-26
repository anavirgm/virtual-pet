package principal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.amiibot.R
import settings.pruebaActivity
import settings.settingsActivity

class inicioActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private val images = arrayOf(
        R.drawable.fuego,
        R.drawable.electrico,
        R.drawable.agua
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio) // 🔹 Esta actividad maneja activity_inicio.xml

        imageView = findViewById(R.id.imageView)
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
            val intent = Intent(this, homeActivity::class.java)

            // Mapa que asocia imágenes con fondos
            val backgrounds = mapOf(
                R.drawable.fuego to R.drawable.fondo_fuego_1,
                R.drawable.electrico to R.drawable.fondo_electrico_1,
                R.drawable.agua to R.drawable.fondo_agua_1
            )

            val selectedImage = images[currentIndex] // Imagen elegida
            val selectedBackground = backgrounds[selectedImage] ?: R.drawable.fondo_fuego_1 // Fondo correspondiente

            intent.putExtra("selectedImage", selectedImage) // Pasamos la imagen
            intent.putExtra("selectedBackground", selectedBackground) // Pasamos el fondo
            startActivity(intent)
        }



    }
}