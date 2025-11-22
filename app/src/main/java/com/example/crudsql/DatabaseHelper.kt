package com.example.crudsql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TodoApp.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "todos"
        const val COLUMN_ID = "id"
        const val COLUMN_TASK = "task"
    }

    // Se llama cuando la base de datos se crea por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TASK TEXT)")
        db.execSQL(createTableQuery)
    }

    // Se llama cuando se actualiza la versión de la base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // CREATE: Insertar una nueva tarea
    fun addTodo(task: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK, task)

        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    // READ: Leer todas las tareas
    fun getAllTodos(): List<Todo> {
        val todoList = ArrayList<Todo>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val taskIndex = cursor.getColumnIndex(COLUMN_TASK)

                // Verificar que las columnas existan
                if (idIndex != -1 && taskIndex != -1) {
                    val id = cursor.getInt(idIndex)
                    val task = cursor.getString(taskIndex)
                    todoList.add(Todo(id, task))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return todoList
    }

    // DELETE: Borrar una tarea por ID
    fun deleteTodo(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }
}