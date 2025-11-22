package com.example.crudsql

// Clase de datos simple para representar una tarea
data class Todo(
    val id: Int,      // ID único para la base de datos
    val task: String  // El texto de la tarea
)