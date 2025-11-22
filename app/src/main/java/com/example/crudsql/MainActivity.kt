package com.example.crudsql

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTodos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador con la lista actual y la lógica de borrado
        todoAdapter = TodoAdapter(dbHelper.getAllTodos()) { idToDelete ->
            deleteTask(idToDelete)
        }
        recyclerView.adapter = todoAdapter

        // Configurar botón flotante (FAB)
        val fab: FloatingActionButton = findViewById(R.id.fabAdd)
        fab.setOnClickListener {
            showAddDialog()
        }
    }

    // Función para mostrar el diálogo de agregar tarea
    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_todo, null)
        val etNewTask = dialogView.findViewById<EditText>(R.id.etNewTask)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val taskText = etNewTask.text.toString()
                if (taskText.isNotEmpty()) {
                    addTask(taskText)
                } else {
                    Toast.makeText(this, "La tarea no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    // Lógica para guardar en SQLite y actualizar la UI
    private fun addTask(task: String) {
        val result = dbHelper.addTodo(task)
        if (result > -1) {
            // Refrescar la lista del adaptador
            todoAdapter.refreshData(dbHelper.getAllTodos())
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    // Lógica para borrar de SQLite y actualizar la UI
    private fun deleteTask(id: Int) {
        dbHelper.deleteTodo(id)
        todoAdapter.refreshData(dbHelper.getAllTodos())
        Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
    }
}