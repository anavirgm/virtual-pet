package principal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.amiibot.R
import settings.settingsActivity

class homeActivity : AppCompatActivity() {

    private lateinit var mascotaManager: MascotaManager
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

        mascotaManager = MascotaManager(this, imageView)


        // 🔹 Recuperar monedas guardadas
        monedas = sharedPreferences.getInt("monedas", 200)
        txtCoins.text = monedas.toString()

        // Recuperar valores de SharedPreferences
        val selectedBackground = sharedPreferences.getInt("selectedBackground", R.drawable.fondo_fuego_1)

        // Establecer el fondo
        mainLayout.setBackgroundResource(selectedBackground)
        mascotaManager.actualizarImagenMascota()
        mascotaManager.configurarCarino()

        val comidasGuardadas = sharedPreferences.getStringSet("comidasCompradas", mutableSetOf()) ?: mutableSetOf()
        comidasCompradas.addAll(comidasGuardadas.map { it.toInt() })

        if (comidasCompradas.isNotEmpty()) {
            btnComida.setImageResource(comidasCompradas.last()) // Mostrar la última comida comprada
        }

        val btnSettings = findViewById<ImageButton>(R.id.btnsettings)
        val btnMascota = findViewById<ImageButton>(R.id.mascosta)
        val btnLampara = findViewById<ImageButton>(R.id.lampara)
        val darkOverlay = findViewById<View>(R.id.darkOverlay)

        // Configurar el carrusel
        val btnLeft = findViewById<ImageButton>(R.id.buttonLeft2)
        val btnRight = findViewById<ImageButton>(R.id.buttonRight2)

        // Al presionar el botón de izquierda, cambia la imagen a la anterior
        btnLeft.setOnClickListener {
            currentImageIndex = if (currentImageIndex > 0) currentImageIndex - 1 else comidasCompradas.size - 1
            btnComida.setImageResource(comidasCompradas[currentImageIndex]) // Cambiar la imagen de "Comida"
        }

        // Al presionar el botón de derecha, cambia la imagen a la siguiente
        btnLeft.setOnClickListener {
            if (comidasCompradas.isNotEmpty()) {
                currentImageIndex = if (currentImageIndex > 0) currentImageIndex - 1 else comidasCompradas.size - 1
                btnComida.setImageResource(comidasCompradas[currentImageIndex])
            }
        }

        btnRight.setOnClickListener {
            if (comidasCompradas.isNotEmpty()) {
                currentImageIndex = (currentImageIndex + 1) % comidasCompradas.size
                btnComida.setImageResource(comidasCompradas[currentImageIndex])
            }
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, settingsActivity::class.java)
            startActivity(intent)
        }
        btnMascota.setOnClickListener {
            val intent = Intent(this, mascotaActivity::class.java)
            startActivity(intent)
        }

        btnComida.setOnClickListener {
            mascotaManager.alimentarMascota()
        }

        btnLampara.setOnClickListener {
            if (darkOverlay.visibility == View.VISIBLE) {
                darkOverlay.visibility = View.GONE // Apagar "modo noche"
            } else {
                darkOverlay.visibility = View.VISIBLE // Encender "modo noche"
                mascotaManager.recargarEnergia() // Recuperar energía al apagar la luz
            }
        }



        val btnTienda = findViewById<ImageButton>(R.id.Tienda)
        btnTienda.setOnClickListener {
            val intent = Intent(this, tiendaActivity::class.java)
            intent.putExtra("monedas", monedas)
            startActivityForResult(intent, 1)
        }
    }

    // Recibir las monedas actualizadas después de salir de la tienda
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            monedas = data?.getIntExtra("monedasRestantes", 200) ?: 200
            val txtCoins = findViewById<TextView>(R.id.txtCoins)
            txtCoins.text = monedas.toString()

            // 🔹 Guardar monedas en SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putInt("monedas", monedas)
            editor.apply()

            // Recuperar la comida comprada y actualizar la lista
            val comidaComprada = data?.getIntExtra("comidaComprada", 0) ?: 0
            if (comidaComprada != 0) {
                comidasCompradas.add(comidaComprada)
                currentImageIndex = comidasCompradas.size - 1
                findViewById<ImageButton>(R.id.Comida).setImageResource(comidasCompradas[currentImageIndex])
            }
        }
    }



}
