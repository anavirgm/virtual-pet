package principal

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.amiibot.R

class MascotaManager(private val context: Context, private val mascotaImage: ImageView) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)
    private var handler = Handler(Looper.getMainLooper())

    var xp = 0
    var hambre = 100
    var animo = 100
    var energia = 100
    var nivel = 1
    var baseMascota = R.drawable.fuego
    private var isHolding = false
    private var isLampOff = false

    init {
        cargarDatos()
        actualizarImagenMascota()
    }

    fun mostrarMascotaDurmiendo() {
        isLampOff = true // 🔹 Indicar que la lámpara está apagada
        mascotaImage.setImageResource(getEmocionMascota("sueno"))

        handler.post(object : Runnable {
            override fun run() {
                if (isLampOff && energia < 100) { // 🔹 Mantener dormido mientras la energía no sea 100%
                    mascotaImage.setImageResource(getEmocionMascota("sueno"))
                    recargarEnergia() // Recuperar energía gradualmente
                    handler.postDelayed(this, 1000) // Revisar cada segundo
                }
            }
        })
    }


    fun actualizarImagenMascota() {
        if (isLampOff && energia < 100) return // 🔹 No actualizar la imagen si está en "modo noche"

        val imagenMascota = when {
            isHolding -> getEmocionMascota("carino")
            energia < 30 -> getEmocionMascota("triste")
            hambre < 50 -> getEmocionMascota("hambre")
            animo >= 80 -> getEmocionMascota("feliz")
            animo < 50 -> getEmocionMascota("enojado")
            else -> baseMascota
        }
        mascotaImage.setImageResource(imagenMascota)
    }

    fun encenderLampara() {
        isLampOff = false // 🔹 Indicar que la lámpara está encendida
        actualizarImagenMascota() // Restaurar la imagen normal
    }

    fun getEmocionMascota(emocion: String): Int {
        return when (baseMascota) {
            R.drawable.fuego, R.drawable.fuego_2 -> when (emocion) {
                "triste" -> if (baseMascota == R.drawable.fuego) R.drawable.fuego_triste else R.drawable.fuego_triste_2
                "feliz" -> if (baseMascota == R.drawable.fuego) R.drawable.fuego_feliz else R.drawable.fuego_feliz_2
                "enojado" -> if (baseMascota == R.drawable.fuego) R.drawable.fuego_enojado else R.drawable.fuego_enojado_2
                "hambre" -> if (baseMascota == R.drawable.fuego) R.drawable.fuego_hambre else R.drawable.fuego_hambre_2
                "sueno" -> if (baseMascota == R.drawable.fuego) R.drawable.fuego_sueno else R.drawable.fuego_sueno_2
                "carino" -> if (baseMascota == R.drawable.fuego) R.drawable.fuego_carino else R.drawable.fuego_carino_2
                else -> baseMascota
            }
            R.drawable.electrico, R.drawable.electrico_2 -> when (emocion) {
                "triste" -> if (baseMascota == R.drawable.electrico) R.drawable.electrico_triste else R.drawable.electrico_triste//_2
                "feliz" -> if (baseMascota == R.drawable.electrico) R.drawable.electrico_feliz else R.drawable.electrico_feliz//_2
                "enojado" -> if (baseMascota == R.drawable.electrico) R.drawable.electrico_enojado else R.drawable.electrico_enojado//_2
                "hambre" -> if (baseMascota == R.drawable.electrico) R.drawable.electrico_hambre else R.drawable.electrico_hambre//_2
                "sueno" -> if (baseMascota == R.drawable.electrico) R.drawable.electrico_sueno else R.drawable.electrico_sueno//_2
                "carino" -> if (baseMascota == R.drawable.electrico) R.drawable.electrico_carino else R.drawable.electrico_carino//_2
                else -> baseMascota
            }
            R.drawable.agua, R.drawable.agua_2 -> when (emocion) {
                "triste" -> if (baseMascota == R.drawable.agua) R.drawable.agua_triste else R.drawable.agua_triste_2
                "feliz" -> if (baseMascota == R.drawable.agua) R.drawable.agua_feliz else R.drawable.agua_feliz_2
                "enojado" -> if (baseMascota == R.drawable.agua) R.drawable.agua_enojado else R.drawable.agua_enojado_2
                "hambre" -> if (baseMascota == R.drawable.agua) R.drawable.agua_hambre else R.drawable.agua_hambre_2
                "sueno" -> if (baseMascota == R.drawable.agua) R.drawable.agua_sueno else R.drawable.agua_sueno_2
                "carino" -> if (baseMascota == R.drawable.agua) R.drawable.agua_carino else R.drawable.agua_carino_2
                else -> baseMascota
            }
            else -> baseMascota
        }
    }

    private fun cargarDatos() {
        xp = sharedPreferences.getInt("xp", 0)
        hambre = sharedPreferences.getInt("hambre", 70)
        animo = sharedPreferences.getInt("animo", 70)
        energia = sharedPreferences.getInt("energia", 70)
        nivel = sharedPreferences.getInt("nivel", 1) // Recuperar nivel guardado
        baseMascota = sharedPreferences.getInt("selectedImage", R.drawable.fuego)
    }


    fun guardarDatos() {
        val editor = sharedPreferences.edit()
        editor.putInt("xp", xp)
        editor.putInt("hambre", hambre)
        editor.putInt("animo", animo)
        editor.putInt("energia", energia)
        editor.putInt("nivel", nivel)
        editor.putInt("selectedImage", baseMascota) // Guardar la evolución
        editor.apply()
    }



    fun iniciarReduccionAutomatica() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (hambre > 0) hambre -= 1
                if (animo > 0) animo -= 1
                if (energia > 0) energia -= 1

                if (hambre < 0) hambre = 0
                if (animo < 0) animo = 0
                if (energia < 0) energia = 0

                guardarDatos()
                actualizarImagenMascota()

                handler.postDelayed(this, 30000)
            }
        }, 30000)
    }


    fun actualizarXP() {
        if (hambre == 100 && animo == 100 && energia == 100) {
            xp += 50
        } else {
            xp += 10
        }

        if (xp >= 100) {
            xp = 0
            nivel += 1
            verificarEvolucion() // Verificar si evoluciona al subir de nivel
        }

        guardarDatos()

        (context as? mascotaActivity)?.runOnUiThread {
            (context.findViewById<ProgressBar>(R.id.progressBar1))?.progress = xp
            (context.findViewById<TextView>(R.id.textView1))?.text = "Nivel: $nivel"
        }
    }



    fun alimentarMascota() {
        if (hambre < 100) {
            hambre += 10
            if (hambre > 100) hambre = 100
            actualizarXP()
            guardarDatos()
            actualizarImagenMascota()
        }
    }

    fun configurarCarino() {
        mascotaImage.setOnLongClickListener {
            isHolding = true
            mascotaImage.setImageResource(getEmocionMascota("carino"))

            if (animo < 100) {
                animo += 10
                if (animo > 100) animo = 100
                actualizarXP()
                guardarDatos()
                actualizarImagenMascota()
            }
            true
        }

        mascotaImage.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && isHolding) {
                isHolding = false
                actualizarImagenMascota()
            }
            false
        }
    }

    fun recargarEnergia() {
        val incremento = 10
        val intervalo = 3000L

        handler.post(object : Runnable {
            override fun run() {
                if (energia < 100) {
                    energia += incremento
                    if (energia > 100) energia = 100
                    actualizarXP()
                    guardarDatos()
                    actualizarImagenMascota()

                    (context as? mascotaActivity)?.runOnUiThread {
                        (context.findViewById<ProgressBar>(R.id.progressBar4))?.progress = energia
                    }

                    handler.postDelayed(this, intervalo)
                }
            }
        })
    }


    fun verificarEvolucion() {
        when (nivel) {
            2 -> baseMascota = when (baseMascota) {
                R.drawable.fuego -> R.drawable.fuego_2
                R.drawable.electrico -> R.drawable.electrico_2
                R.drawable.agua -> R.drawable.agua_2
                else -> baseMascota
            }
            3 -> baseMascota = when (baseMascota) {
                R.drawable.fuego_2 -> R.drawable.fuego_3
                R.drawable.electrico_2 -> R.drawable.electrico_3
                R.drawable.agua_2 -> R.drawable.agua_3
                else -> baseMascota
            }
        }
        guardarDatos()
        actualizarImagenMascota()
    }



    /*

    fun getEmocionMascota(emocion: String): Int {
        return when (baseMascota) {
            R.drawable.fuego, R.drawable.fuego_2, R.drawable.fuego_3 -> when (emocion) {
                "triste" -> if (nivel >= 3) R.drawable.fuego_triste_3 else if (nivel >= 2) R.drawable.fuego_triste_2 else R.drawable.fuego_triste
                "feliz" -> if (nivel >= 3) R.drawable.fuego_feliz_3 else if (nivel >= 2) R.drawable.fuego_feliz_2 else R.drawable.fuego_feliz
                "enojado" -> if (nivel >= 3) R.drawable.fuego_enojado_3 else if (nivel >= 2) R.drawable.fuego_enojado_2 else R.drawable.fuego_enojado
                "hambre" -> if (nivel >= 3) R.drawable.fuego_hambre_3 else if (nivel >= 2) R.drawable.fuego_hambre_2 else R.drawable.fuego_hambre
                "sueno" -> if (nivel >= 3) R.drawable.fuego_sueno_3 else if (nivel >= 2) R.drawable.fuego_sueno_2 else R.drawable.fuego_sueno
                "carino" -> if (nivel >= 3) R.drawable.fuego_carino_3 else if (nivel >= 2) R.drawable.fuego_carino_2 else R.drawable.fuego_carino
                else -> baseMascota
            }
            R.drawable.electrico, R.drawable.electrico_2, R.drawable.electrico_3 -> when (emocion) {
                "triste" -> if (nivel >= 3) R.drawable.electrico_triste_3 else if (nivel >= 2) R.drawable.electrico_triste_2 else R.drawable.electrico_triste
                "feliz" -> if (nivel >= 3) R.drawable.electrico_feliz_3 else if (nivel >= 2) R.drawable.electrico_feliz_2 else R.drawable.electrico_feliz
                "enojado" -> if (nivel >= 3) R.drawable.electrico_enojado_3 else if (nivel >= 2) R.drawable.electrico_enojado_2 else R.drawable.electrico_enojado
                "hambre" -> if (nivel >= 3) R.drawable.electrico_hambre_3 else if (nivel >= 2) R.drawable.electrico_hambre_2 else R.drawable.electrico_hambre
                "sueno" -> if (nivel >= 3) R.drawable.electrico_sueno_3 else if (nivel >= 2) R.drawable.electrico_sueno_2 else R.drawable.electrico_sueno
                "carino" -> if (nivel >= 3) R.drawable.electrico_carino_3 else if (nivel >= 2) R.drawable.electrico_carino_2 else R.drawable.electrico_carino
                else -> baseMascota
            }
            R.drawable.agua, R.drawable.agua_2, R.drawable.agua_3 -> when (emocion) {
                "triste" -> if (nivel >= 3) R.drawable.agua_triste_3 else if (nivel >= 2) R.drawable.agua_triste_2 else R.drawable.agua_triste
                "feliz" -> if (nivel >= 3) R.drawable.agua_feliz_3 else if (nivel >= 2) R.drawable.agua_feliz_2 else R.drawable.agua_feliz
                "enojado" -> if (nivel >= 3) R.drawable.agua_enojado_3 else if (nivel >= 2) R.drawable.agua_enojado_2 else R.drawable.agua_enojado
                "hambre" -> if (nivel >= 3) R.drawable.agua_hambre_3 else if (nivel >= 2) R.drawable.agua_hambre_2 else R.drawable.agua_hambre
                "sueno" -> if (nivel >= 3) R.drawable.agua_sueno_3 else if (nivel >= 2) R.drawable.agua_sueno_2 else R.drawable.agua_sueno
                "carino" -> if (nivel >= 3) R.drawable.agua_carino_3 else if (nivel >= 2) R.drawable.agua_carino_2 else R.drawable.agua_carino
                else -> baseMascota
            }
            else -> baseMascota
        }
    }
    */



}
