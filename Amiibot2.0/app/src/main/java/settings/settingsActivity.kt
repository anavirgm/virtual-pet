package settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.amiibot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import principal.homeActivity
import principal.inicioActivity

class settingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnregreso = findViewById<FloatingActionButton>(R.id.btnback)
        val btnCuenta = findViewById<Button>(R.id.btnCuenta)
        val btnNotificaciones = findViewById<Button>(R.id.btnNotificaciones)
        val btnSonido = findViewById<Button>(R.id.btnSonido)
        val btnAccesibilidad = findViewById<Button>(R.id.btnAccesibilidad)
        val btnOpciones = findViewById<Button>(R.id.btnOpciones)
        val btnAsistencia = findViewById<Button>(R.id.btnAsistencia)
        val btnSaber = findViewById<Button>(R.id.btnSaber)

        btnregreso.setOnClickListener {
            val intent = Intent(this, homeActivity::class.java)
            startActivity(intent)
        }
        btnCuenta.setOnClickListener {
            val intent = Intent(this, CuentaActivity::class.java)
            startActivity(intent)
        }
        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }
        btnSonido.setOnClickListener {
            val intent = Intent(this, SonidoActivity::class.java)
            startActivity(intent)
        }
        btnAccesibilidad.setOnClickListener {
            val intent = Intent(this, AccesibilidadActivity::class.java)
            startActivity(intent)
        }
        btnOpciones.setOnClickListener {
            val intent = Intent(this, OpcionesActivity::class.java)
            startActivity(intent)
        }
        btnAsistencia.setOnClickListener {
            val intent = Intent(this, AsistenciaActivity::class.java)
            startActivity(intent)
        }
        btnSaber.setOnClickListener {
            val intent = Intent(this, SaberActivity::class.java)
            startActivity(intent)
        }



    }
}