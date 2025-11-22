package com.example.crudsql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private var todoList: List<Todo>,
    private val onDeleteClick: (Int) -> Unit // Lambda para manejar el clic en borrar
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    // Clase interna para referenciar las vistas del item_todo.xml
    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTask: TextView = view.findViewById(R.id.tvTask)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    // Crea la vista inflando el XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    // Asigna los datos a las vistas
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.tvTask.text = todo.task

        // Configurar el click del botón borrar
        holder.btnDelete.setOnClickListener {
            onDeleteClick(todo.id)
        }
    }

    override fun getItemCount(): Int = todoList.size

    // Método auxiliar para actualizar la lista desde la Activity
    fun refreshData(newList: List<Todo>) {
        todoList = newList
        notifyDataSetChanged()
    }
}