package com.example.crudsql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private var todoList: List<Todo>,
    private val onEditClick: (Todo) -> Unit,   // NUEVO: Lambda para editar recibe todo el objeto Todo
    private val onDeleteClick: (Int) -> Unit   // Lambda para borrar recibe solo ID
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTask: TextView = view.findViewById(R.id.tvTask)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)     // Referencia al botón editar
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.tvTask.text = todo.task

        // Configurar click de editar (NUEVO)
        holder.btnEdit.setOnClickListener {
            onEditClick(todo)
        }

        // Configurar click de borrar
        holder.btnDelete.setOnClickListener {
            onDeleteClick(todo.id)
        }
    }

    override fun getItemCount(): Int = todoList.size

    fun refreshData(newList: List<Todo>) {
        todoList = newList
        notifyDataSetChanged()
    }
}