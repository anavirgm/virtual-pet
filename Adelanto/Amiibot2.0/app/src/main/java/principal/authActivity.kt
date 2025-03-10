package principal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.amiibot.R
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class authActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Verifica si ya se guardó la fecha de nacimiento
        val savedYear = sharedPreferences.getString("year_of_birth", null)
        if (savedYear != null) {
            goToInicioActivity() // Si ya hay un año guardado, ir a InicioActivity
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null || currentUser.isAnonymous) { // Evitamos iniciar sesión si ya es anónimo
            Handler(Looper.getMainLooper()).postDelayed({
                auth.signInAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            Toast.makeText(this, "Sesión iniciada como ${user.uid}", Toast.LENGTH_SHORT).show()
                            setupCardViewListeners() // Se ejecuta solo después de autenticarse
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }, 500)
        } else {
            setupCardViewListeners() // Si ya hay sesión, configurar los listeners
        }
    }

    private fun setupCardViewListeners() {
        val cardViews = listOf(
            R.id.card_2000, R.id.card_2001, R.id.card_2002, R.id.card_2003, R.id.card_2004,
            R.id.card_2005, R.id.card_2006, R.id.card_2007, R.id.card_2008, R.id.card_2009,
            R.id.card_2010, R.id.card_2011, R.id.card_2012, R.id.card_2013, R.id.card_2014,
            R.id.card_2015, R.id.card_2016, R.id.card_2017, R.id.card_2018, R.id.card_2019,
            R.id.card_2020
        )

        for (cardId in cardViews) {
            val cardView = findViewById<CardView>(cardId)
            val textView = cardView.getChildAt(0) as TextView
            val year = textView.text.toString()

            cardView.setOnClickListener {
                saveYearToFirebase(year)
            }
        }
    }

    private fun saveYearToFirebase(year: String) {
        val user: FirebaseUser? = auth.currentUser

        if (user == null) {
            Toast.makeText(this, "No se encontró usuario autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid
        val userMap = hashMapOf("year_of_birth" to year)

        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                // Guardar en SharedPreferences para que no vuelva a preguntar
                sharedPreferences.edit().putString("year_of_birth", year).apply()
                Toast.makeText(this, "Año guardado con éxito", Toast.LENGTH_SHORT).show()

                // Ir a InicioActivity
                goToInicioActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar el año: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToInicioActivity() {
        val intent = Intent(this, inicioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Elimina la actividad actual del stack
        startActivity(intent)
        finish()
    }
}
