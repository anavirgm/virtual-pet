package principal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.amiibot.R
import notas.notaActivity
import settings.settingsActivity
import android.media.MediaPlayer

class homeActivity : AppCompatActivity() {

    private lateinit var mascotaManager: MascotaManager
    private var monedas = 200 // Valor inicial de las monedas
    private var currentImageIndex = 0 // Índice para el carrusel de imágenes
    private val comidasCompradas = mutableListOf<Int>() // Lista para almacenar las imágenes de las comidas compradas
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var backgroundMusic: MediaPlayer
    private lateinit var clickSound: MediaPlayer
    private lateinit var lamparaSound: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)

        // Inicializar sonidos
        backgroundMusic = MediaPlayer.create(this, R.raw.fondo)
        backgroundMusic.isLooping = true
        //backgroundMusic.start()

        clickSound = MediaPlayer.create(this, R.raw.click)
        lamparaSound = MediaPlayer.create(this, R.raw.luz)


        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val txtCoins = findViewById<TextView>(R.id.txtCoins)
        val btnComida = findViewById<ImageButton>(R.id.Comida) // ImageButton de Comida
        val imageView = findViewById<ImageView>(R.id.imageView)
        val btnSettings = findViewById<ImageButton>(R.id.btnsettings)
        val btnMascota = findViewById<ImageButton>(R.id.mascosta)
        val btnLampara = findViewById<ImageButton>(R.id.lampara)
        val darkOverlay = findViewById<View>(R.id.darkOverlay)
        val btnnota = findViewById<ImageButton>(R.id.btnota)
        val btnLeft = findViewById<ImageButton>(R.id.buttonLeft2)
        val btnRight = findViewById<ImageButton>(R.id.buttonRight2)
        val menuTiendaBtn = findViewById<ImageButton>(R.id.menuTienda)
        val menuDesplegable = findViewById<LinearLayout>(R.id.menuDesplegable)
        val btnTienda = findViewById<ImageButton>(R.id.Tienda)
        val selectedBackground = sharedPreferences.getInt("selectedBackground", R.drawable.fondo_fuego_1) // Recuperar valores de SharedPreferences

        mascotaManager = MascotaManager(this, imageView)

        // 🔹 Recuperar monedas guardadas
        monedas = sharedPreferences.getInt("monedas", 200)
        txtCoins.text = monedas.toString()

        // Establecer el fondo
        mainLayout.setBackgroundResource(selectedBackground)
        mascotaManager.actualizarImagenMascota()
        mascotaManager.configurarCarino()

        val comidasGuardadas = sharedPreferences.getStringSet("comidasCompradas", mutableSetOf()) ?: mutableSetOf()
        comidasCompradas.addAll(comidasGuardadas.map { it.toInt() })

        // Verificar si es la primera vez que se inicia la app
        if (comidasGuardadas.isEmpty()) {
            val comidaPorDefecto = R.drawable.comida_default
            comidasCompradas.add(comidaPorDefecto)

            // Guardar en SharedPreferences
            val editor = sharedPreferences.edit()
            val nuevaSet = mutableSetOf(comidaPorDefecto.toString())
            editor.putStringSet("comidasCompradas", nuevaSet)
            editor.apply()
        }

        btnComida.setImageResource(comidasCompradas.last()) // Mostrar la última comida comprada

        // Al presionar el botón de izquierda, cambia la imagen a la anterior
        btnLeft.setOnClickListener {
            clickSound.start()
            if (comidasCompradas.isNotEmpty()) {
                currentImageIndex = if (currentImageIndex > 0) currentImageIndex - 1 else comidasCompradas.size - 1
                btnComida.setImageResource(comidasCompradas[currentImageIndex])
            }
        }

        btnRight.setOnClickListener {
            clickSound.start()
            if (comidasCompradas.isNotEmpty()) {
                currentImageIndex = (currentImageIndex + 1) % comidasCompradas.size
                btnComida.setImageResource(comidasCompradas[currentImageIndex])
            }
        }

        btnSettings.setOnClickListener {
            clickSound.start()
            val intent = Intent(this, settingsActivity::class.java)
            startActivity(intent)
        }
        btnMascota.setOnClickListener {
            clickSound.start()
            val intent = Intent(this, mascotaActivity::class.java)
            startActivity(intent)
        }
        btnnota.setOnClickListener {
            clickSound.start()
            val intent = Intent(this, notaActivity::class.java)
            startActivity(intent)
        }

        btnComida.setOnClickListener {
            //clickSound.start()
            if (comidasCompradas.isNotEmpty()) {
                val currentComidaId = comidasCompradas[currentImageIndex]
                mascotaManager.alimentarMascota(currentComidaId)

                // Si es un potenciador, eliminarlo del carrusel
                if (currentComidaId == R.drawable.potenciador_1 ||
                    currentComidaId == R.drawable.potenciador_2 ||
                    currentComidaId == R.drawable.potenciador_3 ||
                    currentComidaId == R.drawable.potenciador_4) {

                    comidasCompradas.removeAt(currentImageIndex)

                    // Ajustar índice si es necesario
                    if (currentImageIndex >= comidasCompradas.size) {
                        currentImageIndex = comidasCompradas.size - 1
                    }

                    // Actualizar imagen del carrusel si queda alguna comida
                    if (comidasCompradas.isNotEmpty()) {
                        btnComida.setImageResource(comidasCompradas[currentImageIndex])
                    } else {
                        btnComida.setImageResource(R.drawable.comida_default)
                    }

                    // Mostrar mensaje al usuario
                    Toast.makeText(this, "¡Potenciador usado!", Toast.LENGTH_SHORT).show()

                    // Guardar cambios
                    val editor = sharedPreferences.edit()
                    editor.putStringSet("comidasCompradas", comidasCompradas.map { it.toString() }.toSet())
                    editor.apply()
                }
            }
        }


        btnLampara.setOnClickListener {
            lamparaSound.start()
            if (darkOverlay.visibility == View.VISIBLE) {
                darkOverlay.visibility = View.GONE // Apagar "modo noche"
                mascotaManager.encenderLampara() // Restaurar imagen normal
            } else {
                darkOverlay.visibility = View.VISIBLE // Encender "modo noche"
                mascotaManager.mostrarMascotaDurmiendo() // Mantener dormido mientras la luz esté apagada
            }
        }

        btnTienda.setOnClickListener {
            clickSound.start()
            val intent = Intent(this, tiendaActivity::class.java)
            intent.putExtra("monedas", monedas)
            startActivityForResult(intent, 1)
        }

        menuTiendaBtn.setOnClickListener {
            clickSound.start()
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
            val slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)

            if (menuDesplegable.visibility == View.VISIBLE) {
                menuDesplegable.startAnimation(slideOut)
                menuDesplegable.postDelayed({
                    menuDesplegable.visibility = View.GONE
                }, 300) // Duración de la animación
            } else {
                menuDesplegable.visibility = View.VISIBLE
                menuDesplegable.startAnimation(slideIn)
            }
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


    override fun onDestroy() {
        super.onDestroy()
        if (::backgroundMusic.isInitialized) {
            backgroundMusic.stop()
            backgroundMusic.release()
        }
        if (::clickSound.isInitialized) {
            clickSound.release()
        }
    }



}
