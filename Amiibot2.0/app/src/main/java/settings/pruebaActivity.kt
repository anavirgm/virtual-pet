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
import principal.inicioActivity

class pruebaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prueba)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnregreso = findViewById<FloatingActionButton>(R.id.btnregreso)

        btnregreso.setOnClickListener {
            val intent = Intent(this, inicioActivity::class.java)
            startActivity(intent)
        }

        fun regreso() {
            val intent = Intent(this, inicioActivity::class.java)
            startActivity(intent)
        }


    }
}