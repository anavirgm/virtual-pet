package principal

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.amiibot.R

class tiendaActivity : AppCompatActivity() {

    private var monedas = 200  // Valor inicial de las monedas
    private lateinit var compraSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda)

        compraSound = MediaPlayer.create(this, R.raw.compra)


        // Recuperar el valor de las monedas pasadas desde homeActivity
        monedas = intent.getIntExtra("monedas", 200)

        val txtCoins = findViewById<TextView>(R.id.txtCoins)
        txtCoins.text = monedas.toString()

        // Referencias a las ofertas especiales
        val comida1_oferta = findViewById<ImageView>(R.id.comida_1_oferta)
        val precio1_oferta = findViewById<TextView>(R.id.precio_comida_1_oferta)
        val comida2_oferta = findViewById<ImageView>(R.id.comida_2_oferta)
        val precio2_oferta = findViewById<TextView>(R.id.precio_comida_2_oferta)
        val comida3_oferta = findViewById<ImageView>(R.id.comida_3_oferta)
        val precio3_oferta = findViewById<TextView>(R.id.precio_comida_3_oferta)
        val comida4_oferta = findViewById<ImageView>(R.id.comida_4_oferta)
        val precio4_oferta = findViewById<TextView>(R.id.precio_comida_4_oferta)

        // Referencias a las comidas
        val comida1 = findViewById<ImageView>(R.id.comida_1)
        val precio1 = findViewById<TextView>(R.id.precio_comida_1)

        val comida2 = findViewById<ImageView>(R.id.comida_2)
        val precio2 = findViewById<TextView>(R.id.precio_comida_2)

        val comida3 = findViewById<ImageView>(R.id.comida_3)
        val precio3 = findViewById<TextView>(R.id.precio_comida_3)

        val comida4 = findViewById<ImageView>(R.id.comida_4)
        val precio4 = findViewById<TextView>(R.id.precio_comida_4)

        val comida5 = findViewById<ImageView>(R.id.comida_5)
        val precio5 = findViewById<TextView>(R.id.precio_comida_5)

        val comida6 = findViewById<ImageView>(R.id.comida_6)
        val precio6 = findViewById<TextView>(R.id.precio_comida_6)

        val comida7 = findViewById<ImageView>(R.id.comida_7)
        val precio7 = findViewById<TextView>(R.id.precio_comida_7)

        val comida8 = findViewById<ImageView>(R.id.comida_8)
        val precio8 = findViewById<TextView>(R.id.precio_comida_8)

        // Referencia a los potenciadores
        val potenciador1 = findViewById<ImageView>(R.id.potenciador_1)
        val precio_potenciador1 = findViewById<TextView>(R.id.precio_potenciador_1)

        val potenciador2 = findViewById<ImageView>(R.id.potenciador_2)
        val precio_potenciador2 = findViewById<TextView>(R.id.precio_potenciador_2)

        val potenciador3 = findViewById<ImageView>(R.id.potenciador_3)
        val precio_potenciador3 = findViewById<TextView>(R.id.precio_potenciador_3)

        val potenciador4 = findViewById<ImageView>(R.id.potenciador_4)
        val precio_potenciador4 = findViewById<TextView>(R.id.precio_potenciador_4)


        // Manejo de compra
        comida1.setOnClickListener { comprarComida(precio1.text.toString().toInt(), txtCoins, R.drawable.comida_5) }
        comida2.setOnClickListener { comprarComida(precio2.text.toString().toInt(), txtCoins, R.drawable.comida_6) }
        comida3.setOnClickListener { comprarComida(precio3.text.toString().toInt(), txtCoins, R.drawable.comida_7) }
        comida4.setOnClickListener { comprarComida(precio4.text.toString().toInt(), txtCoins, R.drawable.comida_8) }
        comida5.setOnClickListener { comprarComida(precio5.text.toString().toInt(), txtCoins, R.drawable.comida_9) }
        comida6.setOnClickListener { comprarComida(precio6.text.toString().toInt(), txtCoins, R.drawable.comida_10) }
        comida7.setOnClickListener { comprarComida(precio7.text.toString().toInt(), txtCoins, R.drawable.comida_11) }
        comida8.setOnClickListener { comprarComida(precio8.text.toString().toInt(), txtCoins, R.drawable.comida_12) }

        comida1_oferta.setOnClickListener { comprarComida(precio1_oferta.text.toString().toInt(), txtCoins, R.drawable.comida_1_oferta) }
        comida2_oferta.setOnClickListener { comprarComida(precio2_oferta.text.toString().toInt(), txtCoins, R.drawable.comida_2_oferta) }
        comida3_oferta.setOnClickListener { comprarComida(precio3_oferta.text.toString().toInt(), txtCoins, R.drawable.comida_3_oferta) }
        comida4_oferta.setOnClickListener { comprarComida(precio4_oferta.text.toString().toInt(), txtCoins, R.drawable.comida_4_oferta) }

        potenciador1.setOnClickListener {comprarComida(precio_potenciador1.text.toString().toInt(), txtCoins, R.drawable.potenciador_1) }
        potenciador2.setOnClickListener {comprarComida(precio_potenciador2.text.toString().toInt(), txtCoins, R.drawable.potenciador_2) }
        potenciador3.setOnClickListener {comprarComida(precio_potenciador3.text.toString().toInt(), txtCoins, R.drawable.potenciador_3) }
        potenciador4.setOnClickListener {comprarComida(precio_potenciador4.text.toString().toInt(), txtCoins, R.drawable.potenciador_4) }
    }

    private fun comprarComida(precio: Int, txtCoins: TextView, comidaId: Int) {
        if (monedas >= precio) {
            monedas -= precio
            txtCoins.text = monedas.toString()
            Toast.makeText(this, "Compra realizada!", Toast.LENGTH_SHORT).show()
            compraSound.start()

            // Guardar comida en SharedPreferences
            val sharedPreferences = getSharedPreferences("MascotaPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val comidasGuardadas = sharedPreferences.getStringSet("comidasCompradas", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            comidasGuardadas.add(comidaId.toString()) // Guardamos el ID como String
            editor.putStringSet("comidasCompradas", comidasGuardadas)
            editor.apply()

            // Devolver resultado a homeActivity con la comida comprada
            val resultIntent = Intent()
            resultIntent.putExtra("monedasRestantes", monedas)
            resultIntent.putExtra("comidaComprada", comidaId)
            setResult(RESULT_OK, resultIntent)
        } else {
            Toast.makeText(this, "No tienes suficientes monedas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compraSound.release()
    }


}