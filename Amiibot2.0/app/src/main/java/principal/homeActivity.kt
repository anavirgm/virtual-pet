package principal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.amiibot.R
import settings.settingsActivity

class homeActivity : AppCompatActivity() {

    private var monedas = 200 // Valor inicial de las monedas
    private var currentImageIndex = 0 // Índice para el carrusel de imágenes
    private val comidasCompradas = mutableListOf<Int>() // Lista para almacenar las imágenes de las comidas compradas
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val txtCoins = findViewById<TextView>(R.id.txtCoins)
        val btnComida = findViewById<ImageButton>(R.id.Comida) // Referencia al ImageButton de Comida
        val imageView = findViewById<ImageView>(R.id.imageView)

        // Establecer las monedas en el TextView
        txtCoins.text = monedas.toString()

        // Recuperar valores de SharedPreferences
        val selectedImage = sharedPreferences.getInt("selectedImage", R.drawable.fuego)
        val selectedBackground = sharedPreferences.getInt("selectedBackground", R.drawable.fondo_fuego_1)

        // Establecer el fondo
        mainLayout.setBackgroundResource(selectedBackground)

        // Establecer la imagen de la mascota
        imageView.setImageResource(selectedImage)

        val btnSettings = findViewById<ImageButton>(R.id.btnsettings)
        val btnMascota = findViewById<ImageButton>(R.id.mascosta)
        val btnTienda = findViewById<ImageButton>(R.id.Tienda)

        // Configurar el carrusel
        val btnLeft = findViewById<ImageButton>(R.id.buttonLeft2)
        val btnRight = findViewById<ImageButton>(R.id.buttonRight2)

        // Al presionar el botón de izquierda, cambia la imagen a la anterior
        btnLeft.setOnClickListener {
            currentImageIndex = if (currentImageIndex > 0) currentImageIndex - 1 else comidasCompradas.size - 1
            btnComida.setImageResource(comidasCompradas[currentImageIndex]) // Cambiar la imagen de "Comida"
        }

        // Al presionar el botón de derecha, cambia la imagen a la siguiente
        btnRight.setOnClickListener {
            currentImageIndex = (currentImageIndex + 1) % comidasCompradas.size
            btnComida.setImageResource(comidasCompradas[currentImageIndex]) // Cambiar la imagen de "Comida"
        }

        // Al hacer clic en Tienda, pasamos las monedas actuales a tiendaActivity
        btnTienda.setOnClickListener {
            val intent = Intent(this, tiendaActivity::class.java)
            intent.putExtra("monedas", monedas) // Enviar el valor actual de las monedas
            startActivityForResult(intent, 1) // Código 1 para identificar la tienda
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, settingsActivity::class.java)
            startActivity(intent)
        }
        btnMascota.setOnClickListener {
            val intent = Intent(this, mascotaActivity::class.java)
            startActivity(intent)
        }
    }

    // Recibir las monedas actualizadas después de salir de la tienda
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) { // Verifica que es de tienda
            monedas = data?.getIntExtra("monedasRestantes", 200) ?: 200
            val txtCoins = findViewById<TextView>(R.id.txtCoins)
            txtCoins.text = monedas.toString() // Actualizar la interfaz con el nuevo valor

            // Obtener las comidas compradas de la tienda
            val comidaComprada = data?.getIntExtra("comidaComprada", 0) ?: 0
            if (comidaComprada != 0) {
                comidasCompradas.add(comidaComprada) // Agregar la comida comprada a la lista
                currentImageIndex = comidasCompradas.size - 1 // última imagen comprada como la actual
                findViewById<ImageButton>(R.id.Comida).setImageResource(comidasCompradas[currentImageIndex]) // Actualizar la imagen de "Comida"
            }
        }
    }
}
