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

        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerViewTodos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar adaptador pasando AMBAS funciones (Editar y Borrar)
        todoAdapter = TodoAdapter(
            dbHelper.getAllTodos(),
            onEditClick = { todoToEdit ->
                showEditDialog(todoToEdit) // Llamamos al diálogo de edición
            },
            onDeleteClick = { idToDelete ->
                deleteTask(idToDelete)
            }
        )
        recyclerView.adapter = todoAdapter

        val fab: FloatingActionButton = findViewById(R.id.fabAdd)
        fab.setOnClickListener {
            showAddDialog()
        }
    }

    // Diálogo para CREAR (reutiliza el layout dialog_add_todo)
    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_todo, null)
        val etNewTask = dialogView.findViewById<EditText>(R.id.etNewTask)

        AlertDialog.Builder(this)
            .setTitle("Nueva Tarea")
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

    // NUEVO: Diálogo para EDITAR
    private fun showEditDialog(todo: Todo) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_todo, null)
        val etTask = dialogView.findViewById<EditText>(R.id.etNewTask)

        // Pre-llenamos el campo con el texto actual de la tarea
        etTask.setText(todo.task)
        // Movemos el cursor al final del texto
        etTask.setSelection(todo.task.length)

        AlertDialog.Builder(this)
            .setTitle("Editar Tarea") // Cambiamos el título
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val newTaskText = etTask.text.toString()
                if (newTaskText.isNotEmpty()) {
                    updateTask(todo.id, newTaskText)
                } else {
                    Toast.makeText(this, "La tarea no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun addTask(task: String) {
        val result = dbHelper.addTodo(task)
        if (result > -1) {
            todoAdapter.refreshData(dbHelper.getAllTodos())
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }

    // NUEVO: Lógica para actualizar en BD
    private fun updateTask(id: Int, newTask: String) {
        val result = dbHelper.updateTodo(id, newTask)
        if (result > 0) {
            todoAdapter.refreshData(dbHelper.getAllTodos())
            Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(id: Int) {
        dbHelper.deleteTodo(id)
        todoAdapter.refreshData(dbHelper.getAllTodos())
        Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
    }
}