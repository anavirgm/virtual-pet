package principal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.amiibot.R

class tiendaActivity : AppCompatActivity() {

    private var monedas = 200  // Valor inicial de las monedas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda)

        // Recuperar el valor de las monedas pasadas desde homeActivity
        monedas = intent.getIntExtra("monedas", 200)

        val txtCoins = findViewById<TextView>(R.id.txtCoins)
        txtCoins.text = monedas.toString()

        // Referencias a las comidas
        val comida1 = findViewById<ImageView>(R.id.comida_1)
        val precio1 = findViewById<TextView>(R.id.precio_comida_1)

        val comida2 = findViewById<ImageView>(R.id.comida_2)
        val precio2 = findViewById<TextView>(R.id.precio_comida_2)

        // Manejo de compra
        comida1.setOnClickListener { comprarComida(precio1.text.toString().toInt(), txtCoins, R.drawable.comida_5) }
        comida2.setOnClickListener { comprarComida(precio2.text.toString().toInt(), txtCoins, R.drawable.comida_6) }
    }

    private fun comprarComida(precio: Int, txtCoins: TextView, comidaId: Int) {
        if (monedas >= precio) {
            monedas -= precio
            txtCoins.text = monedas.toString()
            Toast.makeText(this, "Compra realizada!", Toast.LENGTH_SHORT).show()

            // Devolver resultado a homeActivity con la comida comprada
            val resultIntent = Intent()
            resultIntent.putExtra("monedasRestantes", monedas)
            resultIntent.putExtra("comidaComprada", comidaId)  // Enviar el ID de la comida comprada
            setResult(RESULT_OK, resultIntent)

        } else {
            Toast.makeText(this, "No tienes suficientes monedas", Toast.LENGTH_SHORT).show()
        }
    }
}