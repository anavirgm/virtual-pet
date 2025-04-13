package notas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import notas.backend.GeminiAPIClient
import com.example.amiibot.R
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class notaActivity : AppCompatActivity() {

    private lateinit var apiClient: GeminiAPIClient
    private lateinit var promptEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var saveNoteButton: Button
    private lateinit var newNoteButton: Button
    private lateinit var viewNotesButton: Button

    private var currentNoteContent = "" // Almacena el contenido de la nota actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota)

        // Reemplaza "TU_CLAVE_API" con tu clave de API real (solo para pruebas)
        val apiKey = "AIzaSyDec8VIcEP28v4xgFuU73OE6a3ZbOHblHs"
        apiClient = GeminiAPIClient(apiKey)

        promptEditText = findViewById(R.id.promptEditText)
        resultTextView = findViewById(R.id.resultTextView)
        val generateButton: Button = findViewById(R.id.generateButton)
        saveNoteButton = findViewById(R.id.saveNoteButton)
        newNoteButton = findViewById(R.id.newNoteButton)
        viewNotesButton = findViewById(R.id.vernotasButton)

        viewNotesButton.setOnClickListener {
            mostrarNotas() // Llamamos a la función para mostrar las notas
        }

        generateButton.setOnClickListener {
            val userInput = promptEditText.text.toString()
            if (userInput.isEmpty()) {
                resultTextView.text = "Por favor ingrese texto"
            } else {
                val prompt = "da el contenido que te pide: $userInput"

                apiClient.generateText(prompt) { result ->
                    runOnUiThread {
                        if (result != null) {
                            resultTextView.text = result
                            currentNoteContent += "\nPregunta: $userInput\nRespuesta: $result" // Agrega la pregunta y respuesta a la nota actual
                        } else {
                            resultTextView.text = "Error al generar el texto"
                        }
                    }
                }
            }
        }
        saveNoteButton.setOnClickListener {
            guardarNota()
        }

        newNoteButton.setOnClickListener {
            nuevaNota()
        }
    }

    private fun mostrarNotas() {
        val notes = getNotesFromStorage()
        if (notes.isEmpty()) {
            Toast.makeText(this, "No hay notas guardadas", Toast.LENGTH_SHORT).show()
            return
        }

        val noteNames = notes.map { it.name }.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Notas Guardadas")

        val listView = ListView(this)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, noteNames)
        builder.setView(listView)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedNote = notes[position]
            mostrarContenidoEnEditor(selectedNote)
        }

        builder.setNegativeButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarContenidoEnEditor(noteFile: File) {
        try {
            val content = noteFile.readText()
            resultTextView.setText(content)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al leer la nota", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarNota() {
        if (currentNoteContent.isEmpty()) {
            Toast.makeText(this, "No hay contenido para guardar", Toast.LENGTH_SHORT).show()
            return;
        }

        val fileName = "Nota_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".txt"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            val writer = FileWriter(file)
            writer.write(currentNoteContent)
            writer.close()
            Toast.makeText(this, "Nota guardada como: $fileName", Toast.LENGTH_SHORT).show()
            currentNoteContent = ""
            resultTextView.text = ""
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show()
        }
    }

    private fun nuevaNota() {
        currentNoteContent = ""
        resultTextView.text = ""
        promptEditText.text.clear()
        Toast.makeText(this, "Nueva nota creada", Toast.LENGTH_SHORT).show()
    }

    private fun getNotesFromStorage(): List<File> {
        val notesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return notesDir?.listFiles()?.filter { it.isFile && it.name.endsWith(".txt") } ?: emptyList()
    }
}