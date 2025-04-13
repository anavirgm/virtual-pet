package notas

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amiibot.R
import java.io.File

class NoteListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        recyclerView = findViewById(R.id.noteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val notes = getNotesFromStorage()
        noteAdapter = NoteAdapter(notes)
        recyclerView.adapter = noteAdapter
    }

    private fun getNotesFromStorage(): List<File> {
        val notesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return notesDir?.listFiles()?.filter { it.isFile && it.name.endsWith(".txt") } ?: emptyList()
    }

    private inner class NoteAdapter(private val notes: List<File>) :
        RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

        inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val noteNameTextView: TextView = itemView.findViewById(R.id.noteNameTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_list_item, parent, false)
            return NoteViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            val note = notes[position]
            holder.noteNameTextView.text = note.name
        }

        override fun getItemCount() = notes.size
    }
}